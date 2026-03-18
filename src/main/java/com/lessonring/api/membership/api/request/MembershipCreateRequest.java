package com.lessonring.api.membership.api.request;

import com.lessonring.api.membership.domain.MembershipType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Getter;

@Getter
@Schema(description = "이용권 생성 요청")
public class MembershipCreateRequest {

    @NotNull
    @Schema(description = "스튜디오 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long studioId;

    @NotNull
    @Schema(description = "회원 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long memberId;

    @NotBlank
    @Schema(description = "이용권명", example = "10회권", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull
    @Schema(description = "이용권 유형", example = "COUNT", requiredMode = Schema.RequiredMode.REQUIRED)
    private MembershipType type;

    @NotNull
    @Positive
    @Schema(description = "총 횟수", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalCount;

    @NotNull
    @Schema(description = "이용 시작일", example = "2026-03-18", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate startDate;

    @NotNull
    @Schema(description = "이용 종료일", example = "2026-04-18", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate endDate;
}