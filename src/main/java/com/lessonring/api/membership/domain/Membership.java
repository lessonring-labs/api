package com.lessonring.api.membership.domain;

import com.lessonring.api.common.entity.BaseEntity;
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

    @Column(name = "studio_id", nullable = false)
    private Long studioId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "total_count")
    private Integer totalCount;

    @Column(name = "remaining_count")
    private Integer remainingCount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipStatus status;

    private Membership(
            Long studioId,
            Long memberId,
            String name,
            String type,
            Integer totalCount,
            Integer remainingCount,
            LocalDate startDate,
            LocalDate endDate,
            MembershipStatus status
    ) {
        this.studioId = studioId;
        this.memberId = memberId;
        this.name = name;
        this.type = type;
        this.totalCount = totalCount;
        this.remainingCount = remainingCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public static Membership create(
            Long studioId,
            Long memberId,
            String name,
            String type,
            Integer totalCount,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return new Membership(
                studioId,
                memberId,
                name,
                type,
                totalCount,
                totalCount,
                startDate,
                endDate,
                MembershipStatus.ACTIVE
        );
    }

    public void useOnce() {
        if (remainingCount == null || remainingCount <= 0) {
            this.status = MembershipStatus.USED_UP;
            return;
        }

        this.remainingCount = this.remainingCount - 1;

        if (this.remainingCount <= 0) {
            this.status = MembershipStatus.USED_UP;
        }
    }

    public void expire() {
        this.status = MembershipStatus.EXPIRED;
    }

    public boolean isAvailable() {
        return this.status == MembershipStatus.ACTIVE
                && this.remainingCount != null
                && this.remainingCount > 0
                && !this.endDate.isBefore(LocalDate.now());
    }
}