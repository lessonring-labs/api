# Member Domain

`member` 테이블과 `Member` 엔티티를 설명하는 문서다.

## 역할

- 스튜디오 회원의 기본 정보를 관리한다.
- 이용권, 예약, 출석, 결제, 알림의 기준 엔티티로 사용된다.

## 엔티티

대상 클래스: `com.lessonring.api.member.domain.Member`

테이블: `member`

주요 필드:

- `id`
- `studioId`
- `name`
- `phone`
- `email`
- `gender`
- `birthDate`
- `status`
- `joinedAt`
- `memo`
- `createdAt`, `createdBy`, `updatedAt`, `updatedBy`

## Enum

`Gender`

- `MALE`
- `FEMALE`
- `NONE`

`MemberStatus`

- `ACTIVE`
- `INACTIVE`
- `BLOCKED`

## 현재 구현 규칙

- 회원 생성은 `Member.create(...)` 정적 팩토리로 처리한다.
- 생성 시 `status`는 `ACTIVE`, `joinedAt`은 현재 시각으로 설정된다.
- 회원은 반드시 `studioId`에 귀속된다.

## 현재 API

- `POST /api/v1/members`
- `GET /api/v1/members/{id}`
- `GET /api/v1/members`

## 연관 관계

- `studio 1 : N member`
- `member 1 : N membership`
- `member 1 : N booking`
- `member 1 : N attendance`
- `member 1 : N payment`
- `member 1 : N notification`
- `member 1 : 1 refresh_token` 논리 관계
