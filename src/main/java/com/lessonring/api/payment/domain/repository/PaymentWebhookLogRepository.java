package com.lessonring.api.payment.domain.repository;

import com.lessonring.api.payment.domain.PaymentWebhookLog;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentWebhookLogRepository extends JpaRepository<PaymentWebhookLog, Long> {

    Optional<PaymentWebhookLog> findByProviderAndTransmissionId(String provider, String transmissionId);
}
