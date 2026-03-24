# Attendance Domain

`attendance` 테이블과 `Attendance` 엔티티를 설명하는 문서다.

## 역할

- 예약에 대한 실제 출석 기록을 관리한다.
- 출석 시 이용권 차감과 예약 상태 변경을 함께 수행한다.
- 출석 취소 시 예약 상태와 이용권 차감을 되돌린다.

## 엔티티

대상 클래스: `com.lessonring.api.attendance.domain.Attendance`

테이블: `attendance`

주요 필드:

- `id`
- `bookingId`
- `memberId`
- `scheduleId`
- `status`
- `checkedAt`
- `note`
- `createdAt`, `createdBy`, `updatedAt`, `updatedBy`

## Enum

`AttendanceStatus`

- `ATTENDED`
- `ABSENT`
- `CANCELED`

## 현재 구현 규칙

- 출석 생성 전에 동일 `bookingId`의 출석이 이미 있는지 확인한다.
- 출석 생성 시 예약 상태를 `ATTENDED`로 바꾸고 이용권을 차감한다.
- 출석 취소 시 예약 상태를 `RESERVED`로 복원한다.
- 횟수권이면 출석 취소 시 `restoreOnce()`로 차감을 되돌린다.

## 현재 API

- `POST /api/v1/attendances`
- `GET /api/v1/attendances/{id}`
- `GET /api/v1/attendances`
- `DELETE /api/v1/attendances/{id}`

## 연관 관계

- `booking 1 : 0..1 attendance`
- `member 1 : N attendance`
- `schedule 1 : N attendance`
