package com.lessonring.api.payment.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.payment.api.request.PaymentWebhookRequest;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.PaymentWebhookLog;
import com.lessonring.api.payment.domain.repository.PaymentRepository;
import com.lessonring.api.payment.domain.repository.PaymentWebhookLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentWebhookService {

    private static final String PROVIDER = "TOSS";

    private final PaymentRepository paymentRepository;
    private final PaymentWebhookLogRepository paymentWebhookLogRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void handle(
            String transmissionId,
            String signature,
            PaymentWebhookRequest request
    ) {
        if (request.getOrderId() == null || request.getOrderId().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "orderId가 없는 webhook 요청입니다.");
        }

        if (request.getEventType() == null || request.getEventType().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "eventType이 없는 webhook 요청입니다.");
        }

        if (transmissionId != null && !transmissionId.isBlank()) {
            boolean alreadyProcessed = paymentWebhookLogRepository
                    .findByProviderAndTransmissionId(PROVIDER, transmissionId)
                    .isPresent();

            if (alreadyProcessed) {
                log.info("duplicated payment webhook ignored. provider={}, transmissionId={}", PROVIDER, transmissionId);
                return;
            }
        }

        Payment payment = paymentRepository.findByPgOrderId(request.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        switch (request.getEventType()) {
            case "PAYMENT_COMPLETED" -> payment.syncCompletedFromWebhook(
                    request.getPaymentKey(),
                    request.getRawResponse()
            );
            case "PAYMENT_FAILED" -> payment.syncFailedFromWebhook(
                    request.getFailureReason(),
                    request.getRawResponse()
            );
            case "PAYMENT_CANCELED" -> payment.syncCanceledFromWebhook(
                    request.getRawResponse()
            );
            default -> {
                log.warn("unsupported payment webhook eventType={}", request.getEventType());
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
                        toJson(request)
                )
        );

        log.info("payment webhook handled. eventType={}, orderId={}, paymentId={}, transmissionId={}",
                request.getEventType(), request.getOrderId(), payment.getId(), transmissionId);
    }

    private String toJson(PaymentWebhookRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}