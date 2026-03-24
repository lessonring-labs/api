package com.lessonring.api.payment.infrastructure.persistence;

import com.lessonring.api.payment.domain.PaymentWebhookLog;
import com.lessonring.api.payment.domain.repository.PaymentWebhookLogRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentWebhookLogRepositoryImpl implements PaymentWebhookLogRepository {

    private final PaymentWebhookLogJpaRepository paymentWebhookLogJpaRepository;

    @Override
    public PaymentWebhookLog save(PaymentWebhookLog log) {
        return paymentWebhookLogJpaRepository.save(log);
    }

    @Override
    public Optional<PaymentWebhookLog> findByProviderAndTransmissionId(String provider, String transmissionId) {
        return paymentWebhookLogJpaRepository.findByProviderAndTransmissionId(provider, transmissionId);
    }
}