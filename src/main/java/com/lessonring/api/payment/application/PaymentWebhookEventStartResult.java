package com.lessonring.api.payment.application;

import com.lessonring.api.payment.domain.PaymentWebhookEvent;
import com.lessonring.api.payment.domain.PaymentWebhookEventStatus;

public record PaymentWebhookEventStartResult(
        PaymentWebhookEvent event,
        PaymentWebhookEventStatus status,
        boolean newlyCreated
) {
    public boolean isNewlyCreated() {
        return newlyCreated;
    }

    public boolean isSucceeded() {
        return status == PaymentWebhookEventStatus.SUCCEEDED;
    }

    public boolean isProcessing() {
        return status == PaymentWebhookEventStatus.PROCESSING;
    }

    public boolean isFailed() {
        return status == PaymentWebhookEventStatus.FAILED;
    }
}