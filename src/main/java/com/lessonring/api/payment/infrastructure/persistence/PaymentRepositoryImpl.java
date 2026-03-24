package com.lessonring.api.payment.infrastructure.persistence;

import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.repository.PaymentRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return paymentJpaRepository.findById(id);
    }

    @Override
    public List<Payment> findAll() {
        return paymentJpaRepository.findAll();
    }

    @Override
    public Optional<Payment> findByPgOrderId(String pgOrderId) {
        return paymentJpaRepository.findByPgOrderId(pgOrderId);
    }

    @Override
    public Optional<Payment> findByIdempotencyKey(String idempotencyKey) {
        return paymentJpaRepository.findByIdempotencyKey(idempotencyKey);
    }
}