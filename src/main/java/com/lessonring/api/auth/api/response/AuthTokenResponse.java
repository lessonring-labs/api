package com.lessonring.api.auth.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "인증 토큰 응답")
public class AuthTokenResponse {

    @Schema(
            description = "Access Token",
            example = "eyJhbGciOiJIUzI1NiJ9..."
    )
    private final String accessToken;

    @Schema(
            description = "Refresh Token",
            example = "eyJhbGciOiJIUzI1NiJ9..."
    )
    private final String refreshToken;

    public AuthTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}