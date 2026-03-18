package com.lessonring.api.common.error;

import com.lessonring.api.common.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        log.warn("[BusinessException] code={}, message={}",
                errorCode.getCode(),
                e.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(
                        errorCode.getCode(),
                        e.getMessage()
                ));
    }

    /**
     * @Valid 검증 실패 (RequestBody DTO)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .orElse(null);

        String message = fieldError != null
                ? String.format("[%s] %s", fieldError.getField(), fieldError.getDefaultMessage())
                : ErrorCode.INVALID_INPUT_VALUE.getMessage();

        log.warn("[MethodArgumentNotValidException] message={}", message);

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getHttpStatus())
                .body(ApiResponse.fail(
                        ErrorCode.INVALID_INPUT_VALUE.getCode(),
                        message
                ));
    }

    /**
     * @RequestParam / @PathVariable 제약 조건 위반
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("[ConstraintViolationException] message={}", e.getMessage());

        return ResponseEntity
                .status(ErrorCode.CONSTRAINT_VIOLATION.getHttpStatus())
                .body(ApiResponse.fail(
                        ErrorCode.CONSTRAINT_VIOLATION.getCode(),
                        e.getMessage()
                ));
    }

    /**
     * PathVariable / RequestParam 타입 불일치
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = String.format("[%s] 요청 값 타입이 올바르지 않습니다. 입력값=%s",
                e.getName(), e.getValue());

        log.warn("[MethodArgumentTypeMismatchException] message={}", message);

        return ResponseEntity
                .status(ErrorCode.INVALID_TYPE_VALUE.getHttpStatus())
                .body(ApiResponse.fail(
                        ErrorCode.INVALID_TYPE_VALUE.getCode(),
                        message
                ));
    }

    /**
     * 필수 RequestParam 누락
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String message = String.format("[%s] 필수 요청 파라미터가 누락되었습니다.", e.getParameterName());

        log.warn("[MissingServletRequestParameterException] message={}", message);

        return ResponseEntity
                .status(ErrorCode.MISSING_REQUEST_PARAMETER.getHttpStatus())
                .body(ApiResponse.fail(
                        ErrorCode.MISSING_REQUEST_PARAMETER.getCode(),
                        message
                ));
    }

    /**
     * JSON 파싱 실패, enum 값 오류, 날짜 형식 오류 등
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("[HttpMessageNotReadableException] message={}", e.getMessage());

        return ResponseEntity
                .status(ErrorCode.MESSAGE_NOT_READABLE.getHttpStatus())
                .body(ApiResponse.fail(
                        ErrorCode.MESSAGE_NOT_READABLE.getCode(),
                        ErrorCode.MESSAGE_NOT_READABLE.getMessage()
                ));
    }

    /**
     * 기타 모든 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("[Exception] message={}", e.getMessage(), e);

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(ApiResponse.fail(
                        ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
                ));
    }
}