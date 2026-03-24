# Membership Domain

`membership` 테이블과 `Membership` 엔티티를 설명하는 문서다.

## 역할

- 회원이 보유한 이용권 정보를 관리한다.
- 횟수권과 기간권을 모두 표현한다.
- 예약, 출석, 환불 로직의 핵심 상태를 담당한다.

## 엔티티

대상 클래스: `com.lessonring.api.membership.domain.Membership`

테이블: `membership`

주요 필드:

- `id`
- `studioId`
- `memberId`
- `name`
- `type`
- `totalCount`
- `remainingCount`
- `startDate`
- `endDate`
- `status`
- `createdAt`, `createdBy`, `updatedAt`, `updatedBy`

## Enum

`MembershipType`

- `COUNT`
- `PERIOD`

`MembershipStatus`

- `ACTIVE`
- `USED_UP`
- `EXPIRED`
- `SUSPENDED`
- `REFUNDED`

## 현재 구현 규칙

- `startDate`는 `endDate`보다 늦을 수 없다.
- `COUNT` 타입은 `totalCount > 0`이어야 한다.
- 생성 시 `remainingCount = totalCount`, `status = ACTIVE`로 시작한다.
- `isAvailable()`은 상태, 기간, 잔여 횟수를 함께 검증한다.
- `useOnce()`는 횟수권이면 잔여 횟수를 차감하고, 모두 소진되면 `USED_UP`으로 변경한다.
- `refund()`가 호출되면 `REFUNDED` 상태가 된다.

## 현재 API

- `POST /api/v1/memberships`
- `GET /api/v1/memberships/{id}`
- `GET /api/v1/members/{memberId}/memberships`

## 연관 관계

- `member 1 : N membership`
- `studio 1 : N membership`
- `membership 1 : N booking`
- `payment 1 : 0..1 membership` 완료 후 연결
