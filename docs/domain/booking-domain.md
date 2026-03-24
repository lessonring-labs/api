# Booking Domain

`booking` 테이블과 `Booking` 엔티티를 설명하는 문서다.

## 역할

- 회원의 수업 예약 정보를 관리한다.
- 이용권, 회원, 수업을 연결하는 허브 역할을 한다.
- 예약 취소, 출석, no-show 상태 전환을 담당한다.

## 엔티티

대상 클래스: `com.lessonring.api.booking.domain.Booking`

테이블: `booking`

주요 필드:

- `id`
- `studioId`
- `memberId`
- `scheduleId`
- `membershipId`
- `status`
- `bookedAt`
- `canceledAt`
- `cancelReason`
- `createdAt`, `createdBy`, `updatedAt`, `updatedBy`

## Enum

`BookingStatus`

- `RESERVED`
- `ATTENDED`
- `CANCELED`
- `NO_SHOW`

## 현재 구현 규칙

- 생성 시 `status = RESERVED`, `bookedAt = now()`로 시작한다.
- 같은 회원이 같은 수업에 활성 예약을 중복 생성할 수 없다.
- 예약 생성 시 이용권 소유자, 스튜디오 일치, 예약 가능 상태를 함께 검증한다.
- 예약 취소 시 스케줄의 `bookedCount`를 감소시킨다.
- no-show 처리 시 조건에 따라 이용권을 차감한다.

## 현재 API

- `POST /api/v1/bookings`
- `GET /api/v1/bookings/{id}`
- `GET /api/v1/bookings`
- `PATCH /api/v1/bookings/{id}/cancel`
- `PATCH /api/v1/bookings/{id}/no-show`
- `PATCH /api/v1/bookings/no-show/process`

## 연관 관계

- `member 1 : N booking`
- `schedule 1 : N booking`
- `membership 1 : N booking`
- `booking 1 : 0..1 attendance`
