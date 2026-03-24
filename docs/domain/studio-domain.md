# Studio Domain

`studio` 테이블과 `Studio` 엔티티를 설명하는 문서다.

## 역할

- 스튜디오 기본 정보를 관리한다.
- 회원, 강사, 수업, 결제 등 운영 데이터의 최상위 귀속 단위다.

## 엔티티

대상 클래스: `com.lessonring.api.studio.domain.Studio`

테이블: `studio`

주요 필드:

- `id`
- `name`
- `phone`
- `address`
- `detailAddress`
- `timezone`
- `businessNumber`
- `status`
- `createdAt`, `createdBy`, `updatedAt`, `updatedBy`

## Enum

`StudioStatus`

- `ACTIVE`
- `INACTIVE`
- `CLOSED`

## 현재 구현 규칙

- 현재 엔티티에는 생성/수정 메서드가 구현되어 있지 않다.
- 서비스와 컨트롤러도 조회 골격만 있고 실제 조회 구현은 아직 TODO 상태다.

## 현재 API

- `GET /api/v1/studios/{id}` 예정, 현재 TODO

## 연관 관계

- `studio 1 : N member`
- `studio 1 : N instructor`
- `studio 1 : N schedule`
- `studio 1 : N membership`
- `studio 1 : N payment`
- `studio 1 : N notification`
