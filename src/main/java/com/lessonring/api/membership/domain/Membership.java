package com.lessonring.api.membership.domain;

import com.lessonring.api.common.entity.BaseEntity;
import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "membership")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Membership extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studioId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipType type;

    @Column(nullable = false)
    private Integer totalCount;

    @Column(nullable = false)
    private Integer remainingCount;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipStatus status;

    private Membership(
            Long studioId,
            Long memberId,
            String name,
            MembershipType type,
            Integer totalCount,
            LocalDate startDate,
            LocalDate endDate
    ) {
        this.studioId = studioId;
        this.memberId = memberId;
        this.name = name;
        this.type = type;
        this.totalCount = totalCount;
        this.remainingCount = totalCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = MembershipStatus.ACTIVE;
    }

    public static Membership create(
            Long studioId,
            Long memberId,
            String name,
            MembershipType type,
            Integer totalCount,
            LocalDate startDate,
            LocalDate endDate
    ) {
        if (startDate.isAfter(endDate)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (type == MembershipType.COUNT && (totalCount == null || totalCount <= 0)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        return new Membership(
                studioId,
                memberId,
                name,
                type,
                totalCount,
                startDate,
                endDate
        );
    }

    public boolean isExpired(LocalDate today) {
        return endDate.isBefore(today);
    }

    public boolean isWithinPeriod(LocalDate today) {
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public boolean isAvailable() {
        return isAvailable(LocalDate.now());
    }

    public boolean isAvailable(LocalDate today) {
        if (status != MembershipStatus.ACTIVE) {
            return false;
        }

        if (!isWithinPeriod(today)) {
            return false;
        }

        if (type == MembershipType.COUNT) {
            return remainingCount != null && remainingCount > 0;
        }

        if (type == MembershipType.PERIOD) {
            return true;
        }

        return false;
    }

    public void validateUsable(LocalDate today) {
        if (isExpired(today)) {
            markExpired();
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (status == MembershipStatus.SUSPENDED) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (status == MembershipStatus.EXPIRED) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (status == MembershipStatus.USED_UP) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (!isWithinPeriod(today)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (type == MembershipType.COUNT && (remainingCount == null || remainingCount <= 0)) {
            this.remainingCount = 0;
            this.status = MembershipStatus.USED_UP;
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
    }

    public void useOnce(LocalDate today) {
        validateUsable(today);

        if (type == MembershipType.COUNT) {
            this.remainingCount -= 1;

            if (this.remainingCount <= 0) {
                this.remainingCount = 0;
                this.status = MembershipStatus.USED_UP;
            }
        }
    }

    public void markExpired() {
        this.status = MembershipStatus.EXPIRED;
    }

    public void suspend() {
        if (this.status == MembershipStatus.EXPIRED || this.status == MembershipStatus.USED_UP) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
        this.status = MembershipStatus.SUSPENDED;
    }

    public void activate() {
        if (this.status == MembershipStatus.EXPIRED || this.status == MembershipStatus.USED_UP) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
        this.status = MembershipStatus.ACTIVE;
    }

    public void restoreOnce() {
        if (this.type != MembershipType.COUNT) {
            return;
        }

        if (this.remainingCount == null) {
            this.remainingCount = 0;
        }

        if (this.remainingCount < this.totalCount) {
            this.remainingCount += 1;
        }

        if (this.status == MembershipStatus.USED_UP && this.remainingCount > 0) {
            this.status = MembershipStatus.ACTIVE;
        }
    }
}