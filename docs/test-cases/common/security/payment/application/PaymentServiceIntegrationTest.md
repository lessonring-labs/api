# 결제 환불 통합 테스트 설계서

## 1. 기본 정보

| 항목 | 내용 |
|-----|-----|
| 대상 파일 | [PaymentServiceIntegrationTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/common/security/payment/application/PaymentServiceIntegrationTest.java) |
| 대상 계층 | 결제 환불 애플리케이션 서비스 |
| 테스트 유형 | 통합 / 동시성 |
| 주 우선순위 | P0 |
| 관련 기능 | 환불, 이용권 환불 처리, 예약 취소, 금액 계산, 이벤트 발행 |

## 2. 테스트 목적

환불 도메인이 결제 상태, 이용권 상태, 예약 상태, PG 응답, 동시성 제어, 이벤트 발행까지 포함해 운영 정책대로 동작하는지 검증한다.

## 3. 업무 중요도

환불 오류는 금전 손실, CS 증가, 이용권 정합성 파손으로 직결된다. 따라서 본 테스트군은 회귀 테스트 최상위 우선순위에 해당한다.

## 4. 범위

### 포함 범위

- 환불 가능 여부 판정
- 이미 환불된 이용권과의 연계 검증
- 동시 환불 요청 제어
- 미래 예약 자동 취소
- 이용권 상태 변경
- COUNT / PERIOD 환불 금액 계산
- 환불 이벤트 발행

### 제외 범위

- 프론트엔드 환불 화면
- 실 PG 네트워크 호출

## 5. 사전 조건

- 회원, 결제, 이용권, 예약, 스케줄 테스트 데이터 생성 가능
- 외부 PG는 테스트 mock bean으로 대체
- 환불 도메인 이벤트 핸들러가 테스트 컨텍스트에서 동작 가능

## 6. 핵심 정책

- `COMPLETED` 상태 결제만 환불 가능
- 이미 환불된 이용권이 연결된 결제는 재환불 불가
- 미래 `RESERVED` 예약은 환불 시 취소
- COUNT / PERIOD는 환불 계산 규칙이 다름
- 동시 환불에서도 실처리는 1회여야 함

## 7. 상세 테스트 케이스

| ID | 우선순위 | 유형 | 시나리오 | 입력 조건 | 수행 절차 | 기대 결과 |
|-----|-----|-----|-----|-----|-----|-----|
| PAY-REF-INT-001 | P0 | 통합 | 완료되지 않은 결제 환불 차단 | `READY` 또는 `FAILED` 결제 | 환불 호출 | 비즈니스 예외 발생 |
| PAY-REF-INT-002 | P0 | 통합 | 이미 환불된 이용권 연결 결제 재환불 차단 | 이용권 `REFUNDED` | 환불 호출 | 비즈니스 예외 발생 |
| PAY-REF-INT-003 | P0 | 동시성 | 서로 다른 멱등 키 동시 환불 | 동일 paymentId, 서로 다른 key | 2개 스레드 동시 환불 | 1건 성공, 1건 실패 |
| PAY-REF-INT-004 | P0 | 동시성 | 동일 멱등 키 동시 환불 | 동일 paymentId, 동일 key | 2개 스레드 동시 환불 | 실제 처리 1회 |
| PAY-REF-INT-005 | P0 | 통합 | 정상 환불 성공 | 완료된 결제, 유효 이용권 | 환불 호출 | 결제 `CANCELED`, 이용권 `REFUNDED` |
| PAY-REF-INT-006 | P1 | 통합 | COUNT 환불 금액 계산 | 횟수형 이용권 | 환불 호출 | 잔여 횟수 기준 금액 일치 |
| PAY-REF-INT-007 | P1 | 통합 | PERIOD 환불 금액 계산 | 기간형 이용권 | 환불 호출 | 잔여 기간 기준 금액 일치 |
| PAY-REF-INT-008 | P0 | 통합 | 미래 예약 자동 취소 | 미래 예약 존재 | 환불 호출 | 미래 `RESERVED` 취소, 과거 `ATTENDED` 유지 |
| PAY-REF-INT-009 | P1 | 통합 | 환불 이벤트 발행 | 정상 환불 성공 | 후속 이벤트 확인 | 취소 이벤트 및 알림 생성 |
| PAY-REF-INT-010 | P1 | 통합 | 잔여 횟수 없는 COUNT 환불 차단 | 사용 완료 상태 | 환불 호출 | 비즈니스 예외 발생 |
| PAY-REF-INT-011 | P1 | 통합 | 만료된 PERIOD 환불 차단 | 만료 상태 | 환불 호출 | 비즈니스 예외 발생 |

## 8. 판정 기준

- 환불 성공 시 결제, 이용권, 예약이 함께 정합성 있게 변경되어야 한다.
- 환불 실패 시 부분 반영이 없어야 한다.
- 동시 요청에서 중복 환불 또는 이중 취소가 발생하면 실패다.

## 9. 추적 포인트

- 결제 상태: `COMPLETED -> CANCELED`
- 이용권 상태: `ACTIVE/USED_UP -> REFUNDED`
- 예약 상태: 미래 `RESERVED -> CANCELED`
- 이벤트 발행 여부

## 10. 잔여 리스크

- 다중 노드 환경의 실 Redis 락 타이밍 문제는 테스트 환경과 다를 수 있다.
- 실제 PG 응답 형식 변화는 별도 계약 테스트로 보완이 필요하다.

## 현재 테스트 메서드 기준

| 메서드 | DisplayName | 문서 케이스 |
|-----|-----|-----|
| `refund_fails_when_payment_is_not_completed` | COMPLETED 상태가 아닌 결제는 환불할 수 없다 | `PAY-REF-INT-001` |
| `refund_fails_when_membership_already_refunded` | 이미 환불된 이용권이 연결된 결제는 다시 환불할 수 없다 | `PAY-REF-INT-002` |
| `refund_concurrent_with_different_keys_only_one_succeeds` | 동일 paymentId에 대해 서로 다른 idempotencyKey로 동시에 refund 요청하면 1건만 성공한다 | `PAY-REF-INT-003` |
| `refund_concurrent_with_same_key_is_processed_once` | 동일 paymentId에 대해 동일 idempotencyKey로 동시에 refund 요청하면 1건만 실제 처리된다 | `PAY-REF-INT-004` |
| `refund_success` | 완료된 결제를 환불하면 결제는 취소되고 이용권은 환불 상태가 되며 미래 예약은 취소된다 | `PAY-REF-INT-005` |
| `count_refund_amount_is_calculated_correctly` | COUNT 이용권 환불 금액 계산이 정확해야 한다 | `PAY-REF-INT-006` |
| `period_refund_amount_is_calculated_correctly` | PERIOD 이용권 환불 금액 계산이 정확해야 한다 | `PAY-REF-INT-007` |
| `future_reserved_bookings_are_canceled_but_attended_kept` | 환불 시 미래 RESERVED 예약은 자동 취소되고 과거 ATTENDED 예약은 유지된다 | `PAY-REF-INT-008` |
| `payment_canceled_event_is_published_and_notification_created` | 환불 시 PaymentCanceledEvent가 발행되어 환불 완료 알림이 생성된다 | `PAY-REF-INT-009` |
| `refund_fails_when_count_membership_has_no_remaining_count` | 잔여 횟수가 없는 COUNT 이용권은 환불할 수 없다 | `PAY-REF-INT-010` |
| `refund_fails_when_period_membership_is_expired` | 만료된 PERIOD 이용권은 환불할 수 없다 | `PAY-REF-INT-011` |
