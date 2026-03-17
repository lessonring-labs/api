package com.lessonring.api.auth.application;

import com.lessonring.api.auth.api.response.AuthTokenResponse;
import com.lessonring.api.auth.domain.RefreshToken;
import com.lessonring.api.auth.domain.repository.RefreshTokenRepository;
import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.common.security.JwtTokenProvider;
import com.lessonring.api.member.api.response.MemberResponse;
import com.lessonring.api.member.domain.Member;
import com.lessonring.api.member.domain.repository.MemberRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public AuthTokenResponse login(Long userId) {
        memberRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

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

    @Transactional
    public AuthTokenResponse refresh(String refreshTokenValue) {
        if (!jwtTokenProvider.validateToken(refreshTokenValue)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        Long userId = jwtTokenProvider.getUserId(refreshTokenValue);

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!refreshToken.getToken().equals(refreshTokenValue)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        String newRefreshTokenValue = jwtTokenProvider.createRefreshToken(userId);

        LocalDateTime newExpiresAt =
                LocalDateTime.now().plusNanos(jwtTokenProvider.getRefreshTokenExpiration() * 1_000_000);

        refreshToken.update(newRefreshTokenValue, newExpiresAt);

        return new AuthTokenResponse(newAccessToken, newRefreshTokenValue);
    }

    @Transactional
    public void logout(Long userId) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        refreshTokenRepository.delete(refreshToken);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMe(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        return new MemberResponse(member);
    }
}