package com.lessonring.api.member.api.response;

import com.lessonring.api.member.domain.Member;
import com.lessonring.api.member.domain.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@Schema(description = "회원 응답")
public class MemberResponse {

    @Schema(description = "회원 ID", example = "1")
    private final Long id;

    @Schema(description = "스튜디오 ID", example = "1")
    private final Long studioId;

    @Schema(description = "회원 이름", example = "홍길동")
    private final String name;

    @Schema(description = "휴대폰 번호", example = "01012345678")
    private final String phone;

    @Schema(description = "이메일", example = "devyn@example.com", nullable = true)
    private final String email;

    @Schema(description = "성별", example = "MALE", nullable = true)
    private final String gender;

    @Schema(description = "생년월일", example = "1990-01-01", nullable = true)
    private final LocalDate birthDate;

    @Schema(description = "회원 상태", example = "ACTIVE")
    private final MemberStatus status;

    @Schema(description = "가입 시각", example = "2026-03-18T10:30:00")
    private final LocalDateTime joinedAt;

    @Schema(description = "메모", example = "첫 방문 회원", nullable = true)
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