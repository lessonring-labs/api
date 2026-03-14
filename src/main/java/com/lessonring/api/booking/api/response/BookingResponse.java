package com.lessonring.api.booking.api.response;

import com.lessonring.api.booking.domain.Booking;
import com.lessonring.api.booking.domain.BookingStatus;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class BookingResponse {

    private final Long id;
    private final Long studioId;
    private final Long memberId;
    private final Long scheduleId;
    private final Long membershipId;
    private final BookingStatus status;
    private final LocalDateTime bookedAt;
    private final LocalDateTime canceledAt;
    private final String cancelReason;

    public BookingResponse(Booking booking) {
        this.id = booking.getId();
        this.studioId = booking.getStudioId();
        this.memberId = booking.getMemberId();
        this.scheduleId = booking.getScheduleId();
        this.membershipId = booking.getMembershipId();
        this.status = booking.getStatus();
        this.bookedAt = booking.getBookedAt();
        this.canceledAt = booking.getCanceledAt();
        this.cancelReason = booking.getCancelReason();
    }
}