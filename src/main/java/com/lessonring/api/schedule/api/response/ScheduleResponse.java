package com.lessonring.api.schedule.api.response;

import com.lessonring.api.schedule.domain.Schedule;
import com.lessonring.api.schedule.domain.ScheduleStatus;
import com.lessonring.api.schedule.domain.ScheduleType;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ScheduleResponse {

    private final Long id;
    private final Long studioId;
    private final Long instructorId;
    private final String title;
    private final ScheduleType type;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final Integer capacity;
    private final Integer bookedCount;
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