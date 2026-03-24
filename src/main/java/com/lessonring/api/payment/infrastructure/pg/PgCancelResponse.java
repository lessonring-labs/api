package com.lessonring.api.payment.infrastructure.pg;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PgCancelResponse {

    private String provider;
    private String paymentKey;
    private boolean success;
    private String cancelKey;
    private String rawResponse;
    private String failureReason;
}