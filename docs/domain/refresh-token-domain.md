# Refresh Token Domain

`refresh_token` 테이블과 `RefreshToken` 엔티티를 설명하는 문서다.

## 역할

- 로그인 이후 발급된 refresh token을 저장한다.
- 토큰 재발급과 로그아웃 흐름의 기준 데이터다.

## 엔티티

대상 클래스: `com.lessonring.api.auth.domain.RefreshToken`

테이블: `refresh_token`

주요 필드:

- `id`
- `userId`
- `token`
- `expiresAt`
- `createdAt`, `createdBy`, `updatedAt`, `updatedBy`

## 현재 구현 규칙

- `userId`는 현재 인증 기준상 `member.id`를 의미한다.
- 로그인 시 기존 토큰이 있으면 `update()`로 갱신하고, 없으면 새로 생성한다.
- 로그아웃 시 사용자 기준 refresh token을 삭제한다.

## 현재 API

- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout/{userId}`

## 연관 관계

- `member 1 : 0..1 refresh_token`
