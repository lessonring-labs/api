package com.lessonring.api.auth.application;

import com.lessonring.api.auth.api.response.AuthTokenResponse;
import com.lessonring.api.auth.domain.RefreshToken;
import com.lessonring.api.auth.domain.repository.RefreshTokenRepository;
import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.common.security.JwtTokenProvider;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public AuthTokenResponse login(Long userId) {

        String accessToken = jwtTokenProvider.createAccessToken(userId);
        String refreshTokenValue = jwtTokenProvider.createRefreshToken(userId);

        LocalDateTime expiresAt =
                LocalDateTime.now().plusNanos(jwtTokenProvider.getRefreshTokenExpiration() * 1_000_000);

        refreshTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        token -> token.update(refreshTokenValue, expiresAt),
                        () -> refreshTokenRepository.save(
                                RefreshToken.create(userId, refreshTokenValue, expiresAt)
                        )
                );

        return new AuthTokenResponse(accessToken, refreshTokenValue);
    }

    @Transactional(readOnly = true)
    public AuthTokenResponse refresh(String refreshTokenValue) {

        if (!jwtTokenProvider.validateToken(refreshTokenValue)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        Long userId = refreshToken.getUserId();

        String newAccessToken = jwtTokenProvider.createAccessToken(userId);

        return new AuthTokenResponse(newAccessToken, refreshTokenValue);
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}