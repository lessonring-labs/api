package com.lessonring.api.payment.infrastructure.webhook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertiesPaymentWebhookSecretProvider implements PaymentWebhookSecretProvider {

    private final String secret;

    public PropertiesPaymentWebhookSecretProvider(
            @Value("${payment.webhook.secret}") String secret
    ) {
        this.secret = secret;
    }

    @Override
    public String getSecret() {
        return secret;
    }
}