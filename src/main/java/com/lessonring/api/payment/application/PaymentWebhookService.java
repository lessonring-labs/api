package com.lessonring.api.payment.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.payment.api.request.PaymentWebhookRequest;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.PaymentStatus;
import com.lessonring.api.payment.domain.PaymentWebhookLog;
import com.lessonring.api.payment.domain.repository.PaymentRepository;
import com.lessonring.api.payment.domain.repository.PaymentWebhookLogRepository;
import com.lessonring.api.payment.infrastructure.lock.PaymentStateLockManager;
import com.lessonring.api.payment.infrastructure.pg.PgPaymentStatusResponse;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentWebhookService {

    private static final String PROVIDER = "TOSS";
    private static final long WEBHOOK_LOCK_TIMEOUT_SECONDS = 3L;

    private final PaymentRepository paymentRepository;
    private final PaymentWebhookLogRepository paymentWebhookLogRepository;
    private final PaymentStateLockManager paymentStateLockManager;
    private final PaymentWebhookPgVerificationService paymentWebhookPgVerificationService;
    private final ObjectMapper objectMapper;

    @Transactional
    public void handle(
            String transmissionId,
            String signature,
            String rawBody,
            PaymentWebhookRequest request
    ) {
        validateRequest(request);

        if (isDuplicatedTransmission(transmissionId)) {
            log.info(
                    "duplicated payment webhook ignored. provider={}, transmissionId={}, eventType={}, orderId={}",
                    PROVIDER,
                    transmissionId,
                    request.getEventType(),
                    request.getOrderId()
            );
            return;
        }

        Payment payment = paymentRepository.findByPgOrderId(request.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        boolean locked = false;
        boolean unlockDeferred = false;

        try {
            locked = paymentStateLockManager.tryLock(
                    payment.getId(),
                    WEBHOOK_LOCK_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS
            );

            if (!locked) {
                throw new BusinessException(
                        ErrorCode.INVALID_REQUEST,
                        "이미 다른 결제 상태 변경 요청이 처리 중입니다."
                );
            }

            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                Long paymentId = payment.getId();
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        paymentStateLockManager.unlock(paymentId);
                    }
                });
                unlockDeferred = true;
            }

            Payment lockedPayment = paymentRepository.findById(payment.getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

            switch (request.getEventType()) {
                case "PAYMENT_COMPLETED" -> handleCompleted(lockedPayment, request);
                case "PAYMENT_FAILED" -> handleFailed(lockedPayment, request);
                case "PAYMENT_CANCELED" -> handleCanceled(lockedPayment, request);
                default -> {
                    log.warn(
                            "unsupported payment webhook ignored. provider={}, transmissionId={}, eventType={}, orderId={}",
                            PROVIDER,
                            transmissionId,
                            request.getEventType(),
                            request.getOrderId()
                    );
                    return;
                }
            }

            paymentWebhookLogRepository.save(
                    PaymentWebhookLog.create(
                            PROVIDER,
                            transmissionId,
                            request.getEventType(),
                            request.getOrderId(),
                            request.getPaymentKey(),
                            toPayload(rawBody, request)
                    )
            );

            log.info(
                    "payment webhook handled. provider={}, eventType={}, orderId={}, paymentId={}, transmissionId={}",
                    PROVIDER,
                    request.getEventType(),
                    request.getOrderId(),
                    lockedPayment.getId(),
                    transmissionId
            );

        } finally {
            if (locked && !unlockDeferred) {
                paymentStateLockManager.unlock(payment.getId());
            }
        }
    }

    private void handleCompleted(Payment payment, PaymentWebhookRequest request) {
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            log.info(
                    "payment webhook completed skipped - already completed. paymentId={}, orderId={}",
                    payment.getId(),
                    request.getOrderId()
            );
            return;
        }

        if (payment.getStatus() == PaymentStatus.CANCELED) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    "취소된 결제는 completed webhook으로 반영할 수 없습니다."
            );
        }

        PgPaymentStatusResponse pgResponse =
                paymentWebhookPgVerificationService.verifyCompleted(payment, request);

        payment.syncCompletedFromWebhook(
                request.getPaymentKey(),
                pgResponse.getRawResponse() != null ? pgResponse.getRawResponse() : request.getRawResponse()
        );
    }

    private void handleFailed(Payment payment, PaymentWebhookRequest request) {
        if (payment.getStatus() == PaymentStatus.FAILED) {
            log.info(
                    "payment webhook failed skipped - already failed. paymentId={}, orderId={}",
                    payment.getId(),
                    request.getOrderId()
            );
            return;
        }

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            log.info(
                    "payment webhook failed skipped - already completed. paymentId={}, orderId={}",
                    payment.getId(),
                    request.getOrderId()
            );
            return;
        }

        if (payment.getStatus() == PaymentStatus.CANCELED) {
            log.info(
                    "payment webhook failed skipped - already canceled. paymentId={}, orderId={}",
                    payment.getId(),
                    request.getOrderId()
            );
            return;
        }

        PgPaymentStatusResponse pgResponse =
                paymentWebhookPgVerificationService.verifyFailed(payment, request);

        payment.syncFailedFromWebhook(
                request.getFailureReason(),
                pgResponse.getRawResponse() != null ? pgResponse.getRawResponse() : request.getRawResponse()
        );
    }

    private void handleCanceled(Payment payment, PaymentWebhookRequest request) {
        if (payment.getStatus() == PaymentStatus.CANCELED) {
            log.info(
                    "payment webhook canceled skipped - already canceled. paymentId={}, orderId={}",
                    payment.getId(),
                    request.getOrderId()
            );
            return;
        }

        PgPaymentStatusResponse pgResponse =
                paymentWebhookPgVerificationService.verifyCanceled(payment, request);

        payment.syncCanceledFromWebhook(
                pgResponse.getRawResponse() != null ? pgResponse.getRawResponse() : request.getRawResponse()
        );
    }

    private void validateRequest(PaymentWebhookRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "webhook request가 없습니다.");
        }

        if (request.getOrderId() == null || request.getOrderId().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "orderId가 없는 webhook 요청입니다.");
        }

        if (request.getEventType() == null || request.getEventType().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "eventType이 없는 webhook 요청입니다.");
        }
    }

    private boolean isDuplicatedTransmission(String transmissionId) {
        if (transmissionId == null || transmissionId.isBlank()) {
            return false;
        }

        return paymentWebhookLogRepository
                .findByProviderAndTransmissionId(PROVIDER, transmissionId)
                .isPresent();
    }

    private String toPayload(String rawBody, PaymentWebhookRequest request) {
        if (rawBody != null && !rawBody.isBlank()) {
            return rawBody;
        }

        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            log.warn(
                    "failed to serialize webhook payload. provider={}, eventType={}, orderId={}",
                    PROVIDER,
                    request.getEventType(),
                    request.getOrderId()
            );
            return "{}";
        }
    }
}