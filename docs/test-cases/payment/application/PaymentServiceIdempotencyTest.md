# 결제 생성 멱등성 테스트 설계서

## 1. 기본 정보

| 항목 | 내용 |
|-----|-----|
| 대상 파일 | [PaymentServiceIdempotencyTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/payment/application/PaymentServiceIdempotencyTest.java) |
| 대상 계층 | 결제 생성 서비스 |
| 테스트 유형 | 단위 |
| 주 우선순위 | P0 |
| 관련 기능 | 중복 결제 생성 방지 |

## 2. 테스트 목적

동일 생성 요청이 반복되어도 `idempotencyKey` 기준으로 중복 결제가 생성되지 않도록 보장하는지 검증한다.

## 3. 상세 테스트 케이스

| ID | 우선순위 | 유형 | 시나리오 | 기대 결과 |
|-----|-----|-----|-----|-----|
| PAY-CREATE-IDEMP-001 | P0 | 단위 | 기존 멱등 키 재사용 | 기존 Payment 반환 |
| PAY-CREATE-IDEMP-002 | P1 | 단위 | 멱등 키 없음 | 신규 Payment 생성 |
| PAY-CREATE-IDEMP-003 | P0 | 단위 | 새로운 멱등 키 사용 | 신규 Payment 생성 |

## 4. 판정 기준

- 기존 키 재사용 시 새 결제가 생성되면 실패다.

## 현재 테스트 메서드 기준

| 메서드 | DisplayName | 문서 케이스 |
|-----|-----|-----|
| `create_should_return_existing_payment_when_idempotency_key_exists` | 같은 idempotencyKey로 결제 생성 요청이 들어오면 기존 Payment를 반환한다 | `PAY-CREATE-IDEMP-001` |
| `create_should_create_new_payment_when_idempotency_key_is_absent` | idempotencyKey가 없으면 새로운 Payment를 생성한다 | `PAY-CREATE-IDEMP-002` |
| `create_should_create_new_payment_when_idempotency_key_is_new` | 새로운 idempotencyKey이면 새로운 Payment를 생성한다 | `PAY-CREATE-IDEMP-003` |
