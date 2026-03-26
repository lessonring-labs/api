package com.lessonring.api.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /**
     * =========================
     * Common (공통)
     * =========================
     */
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "C000", "잘못된 요청입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C001", "요청한 데이터를 찾을 수 없습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C002", "요청 값 타입이 올바르지 않습니다."),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "C003", "필수 요청 파라미터가 누락되었습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C004", "입력값이 올바르지 않습니다."),
    MESSAGE_NOT_READABLE(HttpStatus.BAD_REQUEST, "C005", "요청 값을 읽을 수 없습니다."),
    CONSTRAINT_VIOLATION(HttpStatus.BAD_REQUEST, "C006", "요청 값 제약 조건 위반입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C999", "서버 내부 오류입니다."),

    /**
     * =========================
     * Auth
     * =========================
     */
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A001", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "만료된 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "A003", "RefreshToken을 찾을 수 없습니다."),

    /**
     * =========================
     * Member
     * =========================
     */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "회원을 찾을 수 없습니다."),
    DUPLICATE_PHONE(HttpStatus.CONFLICT, "M002", "이미 사용 중인 전화번호입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "M003", "이미 사용 중인 이메일입니다."),

    /**
     * =========================
     * Schedule
     * =========================
     */
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "스케줄을 찾을 수 없습니다."),
    SCHEDULE_FULL(HttpStatus.BAD_REQUEST, "S002", "정원이 초과되었습니다."),

    /**
     * =========================
     * Booking
     * =========================
     */
    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "예약을 찾을 수 없습니다."),
    DUPLICATE_BOOKING(HttpStatus.BAD_REQUEST, "B002", "이미 예약이 존재합니다."),
    BOOKING_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "B003", "예약이 불가능한 상태입니다."),

    /**
     * =========================
     * Membership
     * =========================
     */
    MEMBERSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "MS001", "이용권을 찾을 수 없습니다."),
    MEMBERSHIP_EXPIRED(HttpStatus.BAD_REQUEST, "MS002", "만료된 이용권입니다."),
    MEMBERSHIP_USED_UP(HttpStatus.BAD_REQUEST, "MS003", "이용 횟수를 모두 사용한 이용권입니다."),
    MEMBERSHIP_REFUNDED(HttpStatus.BAD_REQUEST, "MS004", "환불된 이용권입니다."),

    /**
     * =========================
     * Attendance
     * =========================
     */
    ATTENDANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "AT001", "출석 정보를 찾을 수 없습니다."),
    ALREADY_ATTENDED(HttpStatus.BAD_REQUEST, "AT002", "이미 출석 처리된 예약입니다."),

    /**
     * =========================
     * Payment
     * =========================
     */
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "결제 정보를 찾을 수 없습니다."),
    PAYMENT_ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "P002", "이미 취소된 결제입니다."),
    PAYMENT_REFUND_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "P003", "환불할 수 없는 결제입니다."),
    PG_CANCEL_FAILED(HttpStatus.BAD_REQUEST, "P004", "PG 결제 취소에 실패했습니다."),
    PAYMENT_APPROVE_IN_PROGRESS(HttpStatus.CONFLICT, "P005", "이미 처리 중인 결제 승인 요청입니다."),
    PAYMENT_APPROVE_ALREADY_COMPLETED(HttpStatus.CONFLICT, "P006", "이미 완료된 결제입니다."),
    PAYMENT_LOCK_ACQUISITION_FAILED(HttpStatus.CONFLICT, "P007", "결제 처리 락 획득에 실패했습니다."),
    PAYMENT_WEBHOOK_INVALID_SIGNATURE(HttpStatus.BAD_REQUEST, "P008", "유효하지 않은 결제 webhook 서명입니다."),
    PAYMENT_WEBHOOK_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "P009", "결제 webhook PG 검증에 실패했습니다."),

    /**
     * =========================
     * Notification
     * =========================
     */
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "N001", "알림을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
