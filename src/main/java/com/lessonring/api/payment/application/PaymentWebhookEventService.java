package com.lessonring.api.payment.application;

import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.payment.domain.PaymentWebhookEvent;
import com.lessonring.api.payment.domain.PaymentWebhookEventStatus;
import com.lessonring.api.payment.domain.repository.PaymentWebhookEventRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentWebhookEventService {

    private final PaymentWebhookEventRepository paymentWebhookEventRepository;

    @Transactional
    public PaymentWebhookEventStartResult startOrGet(
            String eventId,
            String paymentKey,
            String eventType,
            String payloadHash,
            String rawPayload
    ) {
        validate(eventId, eventType, payloadHash, rawPayload);

        return paymentWebhookEventRepository.findByEventId(eventId)
                .map(existing -> handleExisting(existing, payloadHash))
                .orElseGet(() -> createNew(eventId, paymentKey, eventType, payloadHash, rawPayload));
    }

    @Transactional
    public void markProcessing(PaymentWebhookEvent event) {
        event.markProcessing();
    }

    @Transactional
    public void markSuccess(PaymentWebhookEvent event) {
        event.markSucceeded();
    }

    @Transactional
    public void markFailed(PaymentWebhookEvent event, ErrorCode errorCode, String errorMessage) {
        event.markFailed(errorCode.name(), errorMessage);
    }

    private PaymentWebhookEventStartResult handleExisting(
            PaymentWebhookEvent existing,
            String payloadHash
    ) {
        if (!Objects.equals(existing.getPayloadHash(), payloadHash)) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    "동일 webhook eventId로 다른 payload를 처리할 수 없습니다."
            );
        }

        return new PaymentWebhookEventStartResult(
                existing,
                existing.getStatus(),
                false
        );
    }

    private PaymentWebhookEventStartResult createNew(
            String eventId,
            String paymentKey,
            String eventType,
            String payloadHash,
            String rawPayload
    ) {
        PaymentWebhookEvent created = PaymentWebhookEvent.create(
                eventId,
                paymentKey,
                eventType,
                payloadHash,
                rawPayload
        );

        PaymentWebhookEvent saved = paymentWebhookEventRepository.save(created);

        return new PaymentWebhookEventStartResult(
                saved,
                PaymentWebhookEventStatus.RECEIVED,
                true
        );
    }

    private void validate(
            String eventId,
            String eventType,
            String payloadHash,
            String rawPayload
    ) {
        if (eventId == null || eventId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "webhook eventId는 필수입니다.");
        }

        if (eventType == null || eventType.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "webhook eventType은 필수입니다.");
        }

        if (payloadHash == null || payloadHash.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "webhook payloadHash는 필수입니다.");
        }

        if (rawPayload == null || rawPayload.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "webhook rawPayload는 필수입니다.");
        }
    }
}