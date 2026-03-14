package com.lessonring.api.booking.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BookingCreateRequest {

    @NotNull
    private Long studioId;

    @NotNull
    private Long memberId;

    @NotNull
    private Long scheduleId;

    private Long membershipId;
}