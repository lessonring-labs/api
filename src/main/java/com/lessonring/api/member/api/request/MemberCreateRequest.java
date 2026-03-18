package com.lessonring.api.member.api.request;

import com.lessonring.api.member.domain.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;

@Getter
@Schema(description = "회원 등록 요청")
public class MemberCreateRequest {

    @NotNull
    @Schema(description = "스튜디오 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long studioId;

    @NotBlank
    @Schema(description = "회원 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank
    @Schema(description = "휴대폰 번호", example = "01012345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    @Schema(description = "이메일", example = "devyn@example.com", nullable = true)
    private String email;

    @Schema(description = "성별", example = "MALE", nullable = true)
    private Gender gender;

    @Schema(description = "생년월일", example = "1990-01-01", nullable = true)
    private LocalDate birthDate;

    @Schema(description = "메모", example = "첫 방문 회원", nullable = true)
    private String memo;
}