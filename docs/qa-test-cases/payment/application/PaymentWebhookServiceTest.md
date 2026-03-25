# QA - 결제 웹훅 서비스 테스트 케이스

## 1. 문서 기본 정보

| 항목 | 내용 |
|-----|-----|
| 문서명 | 결제 웹훅 서비스 테스트 케이스 |
| 기준 테스트 파일 | [PaymentWebhookServiceTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/application/PaymentWebhookServiceTest.java) |
| 모듈 | 결제 웹훅 |
| 테스트 유형 | 단위 |
| 우선순위 | P0 |

## 2. 테스트 케이스 상세

### PAY-WEBHOOK-QA-001 completed webhook 처리

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | `READY` 상태 결제 존재 |
| 입력값 | `PAYMENT_COMPLETED` webhook |
| 수행절차 | handle 호출 |
| 예상결과 | 결제 `COMPLETED`, 결제 키 반영 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-WEBHOOK-QA-002 failed webhook 처리

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | `READY` 상태 결제 존재 |
| 입력값 | `PAYMENT_FAILED` webhook |
| 수행절차 | handle 호출 |
| 예상결과 | 결제 `FAILED`, 실패 사유 저장 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-WEBHOOK-QA-003 canceled webhook 처리

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 취소 가능 결제 존재 |
| 입력값 | `PAYMENT_CANCELED` webhook |
| 수행절차 | handle 호출 |
| 예상결과 | 결제 `CANCELED` |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-WEBHOOK-QA-004 orderId 누락 요청 차단

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | webhook 처리 가능 |
| 입력값 | `orderId` 없는 request |
| 수행절차 | handle 호출 |
| 예상결과 | 비즈니스 예외 발생 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-WEBHOOK-QA-005 알 수 없는 eventType 무시

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | webhook 처리 가능 |
| 입력값 | 지원하지 않는 eventType |
| 수행절차 | handle 호출 |
| 예상결과 | 예외 없이 종료, 상태 변경 없음 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-WEBHOOK-QA-006 이미 완료된 결제에 completed 재수신

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 결제 상태 `COMPLETED` |
| 입력값 | `PAYMENT_COMPLETED` webhook |
| 수행절차 | handle 호출 |
| 예상결과 | 상태 유지 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-WEBHOOK-QA-007 이미 취소된 결제에 canceled 재수신

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 결제 상태 `CANCELED` |
| 입력값 | `PAYMENT_CANCELED` webhook |
| 수행절차 | handle 호출 |
| 예상결과 | 상태 유지 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-WEBHOOK-QA-008 이미 실패한 결제에 failed 재수신

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 결제 상태 `FAILED` |
| 입력값 | `PAYMENT_FAILED` webhook |
| 수행절차 | handle 호출 |
| 예상결과 | 상태 유지 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-WEBHOOK-QA-009 동일 transmissionId 재수신 무시

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 동일 transmissionId 로그 존재 |
| 입력값 | 동일 transmissionId webhook |
| 수행절차 | handle 호출 |
| 예상결과 | 중복 처리 없이 종료 |
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
