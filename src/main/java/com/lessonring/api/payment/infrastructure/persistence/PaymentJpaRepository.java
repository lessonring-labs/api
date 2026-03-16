package com.lessonring.api.payment.infrastructure.persistence;

import com.lessonring.api.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}