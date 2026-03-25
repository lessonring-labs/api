# QA - 결제 2순위 충돌 동시성 테스트 케이스

## 1. 문서 기본 정보

| 항목 | 내용 |
|-----|-----|
| 문서명 | 결제 2순위 충돌 동시성 테스트 케이스 |
| 기준 테스트 파일 | [PaymentCrossConflictSecondPriorityConcurrencyTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/application/PaymentCrossConflictSecondPriorityConcurrencyTest.java) |
| 모듈 | 결제 |
| 테스트 유형 | 동시성 |
| 우선순위 | P0 |

## 2. 테스트 케이스 상세

### PAY-PRIORITY-QA-001 승인 API와 환불 API 동시 진입

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | `READY` 상태 동일 결제 존재 |
| 입력값 | 승인 요청 1건, 환불 요청 1건 |
| 수행절차 | 두 요청 동시 실행 |
| 예상결과 | 최종 상태가 `CANCELED`로 확정되지 않음 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-PRIORITY-QA-002 completed webhook와 canceled webhook 동시 진입, PG 결과 completed

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 동일 주문 상충 webhook 처리 가능 |
| 입력값 | completed webhook, canceled webhook |
| 수행절차 | 동시 실행 |
| 예상결과 | 최종 상태 `COMPLETED` |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-PRIORITY-QA-003 completed webhook와 canceled webhook 동시 진입, PG 결과 canceled

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 동일 주문 상충 webhook 처리 가능 |
| 입력값 | completed webhook, canceled webhook |
| 수행절차 | 동시 실행 |
| 예상결과 | 최종 상태 `CANCELED` |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

## 3. 수행 요약

| 항목 | 내용 |
|-----|-----|
| 수행자 |  |
| 수행일 |  |
| 수행 버전 |  |
| 결과 요약 |  |
