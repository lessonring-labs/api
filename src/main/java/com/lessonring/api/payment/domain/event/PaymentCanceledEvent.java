package com.lessonring.api.payment.domain.event;

import com.lessonring.api.common.event.DomainEvent;
import lombok.Getter;

@Getter
public class PaymentCanceledEvent extends DomainEvent {

    private final Long paymentId;
    private final Long studioId;
    private final Long memberId;
    private final Long membershipId;
    private final Long amount;

    public PaymentCanceledEvent(
            Long paymentId,
            Long studioId,
            Long memberId,
            Long membershipId,
            Long amount
    ) {
        super(paymentId, "PAYMENT_CANCELED");
        this.paymentId = paymentId;
        this.studioId = studioId;
        this.memberId = memberId;
        this.membershipId = membershipId;
        this.amount = amount;
    }
}