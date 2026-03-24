package com.lessonring.api.payment.application;

import com.lessonring.api.payment.domain.PaymentOperation;

public record PaymentOperationStartResult(
        PaymentOperation operation,
        boolean newlyCreated
) {
}