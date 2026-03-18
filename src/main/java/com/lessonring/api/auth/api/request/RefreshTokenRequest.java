package com.lessonring.api.auth.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "토큰 재발급 요청")
public class RefreshTokenRequest {

    @NotBlank
    @Schema(
            description = "재발급에 사용할 refresh token",
            example = "eyJhbGciOiJIUzI1NiJ9...",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String refreshToken;
}