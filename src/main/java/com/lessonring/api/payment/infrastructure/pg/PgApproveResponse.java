package com.lessonring.api.payment.infrastructure.pg;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PgApproveResponse {

    private String provider;
    private String paymentKey;
    private String orderId;
    private Long amount;
    private boolean success;
    private String rawResponse;
    private String failureReason;
}