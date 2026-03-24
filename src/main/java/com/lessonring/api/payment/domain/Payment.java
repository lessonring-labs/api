package com.lessonring.api.payment.domain;

import com.lessonring.api.common.entity.BaseEntity;
import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.membership.domain.MembershipType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(name = "pg_provider")
    private String pgProvider;

    @Column(name = "pg_order_id")
    private String pgOrderId;

    @Column(name = "pg_payment_key")
    private String pgPaymentKey;

    @Column(name = "pg_raw_response", columnDefinition = "TEXT")
    private String pgRawResponse;

    @Column(name = "failed_reason")
    private String failedReason;

    @Column(name = "idempotency_key")
    private String idempotencyKey;

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
            LocalDate membershipEndDate,
            String idempotencyKey
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
        this.idempotencyKey = idempotencyKey;
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
        return create(
                studioId,
                memberId,
                orderName,
                paymentMethod,
                amount,
                membershipName,
                membershipType,
                membershipTotalCount,
                membershipStartDate,
                membershipEndDate,
                null
        );
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
            LocalDate membershipEndDate,
            String idempotencyKey
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
                membershipEndDate,
                idempotencyKey
        );
    }

    public void markApproveRequested(String pgProvider, String pgOrderId) {
        this.pgProvider = pgProvider;
        this.pgOrderId = pgOrderId;
    }

    public void complete(Long membershipId) {
        complete(membershipId, null, null);
    }

    public void complete(Long membershipId, String pgPaymentKey, String pgRawResponse) {
        if (this.status != PaymentStatus.READY) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "READY 상태의 결제만 완료 처리할 수 있습니다.");
        }
        this.status = PaymentStatus.COMPLETED;
        this.membershipId = membershipId;
        this.pgPaymentKey = pgPaymentKey;
        this.pgRawResponse = pgRawResponse;
        this.paidAt = LocalDateTime.now();
    }

    public void fail(String failedReason, String pgRawResponse) {
        if (this.status != PaymentStatus.READY) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "READY 상태의 결제만 실패 처리할 수 있습니다.");
        }
        this.status = PaymentStatus.FAILED;
        this.failedReason = failedReason;
        this.pgRawResponse = pgRawResponse;
    }

    public void cancel() {
        if (this.status == PaymentStatus.CANCELED) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_CANCELED);
        }
        this.status = PaymentStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
    }

    public void cancelWithPg(String pgRawResponse) {
        if (this.status != PaymentStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "완료된 결제만 취소할 수 있습니다.");
        }
        this.status = PaymentStatus.CANCELED;
        this.pgRawResponse = pgRawResponse;
        this.canceledAt = LocalDateTime.now();
    }

    public boolean isCanceled() {
        return this.status == PaymentStatus.CANCELED;
    }

    public void syncCompletedFromWebhook(String pgPaymentKey, String pgRawResponse) {
        if (this.status == PaymentStatus.COMPLETED) {
            return;
        }

        if (this.status == PaymentStatus.CANCELED) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이미 취소된 결제는 완료 처리할 수 없습니다.");
        }

        this.status = PaymentStatus.COMPLETED;
        this.pgPaymentKey = pgPaymentKey;
        this.pgRawResponse = pgRawResponse;
        if (this.paidAt == null) {
            this.paidAt = LocalDateTime.now();
        }
    }

    public void syncFailedFromWebhook(String failedReason, String pgRawResponse) {
        if (this.status == PaymentStatus.COMPLETED
                || this.status == PaymentStatus.CANCELED
                || this.status == PaymentStatus.FAILED) {
            return;
        }

        this.status = PaymentStatus.FAILED;
        this.failedReason = failedReason;
        this.pgRawResponse = pgRawResponse;
    }

    public void syncCanceledFromWebhook(String pgRawResponse) {
        if (this.status == PaymentStatus.CANCELED) {
            return;
        }

        this.status = PaymentStatus.CANCELED;
        this.pgRawResponse = pgRawResponse;
        if (this.canceledAt == null) {
            this.canceledAt = LocalDateTime.now();
        }
    }
}