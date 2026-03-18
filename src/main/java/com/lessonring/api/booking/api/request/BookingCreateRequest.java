package com.lessonring.api.booking.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "예약 생성 요청")
public class BookingCreateRequest {

    @NotNull
    @Schema(
            description = "스튜디오 ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long studioId;

    @NotNull
    @Schema(
            description = "회원 ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long memberId;

    @NotNull
    @Schema(
            description = "예약할 스케줄 ID",
            example = "10",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long scheduleId;

    @Schema(
            description = "사용할 이용권 ID",
            example = "3",
            nullable = true
    )
    private Long membershipId;
}