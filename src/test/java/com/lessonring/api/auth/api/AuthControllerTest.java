package com.lessonring.api.auth.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lessonring.api.auth.api.request.LoginRequest;
import com.lessonring.api.auth.api.request.RefreshTokenRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("로그인 요청 시 userId가 없으면 validation 에러가 발생한다")
    void login_validation_fail_userId_null() throws Exception {
        LoginRequest request = new LoginRequest();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("C004"));
    }

    @Test
    @DisplayName("토큰 재발급 요청 시 refreshToken이 없으면 validation 에러가 발생한다")
    void refresh_validation_fail_refreshToken_blank() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest();

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("C004"));
    }


    @Test
    @WithMockUser
    @DisplayName("로그아웃 요청 시 userId path variable 타입이 올바르지 않으면 타입 에러가 발생한다")
    void logout_validation_fail_userId_type_mismatch() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout/abc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("C002"));
    }
}