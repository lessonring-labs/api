package com.lessonring.api.attendance.domain;

import com.lessonring.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "attendance")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    @Column(name = "checked_at", nullable = false)
    private LocalDateTime checkedAt;

    @Column(name = "note")
    private String note;

    private Attendance(
            Long bookingId,
            Long memberId,
            Long scheduleId,
            AttendanceStatus status,
            LocalDateTime checkedAt,
            String note
    ) {
        this.bookingId = bookingId;
        this.memberId = memberId;
        this.scheduleId = scheduleId;
        this.status = status;
        this.checkedAt = checkedAt;
        this.note = note;
    }

    public static Attendance create(
            Long bookingId,
            Long memberId,
            Long scheduleId,
            AttendanceStatus status,
            String note
    ) {
        return new Attendance(
                bookingId,
                memberId,
                scheduleId,
                status,
                LocalDateTime.now(),
                note
        );
    }
}