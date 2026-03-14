package com.lessonring.api.booking.domain.event;

import com.lessonring.api.common.event.DomainEvent;
import lombok.Getter;

@Getter
public class BookingCreatedEvent extends DomainEvent {

    private final Long bookingId;
    private final Long memberId;
    private final Long scheduleId;

    public BookingCreatedEvent(Long bookingId, Long memberId, Long scheduleId) {
        super();
        this.bookingId = bookingId;
        this.memberId = memberId;
        this.scheduleId = scheduleId;
    }
}
