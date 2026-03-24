# Schedule Domain

`schedule` 테이블과 `Schedule` 엔티티를 설명하는 문서다.

## 역할

- 수업 일정을 관리한다.
- 강사, 시간, 정원, 예약 수를 함께 가진다.
- 예약 가능 여부 판단의 기준이 된다.

## 엔티티

대상 클래스: `com.lessonring.api.schedule.domain.Schedule`

테이블: `schedule`

주요 필드:

- `id`
- `studioId`
- `instructorId`
- `title`
- `type`
- `startAt`
- `endAt`
- `capacity`
- `bookedCount`
- `status`
- `createdAt`, `createdBy`, `updatedAt`, `updatedBy`

## Enum

`ScheduleType`

- `PRIVATE`
- `GROUP`

`ScheduleStatus`

- `OPEN`
- `CLOSED`
- `CANCELED`

## 현재 구현 규칙

- 생성 시 `bookedCount = 0`, `status = OPEN`으로 시작한다.
- `startAt`은 `endAt`보다 빨라야 한다.
- `increaseBookedCount()`는 정원을 초과하면 실패한다.
- `decreaseBookedCount()`는 최소 0까지만 감소한다.

## 현재 API

- `POST /api/v1/schedules`
- `GET /api/v1/schedules/{id}`
- `GET /api/v1/schedules`

## 연관 관계

- `studio 1 : N schedule`
- `instructor 1 : N schedule`
- `schedule 1 : N booking`
- `schedule 1 : N attendance`
