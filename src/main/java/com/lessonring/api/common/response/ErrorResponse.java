package com.lessonring.api.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "에러 응답")
public class ErrorResponse {

    @Schema(description = "에러 코드", example = "C004")
    private final String code;

    @Schema(description = "에러 메시지", example = "입력값이 올바르지 않습니다.")
    private final String message;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}