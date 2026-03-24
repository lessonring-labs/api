# ERD Flyway Migration Draft

이 문서는 현재 정리된 Flyway migration 구조와 baseline init schema 사용 기준을 함께 정리한 문서다.

- 기준 문서: `docs/architecture/erd.md`
- 대상 경로: `src/main/resources/db/migration`

## Current Migration Rule

- `V1` to `V12`: 테이블 생성 및 스키마 정의
- `V13`: 인덱스와 제약조건만 관리

현재 정리된 migration 순서:

1. `V1__create_studio.sql`
2. `V2__create_instructor.sql`
3. `V3__create_member.sql`
4. `V4__create_membership.sql`
5. `V5__create_schedule.sql`
6. `V6__create_booking.sql`
7. `V7__create_attendance.sql`
8. `V8__create_payment.sql`
9. `V9__create_notification.sql`
10. `V10__create_refresh_token.sql`
11. `V11__create_payment_webhook_log.sql`
12. `V12__create_payment_operation.sql`
13. `V13__add_constraints_and_indexes.sql`

정리 원칙:

- `payment` 관련 후속 컬럼 추가는 `V8__create_payment.sql`에 흡수
- `payment_webhook_log` 관련 후속 변경은 `V11__create_payment_webhook_log.sql`에 흡수
- 공통 인덱스와 unique 제약은 `V13__add_constraints_and_indexes.sql`에 모음

## Baseline Init Schema

baseline 파일 위치:

```text
src/main/resources/db/baseline/V1__init_schema.sql
```

용도:

- 새 데이터베이스를 처음부터 한 번에 구성할 때 사용
- 현재 `V1`부터 `V13`까지의 migration 결과를 압축한 초기 스키마 초안

주의:

- 이 파일은 현재 활성 Flyway migration 디렉터리에 그대로 넣어 쓰는 용도가 아니다.
- `src/main/resources/db/migration` 안에는 이미 `V1`이 존재하므로 버전 충돌이 난다.
- 기존 DB에는 baseline 파일을 직접 적용하지 말고, 현재 migration 이력을 유지해야 한다.
- 기존 환경을 baseline 체계로 바꾸려면 별도 re-baseline 절차가 필요하다.

## Initial Schema Draft Summary

baseline 파일은 아래 테이블을 포함한다.

- `studio`
- `instructor`
- `member`
- `membership`
- `schedule`
- `booking`
- `attendance`
- `payment`
- `notification`
- `refresh_token`
- `payment_webhook_log`
- `payment_operation`

또한 현재 `V13` 기준의 주요 인덱스와 제약을 함께 포함한다.

## Recommended Use

1. 신규 개발 환경이나 신규 DB 생성 시:
   `baseline/V1__init_schema.sql` 사용 가능
2. 기존 Flyway 이력이 있는 환경:
   `migration/V1`부터 `migration/V13`까지 그대로 유지
3. 운영 환경 재정리:
   baseline 전환 여부를 먼저 결정한 뒤 별도 migration 전략 수립
