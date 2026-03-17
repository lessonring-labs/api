package com.lessonring.api.booking.domain;

import com.lessonring.api.common.entity.BaseEntity;
import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
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

    @Column(nullable = false)
    private Long studioId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long scheduleId;

    @Column(nullable = false)
    private Long membershipId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false)
    private LocalDateTime bookedAt;

    private LocalDateTime canceledAt;

    private String cancelReason;

    private Booking(
            Long studioId,
            Long memberId,
            Long scheduleId,
            Long membershipId
    ) {
        this.studioId = studioId;
        this.memberId = memberId;
        this.scheduleId = scheduleId;
        this.membershipId = membershipId;
        this.status = BookingStatus.RESERVED;
        this.bookedAt = LocalDateTime.now();
    }

    public static Booking create(
            Long studioId,
            Long memberId,
            Long scheduleId,
            Long membershipId
    ) {
        return new Booking(studioId, memberId, scheduleId, membershipId);
    }

    public void cancel(String reason) {
        if (this.status != BookingStatus.RESERVED) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
        this.status = BookingStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
        this.cancelReason = reason;
    }

    public void attend() {
        if (this.status != BookingStatus.RESERVED) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
        this.status = BookingStatus.ATTENDED;
    }

    public void markNoShow() {
        if (this.status != BookingStatus.RESERVED) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
        this.status = BookingStatus.NO_SHOW;
    }

    public void revertToReserved() {
        if (this.status != BookingStatus.ATTENDED) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
        this.status = BookingStatus.RESERVED;
    }
}