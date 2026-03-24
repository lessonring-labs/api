# Payment Webhook Log Domain

`payment_webhook_log` 테이블과 `PaymentWebhookLog` 엔티티를 설명하는 문서다.

## 역할

- PG webhook 수신 이력을 저장한다.
- 중복 수신 방지와 감사 로그 용도로 사용된다.

## 엔티티

대상 클래스: `com.lessonring.api.payment.domain.PaymentWebhookLog`

테이블: `payment_webhook_log`

주요 필드:

- `id`
- `provider`
- `transmissionId`
- `eventType`
- `orderId`
- `paymentKey`
- `payload`
- `createdAt`, `createdBy`, `updatedAt`, `updatedBy`

## 현재 구현 규칙

- 현재 구현은 `provider = TOSS`를 사용한다.
- `provider + transmissionId` 조합으로 중복 webhook를 판별한다.
- `orderId`를 기준으로 `payment.pgOrderId`와 논리적으로 연결된다.
- 처리 가능한 이벤트는 `PAYMENT_COMPLETED`, `PAYMENT_FAILED`, `PAYMENT_CANCELED`다.

## 현재 API

- `POST /api/v1/payments/webhook`

## 연관 관계

- `payment_webhook_log N : 1 payment` 논리 관계
- 물리 FK 대신 `orderId`와 `paymentKey` 기반 추적을 사용한다.
