# QA - 결제 승인 동시성 테스트 케이스

## 1. 문서 기본 정보

| 항목 | 내용 |
|-----|-----|
| 문서명 | 결제 승인 동시성 테스트 케이스 |
| 기준 테스트 파일 | [PaymentPgServiceConcurrencyTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/application/PaymentPgServiceConcurrencyTest.java) |
| 모듈 | 결제 승인 |
| 테스트 유형 | 동시성 |
| 우선순위 | P0 |

## 2. 테스트 케이스 상세

### PAY-APP-CON-QA-001 서로 다른 멱등 키로 동일 결제 동시 승인

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 동일 결제 승인 가능 |
| 입력값 | 같은 paymentId, 다른 `idempotencyKey` 2개 |
| 수행절차 | 두 승인 요청 동시 실행 |
| 예상결과 | 1건 성공, 1건 실패, membership 1건만 생성, PG 호출 1회 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-APP-CON-QA-002 동일 멱등 키로 동일 결제 동시 승인

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 동일 결제 승인 가능 |
| 입력값 | 같은 paymentId, 같은 `idempotencyKey` |
| 수행절차 | 두 승인 요청 동시 실행 |
| 예상결과 | 실제 처리 1회, PG 호출 1회 |
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
