package com.lessonring.api.booking.domain.event;

import com.lessonring.api.common.event.DomainEvent;
import lombok.Getter;

@Getter
public class BookingCreatedEvent extends DomainEvent {

    private final Long bookingId;
    private final Long studioId;
    private final Long memberId;
    private final Long scheduleId;
    private final Long membershipId;

    public BookingCreatedEvent(
            Long bookingId,
            Long studioId,
            Long memberId,
            Long scheduleId,
            Long membershipId
    ) {
        super();
        this.bookingId = bookingId;
        this.studioId = studioId;
        this.memberId = memberId;
        this.scheduleId = scheduleId;
        this.membershipId = membershipId;
    }
}