# QA - 결제 환불 통합 테스트 케이스

## 1. 문서 기본 정보

| 항목 | 내용 |
|-----|-----|
| 문서명 | 결제 환불 통합 테스트 케이스 |
| 기준 테스트 파일 | [PaymentServiceIntegrationTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/common/security/payment/application/PaymentServiceIntegrationTest.java) |
| 모듈 | 결제 / 환불 |
| 테스트 유형 | 통합 / 동시성 |
| 우선순위 | P0 |

## 2. 테스트 목적

환불 처리 시 결제, 이용권, 예약, 이벤트가 함께 정합성 있게 동작하는지 검증한다.

## 3. 테스트 환경

| 항목 | 값 |
|-----|-----|
| 실행 환경 | Local / Dev |
| 외부 PG | Mock |
| 필요 데이터 | 회원, 이용권, 결제, 예약, 스케줄 |

## 4. 선행 조건

- 테스트용 완료 결제와 연결 이용권이 준비되어 있어야 한다.
- 미래 예약과 과거 예약을 구분할 수 있는 데이터가 있어야 한다.

## 5. 테스트 케이스 상세

### PAY-REF-QA-001 완료되지 않은 결제 환불 차단

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 결제 상태 `READY` 또는 `FAILED` |
| 입력값 | 대상 paymentId |
| 수행절차 | 환불 호출 |
| 예상결과 | 비즈니스 예외 발생, 상태 변경 없음 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-REF-QA-002 이미 환불된 이용권 연결 결제 재환불 차단

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 연결된 이용권 상태 `REFUNDED` |
| 입력값 | 대상 paymentId |
| 수행절차 | 환불 호출 |
| 예상결과 | 비즈니스 예외 발생 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-REF-QA-003 서로 다른 멱등 키로 동시 환불 시 1건만 성공

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 동일 결제 환불 가능 |
| 입력값 | 동일 paymentId, 서로 다른 `idempotencyKey` |
| 수행절차 | 2개 요청 동시 실행 |
| 예상결과 | 1건 성공, 1건 실패 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-REF-QA-004 동일 멱등 키로 동시 환불 시 실처리 1회

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 동일 결제 환불 가능 |
| 입력값 | 동일 paymentId, 동일 `idempotencyKey` |
| 수행절차 | 2개 요청 동시 실행 |
| 예상결과 | 실제 환불 처리 1회, 중복 취소 없음 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-REF-QA-005 정상 환불 시 결제/이용권 상태 변경

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 완료 결제와 유효 이용권 존재 |
| 입력값 | 대상 paymentId |
| 수행절차 | 환불 호출 후 상태 조회 |
| 예상결과 | 결제 `CANCELED`, 이용권 `REFUNDED` |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-REF-QA-006 COUNT 이용권 환불 금액 계산 검증

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | COUNT 이용권 연결 |
| 입력값 | 대상 paymentId |
| 수행절차 | 환불 호출 후 금액 확인 |
| 예상결과 | 환불 금액이 정책 계산 결과와 일치 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-REF-QA-007 PERIOD 이용권 환불 금액 계산 검증

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | PERIOD 이용권 연결 |
| 입력값 | 대상 paymentId |
| 수행절차 | 환불 호출 후 금액 확인 |
| 예상결과 | 환불 금액이 정책 계산 결과와 일치 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-REF-QA-008 환불 시 미래 예약 자동 취소 검증

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 미래 `RESERVED` 예약과 과거 `ATTENDED` 예약 존재 |
| 입력값 | 대상 paymentId |
| 수행절차 | 환불 호출 후 예약 상태 조회 |
| 예상결과 | 미래 예약만 `CANCELED`, 과거 출석 예약 유지 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-REF-QA-009 환불 이벤트 및 알림 후속 처리 검증

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | 정상 환불 가능 데이터 준비 |
| 입력값 | 대상 paymentId |
| 수행절차 | 환불 호출 후 이벤트/알림 결과 확인 |
| 예상결과 | 환불 완료 이벤트 발행 및 후속 알림 생성 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

## 6. 수행 요약

| 항목 | 내용 |
|-----|-----|
| 수행자 |  |
| 수행일 |  |
| 수행 버전 |  |
| 환경 |  |
| 결과 요약 |  |
| 결함 요약 |  |

## 현재 테스트 메서드 기준

| 메서드 | DisplayName | QA 케이스 |
|-----|-----|-----|
| `refund_fails_when_payment_is_not_completed` | COMPLETED 상태가 아닌 결제는 환불할 수 없다 | `PAY-REF-QA-001` |
| `refund_fails_when_membership_already_refunded` | 이미 환불된 이용권이 연결된 결제는 다시 환불할 수 없다 | `PAY-REF-QA-002` |
| `refund_concurrent_with_different_keys_only_one_succeeds` | 동일 paymentId에 대해 서로 다른 idempotencyKey로 동시에 refund 요청하면 1건만 성공한다 | `PAY-REF-QA-003` |
| `refund_concurrent_with_same_key_is_processed_once` | 동일 paymentId에 대해 동일 idempotencyKey로 동시에 refund 요청하면 1건만 실제 처리된다 | `PAY-REF-QA-004` |
| `refund_success` | 완료된 결제를 환불하면 결제는 취소되고 이용권은 환불 상태가 되며 미래 예약은 취소된다 | `PAY-REF-QA-005` |
| `count_refund_amount_is_calculated_correctly` | COUNT 이용권 환불 금액 계산이 정확해야 한다 | `PAY-REF-QA-006` |
| `period_refund_amount_is_calculated_correctly` | PERIOD 이용권 환불 금액 계산이 정확해야 한다 | `PAY-REF-QA-007` |
| `future_reserved_bookings_are_canceled_but_attended_kept` | 환불 시 미래 RESERVED 예약은 자동 취소되고 과거 ATTENDED 예약은 유지된다 | `PAY-REF-QA-008` |
| `payment_canceled_event_is_published_and_notification_created` | 환불 시 PaymentCanceledEvent가 발행되어 환불 완료 알림이 생성된다 | `PAY-REF-QA-009` |
| `refund_fails_when_count_membership_has_no_remaining_count` | 잔여 횟수가 없는 COUNT 이용권은 환불할 수 없다 | `PAY-REF-QA-010` |
| `refund_fails_when_period_membership_is_expired` | 만료된 PERIOD 이용권은 환불할 수 없다 | `PAY-REF-QA-011` |
