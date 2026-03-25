package com.lessonring.api.payment.domain;

public enum PaymentWebhookEventStatus {
    RECEIVED,
    PROCESSING,
    SUCCEEDED,
    FAILED
}