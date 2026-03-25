# QA - 결제 교차 충돌 동시성 테스트 케이스

## 1. 문서 기본 정보

| 항목 | 내용 |
|-----|-----|
| 문서명 | 결제 교차 충돌 동시성 테스트 케이스 |
| 기준 테스트 파일 | [PaymentCrossConflictConcurrencyTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/application/PaymentCrossConflictConcurrencyTest.java) |
| 모듈 | 결제 |
| 테스트 유형 | 동시성 |
| 우선순위 | P0 |

## 2. 테스트 케이스 상세

### PAY-CROSS-QA-001 승인 API와 completed webhook 동시 진입

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | `READY` 상태 동일 결제 존재 |
| 입력값 | 승인 요청 1건, completed webhook 1건 |
| 수행절차 | 두 요청을 동시에 실행 |
| 예상결과 | 최종 `COMPLETED`, membership 1건, PG approve 1회 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-CROSS-QA-002 환불 API와 canceled webhook 동시 진입

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 완료된 동일 결제와 연결 이용권 존재 |
| 입력값 | 환불 요청 1건, canceled webhook 1건 |
| 수행절차 | 두 요청을 동시에 실행 |
| 예상결과 | 최종 `CANCELED`, 이용권 `REFUNDED`, webhook log 기록 |
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
