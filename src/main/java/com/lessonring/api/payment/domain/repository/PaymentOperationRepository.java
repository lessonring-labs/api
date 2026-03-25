package com.lessonring.api.payment.domain.repository;

import com.lessonring.api.payment.domain.PaymentOperationType;
import com.lessonring.api.payment.domain.PaymentOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentOperationRepository extends JpaRepository<PaymentOperation, Long> {

    Optional<PaymentOperation> findByPaymentIdAndOperationTypeAndIdempotencyKey(
            Long paymentId,
            PaymentOperationType operationType,
            String idempotencyKey
    );
}