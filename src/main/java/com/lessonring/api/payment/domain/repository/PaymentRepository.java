package com.lessonring.api.payment.domain.repository;

import com.lessonring.api.payment.domain.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Optional<Payment> findById(Long id);

    Payment save(Payment payment);

    List<Payment> findAllByMemberId(Long memberId);
}
