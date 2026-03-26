package com.lessonring.api.payment.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.membership.domain.Membership;
import com.lessonring.api.membership.domain.repository.MembershipRepository;
import com.lessonring.api.payment.api.request.PaymentWebhookRequest;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.PaymentStatus;
import com.lessonring.api.payment.domain.PaymentWebhookLog;
import com.lessonring.api.payment.domain.repository.PaymentRepository;
import com.lessonring.api.payment.domain.repository.PaymentWebhookLogRepository;
import com.lessonring.api.payment.infrastructure.lock.PaymentStateLockManager;
import com.lessonring.api.payment.infrastructure.pg.PgPaymentStatusResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
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
    private final MembershipRepository membershipRepository;
    private final EntityManager entityManager;
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
        log.info("payment webhook requested. provider={}, transmissionId={}, eventType={}, orderId={}",
                PROVIDER,
                transmissionId,
                request != null ? request.getEventType() : null,
                request != null ? request.getOrderId() : null);

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
                log.warn("payment webhook lock acquisition failed. provider={}, transmissionId={}, orderId={}, eventType={}",
                        PROVIDER, transmissionId, request.getOrderId(), request.getEventType());
                throw new BusinessException(
                        ErrorCode.PAYMENT_LOCK_ACQUISITION_FAILED,
                        "이미 다른 결제 상태 변경 요청이 처리 중입니다."
                );
            }

            log.info("payment webhook lock acquired. provider={}, transmissionId={}, paymentId={}, eventType={}",
                    PROVIDER, transmissionId, payment.getId(), request.getEventType());

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

            entityManager.refresh(payment, LockModeType.PESSIMISTIC_WRITE);
            Payment lockedPayment = payment;

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

        } catch (BusinessException e) {
            log.warn("payment webhook failed. provider={}, transmissionId={}, eventType={}, orderId={}, errorCode={}, message={}",
                    PROVIDER,
                    transmissionId,
                    request.getEventType(),
                    request.getOrderId(),
                    e.getErrorCode().name(),
                    e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("payment webhook failed by unexpected error. provider={}, transmissionId={}, eventType={}, orderId={}",
                    PROVIDER,
                    transmissionId,
                    request.getEventType(),
                    request.getOrderId(),
                    e);
            throw e;
        } finally {
            if (locked && !unlockDeferred) {
                paymentStateLockManager.unlock(payment.getId());
                log.info("payment webhook lock released. provider={}, transmissionId={}, paymentId={}",
                        PROVIDER, transmissionId, payment.getId());
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

        createMembershipIfNeeded(payment);
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

        refundMembershipIfNeeded(payment);
    }

    private void refundMembershipIfNeeded(Payment payment) {
        if (payment.getMembershipId() == null) {
            return;
        }

        Membership membership = membershipRepository.findById(payment.getMembershipId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBERSHIP_NOT_FOUND));

        if (!membership.isRefunded()) {
            membership.refund();
        }
    }

    private void createMembershipIfNeeded(Payment payment) {
        if (payment.getMembershipId() != null) {
            return;
        }

        Membership membership = Membership.create(
                payment.getStudioId(),
                payment.getMemberId(),
                payment.getMembershipName(),
                payment.getMembershipType(),
                payment.getMembershipTotalCount(),
                payment.getMembershipStartDate(),
                payment.getMembershipEndDate()
        );

        Membership savedMembership = membershipRepository.save(membership);
        payment.linkMembership(savedMembership.getId());
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
