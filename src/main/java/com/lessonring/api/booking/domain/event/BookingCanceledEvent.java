package com.lessonring.api.booking.domain.event;

import com.lessonring.api.common.event.DomainEvent;
import lombok.Getter;

@Getter
public class BookingCanceledEvent extends DomainEvent {

    private final Long bookingId;
    private final Long studioId;
    private final Long memberId;
    private final Long scheduleId;
    private final Long membershipId;
    private final String cancelReason;

    public BookingCanceledEvent(
            Long bookingId,
            Long studioId,
            Long memberId,
            Long scheduleId,
            Long membershipId,
            String cancelReason
    ) {
        super();
        this.bookingId = bookingId;
        this.studioId = studioId;
        this.memberId = memberId;
        this.scheduleId = scheduleId;
        this.membershipId = membershipId;
        this.cancelReason = cancelReason;
    }
}