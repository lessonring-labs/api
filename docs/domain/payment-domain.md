# Payment Domain

`payment` 테이블과 `Payment` 엔티티를 설명하는 문서다.

## 역할

- 결제 생성, 승인, 실패, 취소, 환불 흐름을 관리한다.
- 결제 시점의 이용권 정보를 스냅샷 형태로 저장한다.
- 결제 완료 시 실제 `membership`를 생성하고 연결한다.

## 엔티티

대상 클래스: `com.lessonring.api.payment.domain.Payment`

테이블: `payment`

주요 필드:

- `id`
- `studioId`
- `memberId`
- `membershipId`
- `orderName`
- `paymentMethod`
- `status`
- `amount`
- `paidAt`
- `canceledAt`
- `membershipName`
- `membershipType`
- `membershipTotalCount`
- `membershipStartDate`
- `membershipEndDate`
- `pgProvider`
- `pgOrderId`
- `pgPaymentKey`
- `pgRawResponse`
- `failedReason`
- `idempotencyKey`
- `createdAt`, `createdBy`, `updatedAt`, `updatedBy`

## Enum

`PaymentMethod`

- `CARD`
- `CASH`
- `TRANSFER`

`PaymentStatus`

- `READY`
- `COMPLETED`
- `CANCELED`
- `REFUNDED`
- `FAILED`

## 현재 구현 규칙

- 결제 생성 시 회원 존재 여부와 이용권 기간을 검증한다.
- 멱등 키가 있으면 동일 `idempotencyKey` 결제를 재사용한다.
- `complete()`는 `READY` 상태에서만 가능하며, 완료 시 `membership`를 생성한다.
- `refund()`는 `COMPLETED` 상태에서만 가능하며 미래 예약을 취소하고 이용권을 `REFUNDED`로 바꾼다.
- PG 승인/웹훅 처리에 필요한 외부 키를 저장한다.

## 현재 API

- `POST /api/v1/payments`
- `GET /api/v1/payments/{id}`
- `GET /api/v1/payments`
- `PATCH /api/v1/payments/{id}/complete`
- `PATCH /api/v1/payments/{id}/cancel`
- `PATCH /api/v1/payments/{id}/refund`
- `POST /api/v1/payments/{id}/refund`
- `POST /api/v1/payments/{id}/approve`

## 연관 관계

- `member 1 : N payment`
- `studio 1 : N payment`
- `payment 1 : 0..1 membership`
- `payment`는 `payment_webhook_log`와 `pgOrderId` 기반으로 논리 연결된다.
