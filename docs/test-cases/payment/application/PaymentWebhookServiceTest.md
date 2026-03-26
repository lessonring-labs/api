# 결제 웹훅 서비스 테스트 설계서

## 1. 기본 정보

| 항목 | 내용 |
|-----|-----|
| 대상 파일 | [PaymentWebhookServiceTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/payment/application/PaymentWebhookServiceTest.java) |
| 대상 계층 | 결제 웹훅 처리 서비스 |
| 테스트 유형 | 단위 |
| 주 우선순위 | P0 |
| 관련 기능 | completed/failed/canceled 처리, 요청 검증, 중복 transmission 차단 |

## 2. 테스트 목적

webhook event type별 상태 전이와 입력 검증, 이미 처리된 상태 재수신 정책이 정확한지 검증한다.

## 3. 상세 테스트 케이스

| ID | 우선순위 | 유형 | 시나리오 | 기대 결과 |
|-----|-----|-----|-----|-----|
| PAY-WEBHOOK-UNIT-001 | P0 | 단위 | completed webhook 처리 | 결제 `COMPLETED` 반영 |
| PAY-WEBHOOK-UNIT-002 | P0 | 단위 | failed webhook 처리 | 결제 `FAILED` 반영 |
| PAY-WEBHOOK-UNIT-003 | P0 | 단위 | canceled webhook 처리 | 결제 `CANCELED` 반영 |
| PAY-WEBHOOK-UNIT-004 | P1 | 단위 | orderId 누락 | 비즈니스 예외 발생 |
| PAY-WEBHOOK-UNIT-005 | P1 | 단위 | 알 수 없는 eventType | 예외 없이 무시 |
| PAY-WEBHOOK-UNIT-006 | P0 | 단위 | 이미 완료된 결제에 completed 재수신 | 상태 유지 |
| PAY-WEBHOOK-UNIT-007 | P0 | 단위 | 이미 취소된 결제에 canceled 재수신 | 상태 유지 |
| PAY-WEBHOOK-UNIT-008 | P0 | 단위 | 이미 실패한 결제에 failed 재수신 | 상태 유지 |
| PAY-WEBHOOK-UNIT-009 | P0 | 단위 | 동일 transmissionId 재수신 | 중복 처리 없이 종료 |

## 4. 판정 기준

- 잘못된 요청은 조기에 차단되어야 한다.
- 중복 수신으로 상태가 재오염되면 실패다.

## 현재 테스트 메서드 기준

| 메서드 | DisplayName | 문서 케이스 |
|-----|-----|-----|
| `handle_completed_webhook_success` | completed webhook updates payment to completed | `PAY-WEBHOOK-UNIT-001` |
| `handle_failed_webhook_success` | failed webhook updates payment to failed | `PAY-WEBHOOK-UNIT-002` |
| `handle_canceled_webhook_success` | canceled webhook updates payment to canceled | `PAY-WEBHOOK-UNIT-003` |
| `handle_webhook_fail_when_order_id_missing` | missing orderId throws business exception | `PAY-WEBHOOK-UNIT-004` |
| `handle_webhook_ignore_unknown_event_type` | unknown eventType is ignored | `PAY-WEBHOOK-UNIT-005` |
| `handle_completed_webhook_should_ignore_when_already_completed` | already completed payment ignores completed webhook | `PAY-WEBHOOK-UNIT-006` |
| `handle_canceled_webhook_should_ignore_when_already_canceled` | already canceled payment ignores canceled webhook | `PAY-WEBHOOK-UNIT-007` |
| `handle_failed_webhook_should_ignore_when_already_failed` | already failed payment ignores failed webhook | `PAY-WEBHOOK-UNIT-008` |
| `handle_should_ignore_when_same_transmission_id_already_processed` | duplicate transmissionId is ignored | `PAY-WEBHOOK-UNIT-009` |
