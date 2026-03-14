package com.lessonring.api.payment.infrastructure.persistence;

import com.lessonring.api.payment.domain.Payment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByMemberId(Long memberId);
}
