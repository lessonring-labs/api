# Git 작업 가이드

이 문서는 현재 LessonRing Backend 프로젝트의 Git 작업 기준을 정리한다.

목표:

- 변경 이력을 읽기 쉽게 유지한다.
- 기능, 수정, 문서, 설정 변경을 구분 가능하게 남긴다.
- migration, 설정 파일, 문서 변경처럼 영향 범위가 큰 작업을 안전하게 다룬다.

---

# Commit 형식

현재 프로젝트는 한글 커밋 타입을 사용한다.

기본 형식:

```text
타입(scope): 제목
```

필요하면 본문을 추가한다.

```text
타입(scope): 제목

- 변경 내용 1
- 변경 내용 2
```

예시:

```text
기능(core): 문서 정리
기능(payment): 결제 안정성 강화 및 webhook 멱등 처리 구조 확립
기능(payment): V17__create_payment_operation.sql 추가
기능(api): Swagger 문서화 및 예외 처리/환불 로직 고도화
기능(core): 백엔드 핵심 도메인 1차 구현
```

---

# Commit 타입

현재 프로젝트에서 사용하는 타입은 아래를 권장한다.

- `기능`: 기능 추가, 기능 확장, 구조 구현
- `수정`: 버그 수정, 동작 보정, 회귀 수정
- `리팩토링`: 동작 변화 없이 구조 개선
- `문서`: 문서 추가 및 수정
- `테스트`: 테스트 추가 및 수정
- `설정`: 설정, 빌드, 의존성, 실행 구성 변경

예시:

- `기능(member): 회원 조회 API 추가`
- `수정(booking): 중복 예약 검증 누락 수정`
- `리팩토링(common): 예외 응답 구조 정리`
- `문서(domain): payment-operation 문서 추가`
- `설정(build): redisson 버전 상향`

주의:

- 이미 팀에서 `기능` 중심으로 이력을 쌓고 있다면, 기존 히스토리 흐름을 깨지 않도록 같은 톤을 유지하는 것이 좋다.
- migration 추가나 구조 변경도 팀 합의에 따라 `기능(...)`으로 남길 수 있다.

---

# Scope 기준

현재 프로젝트 기준으로 다음 scope를 사용한다.

- `core`
- `api`
- `auth`
- `member`
- `membership`
- `schedule`
- `booking`
- `attendance`
- `payment`
- `notification`
- `studio`
- `instructor`
- `analytics`
- `common`
- `migration`
- `config`
- `docs`
- `build`
- `infra`

예시:

- `기능(schedule): 수업 일정 생성 API 추가`
- `수정(payment): webhook 중복 처리 보정`
- `문서(docs): ERD 문서 추가`
- `설정(config): application-local 설정 분리`

---

# 제목 작성 규칙

- 한 줄로 짧고 명확하게 쓴다.
- 가능하면 50자 안팎으로 유지한다.
- 구현 결과가 드러나게 쓴다.
- 마침표는 붙이지 않는다.

좋은 예시:

- `기능(booking): 예약 생성 API 추가`
- `문서(migration): baseline 사용 기준 정리`
- `설정(config): local/dev 프로필 분리`

피해야 할 예시:

- `예약 생성 API를 구현했습니다.`
- `여러 파일 수정`
- `수정사항 반영`

---

# 본문 작성 규칙

본문은 왜 바꿨는지, 무엇을 바꿨는지를 짧게 남긴다.

예시:

```text
수정(payment): webhook 로그 저장 조건 수정

- transmission id 기준 중복 저장 방지
- payment webhook log 인덱스 기준에 맞춰 로직 보정
```

---

# Commit 단위 규칙

기본 원칙:

- 하나의 목적을 가진 변경은 하나의 commit으로 묶는다.
- 서로 다른 성격의 변경은 가능한 분리한다.

권장 분리 예시:

1. 기능 구현
2. migration 추가 또는 변경
3. 문서 정리
4. 설정/빌드 변경

예시:

- `기능(payment): 결제 취소 API 추가`
- `설정(migration): payment_operation 테이블 추가`
- `문서(payment): 결제 도메인 문서 갱신`

