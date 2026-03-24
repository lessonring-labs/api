# Notification Domain

`notification` 테이블과 `Notification` 엔티티를 설명하는 문서다.

## 역할

- 회원에게 전달할 알림 이력을 저장한다.
- 읽음 처리를 통해 사용자별 알림 상태를 관리한다.

## 엔티티

대상 클래스: `com.lessonring.api.notification.domain.Notification`

테이블: `notification`

주요 필드:

- `id`
- `studioId`
- `memberId`
- `title`
- `content`
- `type`
- `readAt`
- `createdAt`, `createdBy`, `updatedAt`, `updatedBy`

## 현재 구현 규칙

- 알림은 스튜디오와 회원에 귀속된다.
- `read()` 호출 시 `readAt = now()`로 기록한다.
- 조회는 회원 기준 내림차순 목록 조회를 사용한다.

## 현재 API

- `GET /api/v1/notifications?memberId={memberId}`
- `PATCH /api/v1/notifications/{id}/read`

## 연관 관계

- `member 1 : N notification`
- `studio 1 : N notification`
