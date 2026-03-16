package com.lessonring.api.attendance.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AttendanceCreateRequest {

    @NotNull
    private Long bookingId;

    private String note;
}