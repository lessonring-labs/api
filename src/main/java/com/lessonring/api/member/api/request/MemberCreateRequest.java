package com.lessonring.api.member.api.request;

import com.lessonring.api.member.domain.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class MemberCreateRequest {

    @NotNull
    private Long studioId;

    @NotBlank
    private String name;

    @NotBlank
    private String phone;

    private String email;

    private Gender gender;

    private LocalDate birthDate;

    private String memo;
}