package com.lessonring.api.payment.domain.repository;

import com.lessonring.api.payment.domain.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPgOrderId(String pgOrderId);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
}
