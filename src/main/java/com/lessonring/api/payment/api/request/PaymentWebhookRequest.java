package com.lessonring.api.payment.api.request;

import lombok.Getter;

@Getter
public class PaymentWebhookRequest {

    private String eventType;
    private String orderId;
    private String paymentKey;
    private String status;
    private String rawResponse;
    private String failureReason;
}