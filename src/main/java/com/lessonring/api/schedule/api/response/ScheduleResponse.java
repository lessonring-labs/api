package com.lessonring.api.schedule.api.response;

import com.lessonring.api.schedule.domain.Schedule;
import com.lessonring.api.schedule.domain.ScheduleStatus;
import com.lessonring.api.schedule.domain.ScheduleType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@Schema(description = "수업 응답")
public class ScheduleResponse {

    @Schema(description = "수업 ID", example = "1")
    private final Long id;

    @Schema(description = "스튜디오 ID", example = "1")
    private final Long studioId;

    @Schema(description = "강사 ID", example = "1")
    private final Long instructorId;

    @Schema(description = "수업명", example = "필라테스 입문")
    private final String title;

    @Schema(description = "수업 유형", example = "GROUP")
    private final ScheduleType type;

    @Schema(description = "수업 시작 시각", example = "2026-03-20T10:00:00")
    private final LocalDateTime startAt;

    @Schema(description = "수업 종료 시각", example = "2026-03-20T11:00:00")
    private final LocalDateTime endAt;

    @Schema(description = "정원", example = "10")
    private final Integer capacity;

    @Schema(description = "현재 예약 수", example = "3")
    private final Integer bookedCount;

    @Schema(description = "수업 상태", example = "OPEN")
    private final ScheduleStatus status;

    public ScheduleResponse(Schedule schedule) {
        this.id = schedule.getId();
        this.studioId = schedule.getStudioId();
        this.instructorId = schedule.getInstructorId();
        this.title = schedule.getTitle();
        this.type = schedule.getType();
        this.startAt = schedule.getStartAt();
        this.endAt = schedule.getEndAt();
        this.capacity = schedule.getCapacity();
        this.bookedCount = schedule.getBookedCount();
        this.status = schedule.getStatus();
    }
}