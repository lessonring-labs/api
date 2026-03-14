package com.lessonring.api.auth.api;

import com.lessonring.api.auth.api.request.LoginRequest;
import com.lessonring.api.auth.api.request.RefreshTokenRequest;
import com.lessonring.api.auth.api.response.AuthTokenResponse;
import com.lessonring.api.auth.application.AuthService;
import com.lessonring.api.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request.getUserId()));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(authService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/logout/{userId}")
    public ApiResponse<Void> logout(@PathVariable Long userId) {
        authService.logout(userId);
        return ApiResponse.success();
    }
}