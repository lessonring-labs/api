package com.lessonring.api.common.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtTokenProviderImplTest {

    @Test
    void initSupportsRawTextSecret() {
        JwtTokenProviderImpl provider = createProvider("your-secret-key-your-secret-key-your-secret-key");

        String token = provider.createAccessToken(1L);

        assertThat(provider.validateToken(token)).isTrue();
        assertThat(provider.getUserId(token)).isEqualTo(1L);
    }

    @Test
    void initSupportsBase64Secret() {
        String secret = Base64.getEncoder()
                .encodeToString("your-secret-key-your-secret-key-your-secret-key".getBytes(StandardCharsets.UTF_8));
        JwtTokenProviderImpl provider = createProvider(secret);

        String token = provider.createAccessToken(2L);

        assertThat(provider.validateToken(token)).isTrue();
        assertThat(provider.getUserId(token)).isEqualTo(2L);
    }

    @Test
    void initSupportsBase64UrlSecret() {
        String secret = Base64.getUrlEncoder()
                .encodeToString("your-secret-key-your-secret-key-your-secret-key".getBytes(StandardCharsets.UTF_8));
        JwtTokenProviderImpl provider = createProvider(secret);

        String token = provider.createAccessToken(3L);

        assertThat(provider.validateToken(token)).isTrue();
        assertThat(provider.getUserId(token)).isEqualTo(3L);
    }

    private JwtTokenProviderImpl createProvider(String secret) {
        JwtTokenProviderImpl provider = new JwtTokenProviderImpl();
        ReflectionTestUtils.setField(provider, "secret", secret);
        ReflectionTestUtils.setField(provider, "accessTokenExpiration", 3600000L);
        ReflectionTestUtils.setField(provider, "refreshTokenExpiration", 1209600000L);
        provider.init();
        return provider;
    }
}
