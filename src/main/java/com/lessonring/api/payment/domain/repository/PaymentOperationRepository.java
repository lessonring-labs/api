package com.lessonring.api.payment.domain.repository;

import com.lessonring.api.payment.domain.PaymentOperation;
import com.lessonring.api.payment.domain.PaymentOperationType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentOperationRepository extends JpaRepository<PaymentOperation, Long> {

    Optional<PaymentOperation> findByPaymentIdAndOperationTypeAndIdempotencyKey(
            Long paymentId,
            PaymentOperationType operationType,
            String idempotencyKey
    );
}