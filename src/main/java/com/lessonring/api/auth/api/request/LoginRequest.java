package com.lessonring.api.auth.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "로그인 요청")
public class LoginRequest {

    @NotNull
    @Schema(description = "로그인할 사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;
}