package com.lessonring.api.payment.infrastructure.persistence;

import com.lessonring.api.payment.domain.PaymentWebhookLog;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentWebhookLogJpaRepository extends JpaRepository<PaymentWebhookLog, Long> {

    Optional<PaymentWebhookLog> findByProviderAndTransmissionId(String provider, String transmissionId);
}