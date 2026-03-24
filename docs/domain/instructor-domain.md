# Instructor Domain

`instructor` 테이블과 `Instructor` 엔티티를 설명하는 문서다.

## 역할

- 스튜디오 소속 강사 정보를 관리한다.
- 수업 일정의 담당자를 지정하는 기준 데이터다.

## 엔티티

대상 클래스: `com.lessonring.api.instructor.domain.Instructor`

테이블: `instructor`

주요 필드:

- `id`
- `studioId`
- `name`
- `phone`
- `email`
- `profileImageUrl`
- `status`
- `memo`
- `createdAt`, `createdBy`, `updatedAt`, `updatedBy`

## Enum

`InstructorStatus`

- `ACTIVE`
- `INACTIVE`
- `LEAVE`

## 현재 구현 규칙

- 강사는 반드시 `studioId`에 귀속된다.
- 현재 엔티티에는 생성/변경 메서드가 아직 없다.
- 서비스와 컨트롤러는 조회 골격만 있고 실제 조회 구현은 TODO 상태다.

## 현재 API

- `GET /api/v1/instructors/{id}` 예정, 현재 TODO

## 연관 관계

- `studio 1 : N instructor`
- `instructor 1 : N schedule`
