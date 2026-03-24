package com.lessonring.api.payment.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentApproveRequest {

    @NotBlank(message = "paymentKey는 필수입니다.")
    private String paymentKey;

    @NotBlank(message = "orderId는 필수입니다.")
    private String orderId;

    @NotNull(message = "amount는 필수입니다.")
    private Long amount;
}