package com.lessonring.api.payment.domain;

import com.lessonring.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "payment_operation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_payment_operation_key",
                        columnNames = {"payment_id", "operation_type", "idempotency_key"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentOperation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 20)
    private OperationType operationType;

    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;

    @Column(name = "request_hash", nullable = false, length = 64)
    private String requestHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OperationStatus status;

    @Column(name = "provider_reference", length = 200)
    private String providerReference;

    @Lob
    @Column(name = "response_payload")
    private String responsePayload;

    @Column(name = "error_code", length = 100)
    private String errorCode;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    public static PaymentOperation create(
            Long paymentId,
            OperationType operationType,
            String idempotencyKey,
            String requestHash
    ) {
        PaymentOperation operation = new PaymentOperation();
        operation.paymentId = paymentId;
        operation.operationType = operationType;
        operation.idempotencyKey = idempotencyKey;
        operation.requestHash = requestHash;
        operation.status = OperationStatus.PROCESSING;
        return operation;
    }

    public void markSucceeded(String providerReference, String responsePayload) {
        this.status = OperationStatus.SUCCEEDED;
        this.providerReference = providerReference;
        this.responsePayload = responsePayload;
        this.errorCode = null;
        this.errorMessage = null;
    }

    public void markFailed(String errorCode, String errorMessage) {
        this.status = OperationStatus.FAILED;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public boolean isProcessing() {
        return this.status == OperationStatus.PROCESSING;
    }

    public boolean isSucceeded() {
        return this.status == OperationStatus.SUCCEEDED;
    }

    public boolean isFailed() {
        return this.status == OperationStatus.FAILED;
    }
}