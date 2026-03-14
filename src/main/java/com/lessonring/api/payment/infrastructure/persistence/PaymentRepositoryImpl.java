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
    public Optional<Payment> findById(Long id) {
        return paymentJpaRepository.findById(id);
    }

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public List<Payment> findAllByMemberId(Long memberId) {
        return paymentJpaRepository.findAllByMemberId(memberId);
    }
}
