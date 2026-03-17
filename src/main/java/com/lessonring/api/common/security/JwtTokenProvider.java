package com.lessonring.api.common.security;

public interface JwtTokenProvider {

    String createAccessToken(Long userId);

    String createRefreshToken(Long userId);

    boolean validateToken(String token);

    Long getUserId(String token);

    long getAccessTokenExpiration();

    long getRefreshTokenExpiration();
}