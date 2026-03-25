# 결제 웹훅 서비스 테스트 설계서

## 1. 대상 정보

- 테스트 파일: [PaymentWebhookServiceTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/application/PaymentWebhookServiceTest.java)
- 대상 계층: 결제 웹훅 처리 서비스
- 테스트 유형: 단위 테스트

## 2. 문서 목적

결제 webhook의 event type별 상태 전이, 요청 유효성 검증, 중복 transmission 처리, 이미 처리된 상태 재수신 시 정책을 검증한다.

## 3. 검증 범위

- `PAYMENT_COMPLETED`
- `PAYMENT_FAILED`
- `PAYMENT_CANCELED`
- 잘못된 요청 차단
- 알 수 없는 이벤트 무시
- 이미 처리된 상태 재수신 정책
- duplicate transmission 무시

## 4. 상세 테스트 케이스

| ID | 시나리오 | 입력 조건 | 수행 절차 | 기대 결과 |
|-----|-----|-----|-----|-----|
| PAY-WEBHOOK-UNIT-001 | completed webhook 정상 처리 | `READY` 상태 결제 | handle 호출 | 결제 `COMPLETED`, 결제 키 및 raw response 반영 |
| PAY-WEBHOOK-UNIT-002 | failed webhook 정상 처리 | `READY` 상태 결제 | handle 호출 | 결제 `FAILED`, 실패 사유 저장 |
| PAY-WEBHOOK-UNIT-003 | canceled webhook 정상 처리 | 완료 또는 취소 가능 결제 | handle 호출 | 결제 `CANCELED` |
| PAY-WEBHOOK-UNIT-004 | orderId 누락 | 잘못된 request | handle 호출 | 비즈니스 예외 발생 |
| PAY-WEBHOOK-UNIT-005 | 알 수 없는 eventType | 지원하지 않는 이벤트 | handle 호출 | 예외 없이 무시 |
| PAY-WEBHOOK-UNIT-006 | 이미 완료된 결제에 completed 재수신 | `COMPLETED` 상태 | handle 호출 | 상태 유지, 중복 완료 방지 |
| PAY-WEBHOOK-UNIT-007 | 이미 취소된 결제에 canceled 재수신 | `CANCELED` 상태 | handle 호출 | 상태 유지 |
| PAY-WEBHOOK-UNIT-008 | 이미 실패한 결제에 failed 재수신 | `FAILED` 상태 | handle 호출 | 상태 유지 |
| PAY-WEBHOOK-UNIT-009 | 동일 transmissionId 재수신 | 기존 로그 존재 | handle 호출 | 중복 처리 없이 종료 |

## 5. 합격 기준

- event type별 상태 전이가 정확해야 한다.
- 잘못된 요청은 조기에 차단되어야 한다.
- 재수신과 중복 수신은 상태를 다시 오염시키면 안 된다.
