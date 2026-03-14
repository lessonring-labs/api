package com.lessonring.api.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "C001", "잘못된 요청입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C002", "데이터를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "서버 내부 오류입니다."),

    // Auth
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_001", "유효하지 않은 토큰입니다."),
    AUTH_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_002", "만료된 토큰입니다."),
    AUTH_REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH_003", "리프레시 토큰을 찾을 수 없습니다."),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_001", "회원을 찾을 수 없습니다."),
    MEMBER_DUPLICATE_PHONE(HttpStatus.CONFLICT, "MEMBER_002", "이미 등록된 전화번호입니다."),

    // Membership
    MEMBERSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBERSHIP_001", "이용권을 찾을 수 없습니다."),
    MEMBERSHIP_EXPIRED(HttpStatus.BAD_REQUEST, "MEMBERSHIP_002", "만료된 이용권입니다."),
    MEMBERSHIP_NO_REMAINING(HttpStatus.BAD_REQUEST, "MEMBERSHIP_003", "잔여 횟수가 없습니다."),

    // Schedule
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHEDULE_001", "수업 일정을 찾을 수 없습니다."),
    SCHEDULE_FULL(HttpStatus.CONFLICT, "SCHEDULE_002", "수업 정원이 초과되었습니다."),

    // Booking
    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKING_001", "예약을 찾을 수 없습니다."),
    BOOKING_DUPLICATE(HttpStatus.CONFLICT, "BOOKING_002", "이미 예약된 수업입니다."),
    BOOKING_ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "BOOKING_003", "이미 취소된 예약입니다."),

    // Attendance
    ATTENDANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "ATTENDANCE_001", "출석 기록을 찾을 수 없습니다."),
    ATTENDANCE_ALREADY_RECORDED(HttpStatus.CONFLICT, "ATTENDANCE_002", "이미 출석 처리되었습니다."),

    // Payment
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_001", "결제 정보를 찾을 수 없습니다."),
    PAYMENT_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "PAYMENT_002", "이미 완료된 결제입니다."),
    PAYMENT_CANCEL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "PAYMENT_003", "취소할 수 없는 결제입니다."),

    // Studio
    STUDIO_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDIO_001", "스튜디오를 찾을 수 없습니다."),

    // Instructor
    INSTRUCTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "INSTRUCTOR_001", "강사를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}