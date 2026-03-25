# QA - 결제 컨트롤러 테스트 케이스

## 1. 문서 기본 정보

| 항목 | 내용 |
|-----|-----|
| 문서명 | 결제 컨트롤러 테스트 케이스 |
| 기준 테스트 파일 | [PaymentControllerTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/api/PaymentControllerTest.java) |
| 모듈 | 결제 |
| 테스트 유형 | API |
| 우선순위 | P1 |

## 2. 테스트 케이스 상세

### PAY-CTRL-QA-001 결제 승인 요청 성공

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | 승인 가능한 결제 존재 |
| 입력값 | 유효한 `paymentKey`, `orderId`, `amount` |
| 수행절차 | `/api/v1/payments/{id}/approve` 호출 |
| 예상결과 | `200 OK`, 성공 응답, `paymentId/status/paymentKey/amount` 반환 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-CTRL-QA-002 paymentKey 누락 시 validation 실패

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | API 호출 가능 |
| 입력값 | `paymentKey` 없는 요청 |
| 수행절차 | 승인 API 호출 |
| 예상결과 | `400 Bad Request` |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-CTRL-QA-003 amount 누락 시 validation 실패

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | API 호출 가능 |
| 입력값 | `amount` 없는 요청 |
| 수행절차 | 승인 API 호출 |
| 예상결과 | `400 Bad Request` |
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
