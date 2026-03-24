package com.lessonring.api.payment.domain.repository;

import com.lessonring.api.payment.domain.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(Long id);

    List<Payment> findAll();

    Optional<Payment> findByPgOrderId(String pgOrderId);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
}