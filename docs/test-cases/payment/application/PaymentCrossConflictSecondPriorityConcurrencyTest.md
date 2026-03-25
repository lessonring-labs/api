# 결제 2순위 충돌 동시성 테스트 설계서

## 1. 대상 정보

- 테스트 파일: [PaymentCrossConflictSecondPriorityConcurrencyTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/application/PaymentCrossConflictSecondPriorityConcurrencyTest.java)
- 대상 계층: 결제 상태 충돌 우선순위 처리
- 테스트 유형: 통합 동시성 테스트

## 2. 문서 목적

동일 결제에 대해 상충하는 요청이 동시에 들어왔을 때, 단순 선착순이 아니라 정의된 우선순위와 PG 검증 결과에 따라 최종 상태가 결정되는지 확인한다.

## 3. 상세 테스트 케이스

| ID | 시나리오 | 입력 조건 | 수행 절차 | 기대 결과 |
|-----|-----|-----|-----|-----|
| PAY-PRIORITY-001 | 승인 API와 환불 API 동시 진입 | `READY` 상태 결제 | 승인/환불 스레드 동시 실행 | `REFUND`가 최종 승자가 되지 못함, `CANCELED` 확정 금지 |
| PAY-PRIORITY-002 | completed webhook와 canceled webhook 동시 진입, PG 결과 completed | 동일 주문, 상충 webhook | 두 webhook 동시 실행 | 최종 상태 `COMPLETED` |
| PAY-PRIORITY-003 | completed webhook와 canceled webhook 동시 진입, PG 결과 canceled | 동일 주문, 상충 webhook | 두 webhook 동시 실행 | 최종 상태 `CANCELED` |

## 4. 합격 기준

- 최종 상태는 테스트가 의도한 우선순위 정책과 일치해야 한다.
- PG 검증 결과가 우선순위를 뒤집는 핵심 근거로 작동해야 한다.
