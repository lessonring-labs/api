package com.lessonring.api.schedule.api.request;

import com.lessonring.api.schedule.domain.ScheduleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@Schema(description = "수업 생성 요청")
public class ScheduleCreateRequest {

    @NotNull
    @Schema(description = "스튜디오 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long studioId;

    @NotNull
    @Schema(description = "강사 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long instructorId;

    @NotBlank
    @Schema(description = "수업명", example = "필라테스 입문", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotNull
    @Schema(description = "수업 유형", example = "GROUP", requiredMode = Schema.RequiredMode.REQUIRED)
    private ScheduleType type;

    @NotNull
    @Future
    @Schema(description = "수업 시작 시각", example = "2026-03-20T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime startAt;

    @NotNull
    @Future
    @Schema(description = "수업 종료 시각", example = "2026-03-20T11:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime endAt;

    @NotNull
    @Positive
    @Schema(description = "정원", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer capacity;
}