package com.lessonring.api.payment.domain.event;

import com.lessonring.api.common.event.DomainEvent;
import lombok.Getter;

@Getter
public class PaymentCompletedEvent extends DomainEvent {

    private final Long paymentId;
    private final Long studioId;
    private final Long memberId;
    private final Long membershipId;
    private final Long amount;

    public PaymentCompletedEvent(
            Long paymentId,
            Long studioId,
            Long memberId,
            Long membershipId,
            Long amount
    ) {
        super();
        this.paymentId = paymentId;
        this.studioId = studioId;
        this.memberId = memberId;
        this.membershipId = membershipId;
        this.amount = amount;
    }
}