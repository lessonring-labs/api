package com.lessonring.api.payment.api.response;

import com.lessonring.api.membership.domain.MembershipType;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.PaymentMethod;
import com.lessonring.api.payment.domain.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@Schema(description = "결제 응답")
public class PaymentResponse {

    @Schema(description = "결제 ID", example = "1")
    private final Long id;

    @Schema(description = "스튜디오 ID", example = "1")
    private final Long studioId;

    @Schema(description = "회원 ID", example = "1")
    private final Long memberId;

    @Schema(description = "생성된 이용권 ID", example = "3", nullable = true)
    private final Long membershipId;

    @Schema(description = "주문명", example = "10회권 결제")
    private final String orderName;

    @Schema(description = "결제 수단", example = "CARD")
    private final PaymentMethod paymentMethod;

    @Schema(description = "결제 상태", example = "COMPLETED")
    private final PaymentStatus status;

    @Schema(description = "결제 금액", example = "150000")
    private final Long amount;

    @Schema(description = "결제 완료 시각", example = "2026-03-18T10:30:00", nullable = true)
    private final LocalDateTime paidAt;

    @Schema(description = "결제 취소 시각", example = "2026-03-18T11:00:00", nullable = true)
    private final LocalDateTime canceledAt;

    @Schema(description = "이용권명", example = "10회권")
    private final String membershipName;

    @Schema(description = "이용권 유형", example = "COUNT")
    private final MembershipType membershipType;

    @Schema(description = "이용권 총 횟수", example = "10")
    private final Integer membershipTotalCount;

    @Schema(description = "이용권 시작일", example = "2026-03-18")
    private final LocalDate membershipStartDate;

    @Schema(description = "이용권 종료일", example = "2026-04-18")
    private final LocalDate membershipEndDate;

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.studioId = payment.getStudioId();
        this.memberId = payment.getMemberId();
        this.membershipId = payment.getMembershipId();
        this.orderName = payment.getOrderName();
        this.paymentMethod = payment.getPaymentMethod();
        this.status = payment.getStatus();
        this.amount = payment.getAmount();
        this.paidAt = payment.getPaidAt();
        this.canceledAt = payment.getCanceledAt();
        this.membershipName = payment.getMembershipName();
        this.membershipType = payment.getMembershipType();
        this.membershipTotalCount = payment.getMembershipTotalCount();
        this.membershipStartDate = payment.getMembershipStartDate();
        this.membershipEndDate = payment.getMembershipEndDate();
    }
}