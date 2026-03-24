# Payment Operation Domain

`payment_operation` 테이블과 `PaymentOperation` 엔티티를 설명하는 문서다.

## 역할

- 결제 승인/환불 같은 결제 후속 작업의 멱등 처리 상태를 저장한다.
- 같은 요청이 반복되더라도 동일 작업으로 식별하고 재사용할 수 있게 한다.
- 성공 시 응답 payload를 저장해 재응답 복원에 사용한다.

## 엔티티

대상 클래스: `com.lessonring.api.payment.domain.PaymentOperation`

테이블: `payment_operation`

주요 필드:

- `id`
- `paymentId`
- `operationType`
- `idempotencyKey`
- `requestHash`
- `status`
- `providerReference`
- `responsePayload`
- `errorCode`
- `errorMessage`
- `createdAt`, `createdBy`, `updatedAt`, `updatedBy`

## Enum

`OperationType`

- `APPROVE`
- `REFUND`

`OperationStatus`

- `PROCESSING`
- `SUCCEEDED`
- `FAILED`

## 현재 구현 규칙

- 유니크 키는 `(paymentId, operationType, idempotencyKey)` 조합이다.
- 같은 키로 요청이 다시 들어오면 기존 작업을 재사용한다.
- 같은 idempotency key라도 `requestHash`가 다르면 예외를 발생시킨다.
- 새 작업 생성 시 상태는 `PROCESSING`으로 시작한다.
- 성공 시 `providerReference`, `responsePayload`를 저장하고 상태를 `SUCCEEDED`로 변경한다.
- 실패 시 `errorCode`, `errorMessage`를 저장하고 상태를 `FAILED`로 변경한다.

## 현재 구현 사용처

- `PaymentOperationService.startOrGet(...)`
- `PaymentOperationService.restoreRefundResponse(...)`
- `PaymentService`의 환불 멱등 처리 흐름

## 연관 관계

- `payment 1 : N payment_operation`
- 물리적으로는 `paymentId`를 저장하며, JPA 연관관계 대신 ID 참조를 사용한다.
