package com.lessonring.api.payment.domain.repository;

import com.lessonring.api.payment.domain.PaymentWebhookLog;
import java.util.Optional;

public interface PaymentWebhookLogRepository {

    PaymentWebhookLog save(PaymentWebhookLog log);

    Optional<PaymentWebhookLog> findByProviderAndTransmissionId(String provider, String transmissionId);
}