package com.lessonring.api.payment.infrastructure.pg;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PgCancelRequest {

    private String paymentKey;
    private Long cancelAmount;
    private String reason;
}