---

# 현재 프로젝트에서 주의할 작업

## 1. Flyway migration

현재 migration 규칙:

- `V1`부터 `V12`까지는 테이블 생성 및 스키마 정의
- `V13`은 인덱스와 제약조건 전용 파일

현재 순서:

- `V1__create_studio.sql`
- `V2__create_instructor.sql`
- `V3__create_member.sql`
- `V4__create_membership.sql`
- `V5__create_schedule.sql`
- `V6__create_booking.sql`
- `V7__create_attendance.sql`
- `V8__create_payment.sql`
- `V9__create_notification.sql`
- `V10__create_refresh_token.sql`
- `V11__create_payment_webhook_log.sql`
- `V12__create_payment_operation.sql`
- `V13__create_payment_webhook_event.sql`
- `V14__add_constraints_and_indexes.sql`

주의:

- 이미 적용된 migration 파일의 버전이나 이름을 함부로 바꾸지 않는다.
- 운영 또는 공용 DB에 적용된 이력이 있으면 새 migration을 추가하는 방식으로 처리한다.
- baseline 파일은 `src/main/resources/db/baseline/V1__init_schema.sql`에 따로 관리한다.

권장 commit 예시:

- `기능(payment): payment_operation 테이블 추가`
- `설정(migration): payment 인덱스 및 제약조건 정리`
- `문서(migration): baseline 사용 기준 정리`

## 2. 설정 파일

현재 설정 구조:

- `src/main/resources/application.yml`: 공통 설정
- `src/main/resources/application-local.yml`: 로컬 개발용
- `src/main/resources/application-dev.yml`: 개발 서버용

원칙:

- 공통 설정은 `application.yml`에 둔다.
- 환경마다 달라지는 값은 프로필 파일로 분리한다.
- 민감 정보는 가능한 환경변수로 주입한다.

권장 commit 예시:

- `설정(config): local/dev 프로필 설정 분리`
- `설정(config): IntelliJ 실행 구성 추가`

## 3. IntelliJ 실행 구성

공유 실행 구성 파일:

- `.run/LessonRing Local.run.xml`
- `.run/LessonRing Dev.run.xml`

원칙:

- 팀에서 공통으로 쓰는 실행 구성은 `.run` 아래 공유 파일로 관리한다.
- 개인 전용 설정은 IDE 로컬 설정에만 둔다.

## 4. 문서

현재 문서는 `docs` 아래에서 관리한다.

주요 위치:

- `docs/architecture`
- `docs/domain`
- `docs/development`
- `docs/git.md`

문서 변경은 가능하면 관련 코드 변경과 함께 commit하거나, 별도 문서 commit으로 분리한다.

---

# 브랜치 작업 원칙

- 기능 작업은 작은 단위 브랜치에서 진행한다.
- 한 브랜치에서 여러 unrelated 작업을 섞지 않는다.
- PR 전에는 실행 불가 상태의 중간 commit을 정리한다.

권장 브랜치 예시:

- `feature/booking-cancel`
- `fix/payment-webhook-duplication`
- `chore/migration-index-cleanup`
- `docs/domain-update`

---

# PR 전 체크리스트

- 관련 없는 생성물(`build`, `.gradle`, `.idea`)이 포함되지 않았는지 확인
- migration 변경 시 순서와 영향 범위를 확인
- 설정 파일 변경 시 `local`, `dev` 동작 차이를 확인
- 문서 변경 시 실제 코드/설정과 내용이 일치하는지 확인
- 보안 경고와 관련된 판단은 `SECURITY.md`에 반영했는지 확인

---

# 현재 스타일 기준 추천 예시

- `기능(core): 문서 정리`
- `기능(payment): 결제 안정성 강화 및 webhook 멱등 처리 구조 확립`
- `기능(payment): payment_operation 테이블 추가`
- `기능(api): Swagger 문서화 및 예외 처리 고도화`
- `수정(payment): webhook 중복 저장 방지`
- `문서(domain): payment-operation 문서 추가`
- `설정(config): local/dev 실행 설정 추가`
- `설정(build): spring boot 및 redisson 버전 상향`
