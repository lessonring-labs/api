package com.lessonring.api.booking.domain.event;

import com.lessonring.api.common.event.DomainEvent;
import lombok.Getter;

@Getter
public class BookingCanceledEvent extends DomainEvent {

    private final Long bookingId;
    private final Long memberId;
    private final Long scheduleId;
    private final String cancelReason;

    public BookingCanceledEvent(Long bookingId, Long memberId, Long scheduleId, String cancelReason) {
        super();
        this.bookingId = bookingId;
        this.memberId = memberId;
        this.scheduleId = scheduleId;
        this.cancelReason = cancelReason;
    }
}
