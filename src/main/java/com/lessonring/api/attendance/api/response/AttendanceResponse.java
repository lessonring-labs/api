package com.lessonring.api.attendance.api.response;

import com.lessonring.api.attendance.domain.Attendance;
import com.lessonring.api.attendance.domain.AttendanceStatus;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class AttendanceResponse {

    private final Long id;
    private final Long bookingId;
    private final Long memberId;
    private final Long scheduleId;
    private final AttendanceStatus status;
    private final LocalDateTime checkedAt;
    private final String note;

    public AttendanceResponse(Attendance attendance) {
        this.id = attendance.getId();
        this.bookingId = attendance.getBookingId();
        this.memberId = attendance.getMemberId();
        this.scheduleId = attendance.getScheduleId();
        this.status = attendance.getStatus();
        this.checkedAt = attendance.getCheckedAt();
        this.note = attendance.getNote();
    }
}