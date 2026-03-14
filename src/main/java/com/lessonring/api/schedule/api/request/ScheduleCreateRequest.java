package com.lessonring.api.schedule.api.request;

import com.lessonring.api.schedule.domain.ScheduleType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ScheduleCreateRequest {

    @NotNull
    private Long studioId;

    @NotNull
    private Long instructorId;

    @NotBlank
    private String title;

    @NotNull
    private ScheduleType type;

    @NotNull
    @Future
    private LocalDateTime startAt;

    @NotNull
    @Future
    private LocalDateTime endAt;

    @NotNull
    @Positive
    private Integer capacity;
}