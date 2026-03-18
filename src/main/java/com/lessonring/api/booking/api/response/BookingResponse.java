package com.lessonring.api.booking.api.response;

import com.lessonring.api.booking.domain.Booking;
import com.lessonring.api.booking.domain.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@Schema(description = "예약 응답")
public class BookingResponse {

    @Schema(description = "예약 ID", example = "1")
    private final Long id;

    @Schema(description = "스튜디오 ID", example = "1")
    private final Long studioId;

    @Schema(description = "회원 ID", example = "1")
    private final Long memberId;

    @Schema(description = "스케줄 ID", example = "10")
    private final Long scheduleId;

    @Schema(description = "이용권 ID", example = "3", nullable = true)
    private final Long membershipId;

    @Schema(description = "예약 상태", example = "BOOKED")
    private final BookingStatus status;

    @Schema(description = "예약 생성 시각", example = "2026-03-18T10:30:00")
    private final LocalDateTime bookedAt;

    @Schema(description = "예약 취소 시각", example = "2026-03-18T11:00:00", nullable = true)
    private final LocalDateTime canceledAt;

    @Schema(description = "예약 취소 사유", example = "user canceled", nullable = true)
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