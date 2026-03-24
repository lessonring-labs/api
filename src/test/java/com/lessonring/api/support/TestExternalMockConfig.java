package com.lessonring.api.support;

import com.lessonring.api.payment.infrastructure.pg.PgClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestExternalMockConfig {

    @Bean
    PgClient pgClient() {
        return Mockito.mock(PgClient.class);
    }
}