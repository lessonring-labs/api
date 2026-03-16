package com.lessonring.api.payment.domain;

import com.lessonring.api.common.entity.BaseEntity;
import com.lessonring.api.membership.domain.MembershipType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "studio_id", nullable = false)
    private Long studioId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "membership_id")
    private Long membershipId;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private Long amount;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "membership_name", nullable = false)
    private String membershipName;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_type", nullable = false)
    private MembershipType membershipType;

    @Column(name = "membership_total_count", nullable = false)
    private Integer membershipTotalCount;

    @Column(name = "membership_start_date", nullable = false)
    private LocalDate membershipStartDate;

    @Column(name = "membership_end_date", nullable = false)
    private LocalDate membershipEndDate;

    private Payment(
            Long studioId,
            Long memberId,
            String orderName,
            PaymentMethod paymentMethod,
            Long amount,
            String membershipName,
            MembershipType membershipType,
            Integer membershipTotalCount,
            LocalDate membershipStartDate,
            LocalDate membershipEndDate
    ) {
        this.studioId = studioId;
        this.memberId = memberId;
        this.orderName = orderName;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.READY;
        this.amount = amount;
        this.membershipName = membershipName;
        this.membershipType = membershipType;
        this.membershipTotalCount = membershipTotalCount;
        this.membershipStartDate = membershipStartDate;
        this.membershipEndDate = membershipEndDate;
    }

    public static Payment create(
            Long studioId,
            Long memberId,
            String orderName,
            PaymentMethod paymentMethod,
            Long amount,
            String membershipName,
            MembershipType membershipType,
            Integer membershipTotalCount,
            LocalDate membershipStartDate,
            LocalDate membershipEndDate
    ) {
        return new Payment(
                studioId,
                memberId,
                orderName,
                paymentMethod,
                amount,
                membershipName,
                membershipType,
                membershipTotalCount,
                membershipStartDate,
                membershipEndDate
        );
    }

    public void complete(Long membershipId) {
        this.status = PaymentStatus.COMPLETED;
        this.membershipId = membershipId;
        this.paidAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
    }
}