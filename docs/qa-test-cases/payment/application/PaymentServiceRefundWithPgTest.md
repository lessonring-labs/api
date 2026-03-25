# QA - 결제 환불 PG 연동 테스트 케이스

## 1. 문서 기본 정보

| 항목 | 내용 |
|-----|-----|
| 문서명 | 결제 환불 PG 연동 테스트 케이스 |
| 기준 테스트 파일 | [PaymentServiceRefundWithPgTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/application/PaymentServiceRefundWithPgTest.java) |
| 모듈 | 결제 환불 |
| 테스트 유형 | 단위 |
| 우선순위 | P0 |

## 2. 테스트 케이스 상세

### PAY-REF-PG-QA-001 PG 취소 성공 시 내부 환불 완료

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 완료된 결제, 연결 이용권 존재, PG 성공 응답 |
| 입력값 | 대상 paymentId |
| 수행절차 | refund 호출 |
| 예상결과 | 환불 응답 반환, 결제 `CANCELED` |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-REF-PG-QA-002 PG 취소 실패 시 예외 발생

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 완료된 결제, PG 실패 응답 |
| 입력값 | 대상 paymentId |
| 수행절차 | refund 호출 |
| 예상결과 | 비즈니스 예외 발생 |
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
