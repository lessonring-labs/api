package com.lessonring.api.payment.infrastructure.pg;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PgPaymentStatusResponse {

    private String paymentKey;
    private String orderId;
    private Long totalAmount;
    private String status;
    private String rawResponse;

    public boolean isCompleted() {
        return "DONE".equalsIgnoreCase(status)
                || "COMPLETED".equalsIgnoreCase(status)
                || "PAYMENT_COMPLETED".equalsIgnoreCase(status);
    }

    public boolean isCanceled() {
        return "CANCELED".equalsIgnoreCase(status)
                || "CANCELLED".equalsIgnoreCase(status)
                || "PAYMENT_CANCELED".equalsIgnoreCase(status);
    }

    public boolean isFailed() {
        return "FAILED".equalsIgnoreCase(status)
                || "PAYMENT_FAILED".equalsIgnoreCase(status);
    }
}