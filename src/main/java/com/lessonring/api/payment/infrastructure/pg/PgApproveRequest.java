package com.lessonring.api.payment.infrastructure.pg;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PgApproveRequest {

    private final String paymentKey;
    private final String orderId;
    private final Long amount;
}