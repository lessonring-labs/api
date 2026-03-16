// 3) src/main/java/com/lessonring/api/payment/api/response/PaymentResponse.java
package com.lessonring.api.payment.api.response;

import com.lessonring.api.membership.domain.MembershipType;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.PaymentMethod;
import com.lessonring.api.payment.domain.PaymentStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class PaymentResponse {

    private final Long id;
    private final Long studioId;
    private final Long memberId;
    private final Long membershipId;
    private final String orderName;
    private final PaymentMethod paymentMethod;
    private final PaymentStatus status;
    private final Long amount;
    private final LocalDateTime paidAt;
    private final LocalDateTime canceledAt;
    private final String membershipName;
    private final MembershipType membershipType;
    private final Integer membershipTotalCount;
    private final LocalDate membershipStartDate;
    private final LocalDate membershipEndDate;

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.studioId = payment.getStudioId();
        this.memberId = payment.getMemberId();
        this.membershipId = payment.getMembershipId();
        this.orderName = payment.getOrderName();
        this.paymentMethod = payment.getPaymentMethod();
        this.status = payment.getStatus();
        this.amount = payment.getAmount();
        this.paidAt = payment.getPaidAt();
        this.canceledAt = payment.getCanceledAt();
        this.membershipName = payment.getMembershipName();
        this.membershipType = payment.getMembershipType();
        this.membershipTotalCount = payment.getMembershipTotalCount();
        this.membershipStartDate = payment.getMembershipStartDate();
        this.membershipEndDate = payment.getMembershipEndDate();
    }
}