// 2) src/main/java/com/lessonring/api/payment/api/request/PaymentCreateRequest.java
package com.lessonring.api.payment.api.request;

import com.lessonring.api.membership.domain.MembershipType;
import com.lessonring.api.payment.domain.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class PaymentCreateRequest {

    @NotNull
    private Long studioId;

    @NotNull
    private Long memberId;

    @NotBlank
    private String orderName;

    @NotNull
    private PaymentMethod paymentMethod;

    @NotNull
    @Positive
    private Long amount;

    @NotBlank
    private String membershipName;

    @NotBlank
    private MembershipType membershipType;

    @NotNull
    @Positive
    private Integer membershipTotalCount;

    @NotNull
    private LocalDate membershipStartDate;

    @NotNull
    private LocalDate membershipEndDate;
}