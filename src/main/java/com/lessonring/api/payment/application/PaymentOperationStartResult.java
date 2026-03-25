package com.lessonring.api.payment.application;

import com.lessonring.api.payment.domain.PaymentOperation;
import com.lessonring.api.payment.domain.PaymentOperationStatus;

public record PaymentOperationStartResult(
        PaymentOperation operation,
        PaymentOperationStatus status,
        boolean newlyCreated
) {
    public boolean isSucceeded() {
        return status == PaymentOperationStatus.SUCCEEDED;
    }

    public boolean isProcessing() {
        return status == PaymentOperationStatus.PROCESSING;
    }

    public boolean isFailed() {
        return status == PaymentOperationStatus.FAILED;
    }

    public boolean isNewlyCreated() {
        return newlyCreated;
    }
}