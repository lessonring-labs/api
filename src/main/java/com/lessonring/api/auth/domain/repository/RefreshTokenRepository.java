package com.lessonring.api.auth.domain.repository;

import com.lessonring.api.auth.domain.RefreshToken;
import java.util.Optional;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByUserId(Long userId);

    Optional<RefreshToken> findByToken(String token);

    void delete(RefreshToken refreshToken);

    void deleteByUserId(Long userId);
}