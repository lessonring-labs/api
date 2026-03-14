package com.lessonring.api.common.security;

public interface JwtTokenProvider {

    String createAccessToken(Long userId);

    String createRefreshToken(Long userId);

    Long getUserId(String token);

    boolean validateToken(String token);

    long getRefreshTokenExpiration();
}