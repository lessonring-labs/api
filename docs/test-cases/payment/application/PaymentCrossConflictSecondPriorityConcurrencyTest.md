# 결제 2순위 충돌 동시성 테스트 설계서

## 1. 기본 정보

| 항목 | 내용 |
|-----|-----|
| 대상 파일 | [PaymentCrossConflictSecondPriorityConcurrencyTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/application/PaymentCrossConflictSecondPriorityConcurrencyTest.java) |
| 대상 계층 | 결제 상태 충돌 우선순위 처리 |
| 테스트 유형 | 통합 / 동시성 |
| 주 우선순위 | P0 |
| 관련 기능 | 승인 vs 환불, completed webhook vs canceled webhook |

## 2. 테스트 목적

경쟁 상태에서 단순 선착순이 아니라 도메인 우선순위와 PG 검증 결과를 기반으로 최종 상태를 결정하는 정책이 유지되는지 확인한다.

## 3. 상세 테스트 케이스

| ID | 우선순위 | 유형 | 시나리오 | 입력 조건 | 기대 결과 |
|-----|-----|-----|-----|-----|-----|
| PAY-PRIORITY-001 | P0 | 동시성 | 승인 API와 환불 API 동시 진입 | `READY` 상태 결제 | 환불이 최종 승자가 되지 않음, `CANCELED` 확정 금지 |
| PAY-PRIORITY-002 | P0 | 동시성 | completed webhook와 canceled webhook 동시 진입, PG 결과 completed | 동일 주문, 상충 webhook | 최종 상태 `COMPLETED` |
| PAY-PRIORITY-003 | P0 | 동시성 | completed webhook와 canceled webhook 동시 진입, PG 결과 canceled | 동일 주문, 상충 webhook | 최종 상태 `CANCELED` |

## 4. 판정 기준

- 최종 상태는 사전에 정의된 정책과 일치해야 한다.
- 상충 이벤트가 동시에 들어와도 상태가 흔들리지 않아야 한다.
