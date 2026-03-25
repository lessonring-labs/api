package com.lessonring.api.payment.infrastructure.webhook;

public interface PaymentWebhookSecretProvider {
    String getSecret();
}