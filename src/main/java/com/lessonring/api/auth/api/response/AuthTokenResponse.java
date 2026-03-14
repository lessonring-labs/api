package com.lessonring.api.auth.api.response;

import lombok.Getter;

@Getter
public class AuthTokenResponse {

    private final String accessToken;
    private final String refreshToken;

    public AuthTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}