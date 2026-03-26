# 결제 승인 동시성 테스트 설계서

## 1. 기본 정보

| 항목 | 내용 |
|-----|-----|
| 대상 파일 | [PaymentPgServiceConcurrencyTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/payment/application/PaymentPgServiceConcurrencyTest.java) |
| 대상 계층 | 결제 승인 서비스 |
| 테스트 유형 | 통합 / 동시성 |
| 주 우선순위 | P0 |
| 관련 기능 | 승인 멱등성, operation 기록, PG 중복 호출 방지 |

## 2. 테스트 목적

동일 결제에 대해 승인 요청이 동시에 들어와도 PG 호출과 상태 반영이 중복되지 않는지 확인한다.

## 3. 상세 테스트 케이스

| ID | 우선순위 | 유형 | 시나리오 | 입력 조건 | 기대 결과 |
|-----|-----|-----|-----|-----|-----|
| PAY-APP-CON-001 | P0 | 동시성 | 서로 다른 멱등 키 동시 승인 | 동일 결제, 다른 `idempotencyKey` | 1건 성공, 1건 실패, membership 1건 |
| PAY-APP-CON-002 | P0 | 동시성 | 동일 멱등 키 동시 승인 | 동일 결제, 동일 `idempotencyKey` | 실제 처리 1회, PG 호출 1회 |

## 4. 판정 기준

- `PaymentOperation` 상태가 정책대로 저장되어야 한다.
- membership 중복 생성 시 실패다.
- 외부 PG 호출 2회 이상이면 실패다.

## 현재 테스트 메서드 기준

| 메서드 | DisplayName | 문서 케이스 |
|-----|-----|-----|
| `approve_concurrent_with_different_idempotency_keys_only_one_succeeds` | 서로 다른 idempotencyKey로 동일 payment 동시 approve 시 1건만 완료된다 | `PAY-APP-CON-001` |
| `approve_concurrent_with_same_idempotency_key_calls_pg_once` | 동일 idempotencyKey로 동일 payment 동시 approve 시 최종 1건만 처리되고 PG 호출은 1회다 | `PAY-APP-CON-002` |
