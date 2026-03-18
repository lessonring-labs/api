package com.lessonring.api.payment.api.request;

import com.lessonring.api.membership.domain.MembershipType;
import com.lessonring.api.payment.domain.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Getter;

@Getter
@Schema(description = "결제 생성 요청")
public class PaymentCreateRequest {

    @NotNull
    @Schema(description = "스튜디오 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long studioId;

    @NotNull
    @Schema(description = "회원 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long memberId;

    @NotBlank
    @Schema(description = "주문명", example = "10회권 결제", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderName;

    @NotNull
    @Schema(description = "결제 수단", example = "CARD", requiredMode = Schema.RequiredMode.REQUIRED)
    private PaymentMethod paymentMethod;

    @NotNull
    @Positive
    @Schema(description = "결제 금액", example = "150000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long amount;

    @NotBlank
    @Schema(description = "생성할 이용권명", example = "10회권", requiredMode = Schema.RequiredMode.REQUIRED)
    private String membershipName;

    @NotNull
    @Schema(description = "생성할 이용권 유형", example = "COUNT", requiredMode = Schema.RequiredMode.REQUIRED)
    private MembershipType membershipType;

    @NotNull
    @Positive
    @Schema(description = "이용권 총 횟수", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer membershipTotalCount;

    @NotNull
    @Schema(description = "이용권 시작일", example = "2026-03-18", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate membershipStartDate;

    @NotNull
    @Schema(description = "이용권 종료일", example = "2026-04-18", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate membershipEndDate;
}