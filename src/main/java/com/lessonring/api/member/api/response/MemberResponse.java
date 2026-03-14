package com.lessonring.api.member.api.response;

import com.lessonring.api.member.domain.Member;
import com.lessonring.api.member.domain.MemberStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class MemberResponse {

    private final Long id;
    private final Long studioId;
    private final String name;
    private final String phone;
    private final String email;
    private final String gender;
    private final LocalDate birthDate;
    private final MemberStatus status;
    private final LocalDateTime joinedAt;
    private final String memo;

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.studioId = member.getStudioId();
        this.name = member.getName();
        this.phone = member.getPhone();
        this.email = member.getEmail();
        this.gender = member.getGender() == null ? null : member.getGender().name();
        this.birthDate = member.getBirthDate();
        this.status = member.getStatus();
        this.joinedAt = member.getJoinedAt();
        this.memo = member.getMemo();
    }
}