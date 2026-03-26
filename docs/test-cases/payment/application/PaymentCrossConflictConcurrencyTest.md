# 결제 교차 충돌 동시성 테스트 설계서

## 1. 기본 정보

| 항목 | 내용 |
|-----|-----|
| 대상 파일 | [PaymentCrossConflictConcurrencyTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/payment/application/PaymentCrossConflictConcurrencyTest.java) |
| 대상 계층 | 승인/환불/웹훅 간 교차 충돌 처리 |
| 테스트 유형 | 통합 / 동시성 |
| 주 우선순위 | P0 |
| 관련 기능 | 승인 API, 환불 API, webhook 처리 경합 |

## 2. 테스트 목적

동일 결제에 대해 서로 다른 경로가 동시에 상태를 변경하려 할 때 최종 상태가 하나로 수렴하고 중복 처리 부작용이 발생하지 않는지 확인한다.

## 3. 핵심 위험

- 승인 API와 webhook completed가 동시에 성공하여 membership이 중복 생성될 수 있다.
- 환불 API와 webhook canceled가 동시에 성공하여 중복 취소 또는 중복 환불이 발생할 수 있다.

## 4. 상세 테스트 케이스

| ID | 우선순위 | 유형 | 시나리오 | 입력 조건 | 기대 결과 |
|-----|-----|-----|-----|-----|-----|
| PAY-CROSS-001 | P0 | 동시성 | 승인 API와 completed webhook 동시 진입 | 동일 payment, 동일 주문 | 최종 상태 `COMPLETED`, membership 1건, PG approve 1회 |
| PAY-CROSS-002 | P0 | 동시성 | 환불 API와 canceled webhook 동시 진입 | 완료된 동일 결제 | 최종 상태 `CANCELED`, 이용권 `REFUNDED`, webhook log 기록 |

## 5. 판정 기준

- 상태는 중복 반영되면 안 된다.
- 후속 자원 생성 또는 변경은 1회만 일어나야 한다.

## 6. 추적 포인트

- membership 개수
- payment 최종 상태
- webhook 로그 존재 여부
- PG 호출 횟수

## 현재 테스트 메서드 기준

| 메서드 | DisplayName | 문서 케이스 |
|-----|-----|-----|
| `approve_vs_webhook_completed` | approve API 와 webhook completed 동시 진입 시 최종 COMPLETED 1회만 반영된다 | `PAY-CROSS-001` |
| `refund_vs_webhook_canceled` | refund API 와 webhook canceled 동시 진입 시 최종 CANCELED 1회만 반영된다 | `PAY-CROSS-002` |
