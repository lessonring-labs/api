package com.lessonring.api.payment.infrastructure.persistence;

import com.lessonring.api.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPgOrderId(String pgOrderId);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
}