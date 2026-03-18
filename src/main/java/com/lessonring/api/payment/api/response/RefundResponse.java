package com.lessonring.api.payment.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "결제 환불 응답")
public class RefundResponse {

    @Schema(description = "결제 ID", example = "1")
    private final Long paymentId;

    @Schema(description = "회원 ID", example = "1")
    private final Long memberId;

    @Schema(description = "이용권 ID", example = "3")
    private final Long membershipId;

    @Schema(description = "환불 금액", example = "90000")
    private final Long refundAmount;

    @Schema(description = "취소된 미래 예약 수", example = "2")
    private final int canceledBookingCount;

    @Schema(description = "결제 상태", example = "CANCELED")
    private final String paymentStatus;

    @Schema(description = "이용권 상태", example = "REFUNDED")
    private final String membershipStatus;

    public RefundResponse(
            Long paymentId,
            Long memberId,
            Long membershipId,
            Long refundAmount,
            int canceledBookingCount,
            String paymentStatus,
            String membershipStatus
    ) {
        this.paymentId = paymentId;
        this.memberId = memberId;
        this.membershipId = membershipId;
        this.refundAmount = refundAmount;
        this.canceledBookingCount = canceledBookingCount;
        this.paymentStatus = paymentStatus;
        this.membershipStatus = membershipStatus;
    }
}