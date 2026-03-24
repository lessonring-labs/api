package com.lessonring.api.payment.api.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentApproveResponse {

    private Long paymentId;
    private String status;
    private String paymentKey;
    private Long amount;
}