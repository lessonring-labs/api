package com.lessonring.api.membership.api.request;

import com.lessonring.api.membership.domain.MembershipType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class MembershipCreateRequest {

    @NotNull
    private Long studioId;

    @NotNull
    private Long memberId;

    @NotBlank
    private String name;

    @NotNull
    private MembershipType type;

    @NotNull
    @Positive
    private Integer totalCount;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}