package com.lessonring.api.booking.domain;

import com.lessonring.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "booking")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "studio_id", nullable = false)
    private Long studioId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "membership_id")
    private Long membershipId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(name = "booked_at", nullable = false)
    private LocalDateTime bookedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "cancel_reason")
    private String cancelReason;

    private Booking(
            Long studioId,
            Long memberId,
            Long scheduleId,
            Long membershipId,
            BookingStatus status,
            LocalDateTime bookedAt,
            LocalDateTime canceledAt,
            String cancelReason
    ) {
        this.studioId = studioId;
        this.memberId = memberId;
        this.scheduleId = scheduleId;
        this.membershipId = membershipId;
        this.status = status;
        this.bookedAt = bookedAt;
        this.canceledAt = canceledAt;
        this.cancelReason = cancelReason;
    }

    public static Booking create(
            Long studioId,
            Long memberId,
            Long scheduleId,
            Long membershipId
    ) {
        return new Booking(
                studioId,
                memberId,
                scheduleId,
                membershipId,
                BookingStatus.RESERVED,
                LocalDateTime.now(),
                null,
                null
        );
    }

    public void cancel(String cancelReason) {
        this.status = BookingStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
        this.cancelReason = cancelReason;
    }
}