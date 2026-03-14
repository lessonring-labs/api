# Error Handling

## 개요

LessonRing API는 Global Exception Handler를 사용하여
모든 예외를 일관된 방식으로 처리한다.

Spring의 `@RestControllerAdvice`를 사용한다.

---

## 예외 처리 구조

모든 비즈니스 예외는 `BusinessException` + `ErrorCode` 조합으로 처리한다.

```java
throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
```

### BusinessException

```java
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
```

### GlobalExceptionHandler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode.getCode(), errorCode.getMessage()));
    }
}
```

---

## Error Code 규칙

에러 코드는 `ErrorCode` enum으로 관리한다.

코드 형식: `{DOMAIN}_{번호}`

### Common

| 코드 | HTTP Status | 설명 |
|------|-------------|------|
| C001 | 400 | 잘못된 요청 |
| C002 | 404 | 데이터를 찾을 수 없음 |
| C003 | 500 | 서버 내부 오류 |

### Auth

| 코드 | HTTP Status | 설명 |
|------|-------------|------|
| AUTH_001 | 401 | 유효하지 않은 토큰 |
| AUTH_002 | 401 | 만료된 토큰 |
| AUTH_003 | 401 | 리프레시 토큰 미존재 |

### Member

| 코드 | HTTP Status | 설명 |
|------|-------------|------|
| MEMBER_001 | 404 | 회원 미존재 |
| MEMBER_002 | 409 | 전화번호 중복 |

### Membership

| 코드 | HTTP Status | 설명 |
|------|-------------|------|
| MEMBERSHIP_001 | 404 | 이용권 미존재 |
| MEMBERSHIP_002 | 400 | 이용권 만료 |
| MEMBERSHIP_003 | 400 | 잔여 횟수 없음 |

### Schedule

| 코드 | HTTP Status | 설명 |
|------|-------------|------|
| SCHEDULE_001 | 404 | 수업 일정 미존재 |
| SCHEDULE_002 | 409 | 수업 정원 초과 |

### Booking

| 코드 | HTTP Status | 설명 |
|------|-------------|------|
| BOOKING_001 | 404 | 예약 미존재 |
| BOOKING_002 | 409 | 중복 예약 |
| BOOKING_003 | 400 | 이미 취소된 예약 |

### Attendance

| 코드 | HTTP Status | 설명 |
|------|-------------|------|
| ATTENDANCE_001 | 404 | 출석 기록 미존재 |
| ATTENDANCE_002 | 409 | 이미 출석 처리됨 |

### Payment

| 코드 | HTTP Status | 설명 |
|------|-------------|------|
| PAYMENT_001 | 404 | 결제 정보 미존재 |
| PAYMENT_002 | 400 | 이미 완료된 결제 |
| PAYMENT_003 | 400 | 취소 불가 결제 |

### Studio

| 코드 | HTTP Status | 설명 |
|------|-------------|------|
| STUDIO_001 | 404 | 스튜디오 미존재 |

### Instructor

| 코드 | HTTP Status | 설명 |
|------|-------------|------|
| INSTRUCTOR_001 | 404 | 강사 미존재 |

---

## HTTP Status 규칙

| Status | 용도 |
|--------|------|
| 200 OK | 정상 처리 |
| 201 CREATED | 리소스 생성 |
| 400 BAD_REQUEST | 잘못된 요청 / 비즈니스 규칙 위반 |
| 401 UNAUTHORIZED | 인증 실패 |
| 403 FORBIDDEN | 권한 없음 |
| 404 NOT_FOUND | 리소스 미존재 |
| 409 CONFLICT | 중복 / 충돌 |
| 500 INTERNAL_SERVER_ERROR | 서버 내부 오류 |

---

## 에러 코드 추가 규칙

새로운 에러 코드 추가 시 다음 규칙을 따른다.

1. `ErrorCode` enum에 도메인 그룹별로 추가한다
2. 코드 형식은 `{DOMAIN}_{번호}`를 따른다
3. 번호는 도메인 내에서 순차적으로 부여한다
4. HTTP Status는 의미에 맞게 선택한다
