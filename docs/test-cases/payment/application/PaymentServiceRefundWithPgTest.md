# 결제 환불 PG 연동 테스트 설계서

## 1. 기본 정보

| 항목 | 내용 |
|-----|-----|
| 대상 파일 | [PaymentServiceRefundWithPgTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/payment/application/PaymentServiceRefundWithPgTest.java) |
| 대상 계층 | 결제 환불 서비스 |
| 테스트 유형 | 단위 |
| 주 우선순위 | P0 |
| 관련 기능 | PG 취소 성공/실패 연동 |

## 2. 테스트 목적

외부 PG 취소 결과가 내부 환불 처리 성공/실패를 정확히 결정하는지 검증한다.

## 3. 상세 테스트 케이스

| ID | 우선순위 | 유형 | 시나리오 | 기대 결과 |
|-----|-----|-----|-----|-----|
| PAY-REF-PG-001 | P0 | 단위 | PG 취소 성공 | 환불 응답 반환, 결제 `CANCELED` |
| PAY-REF-PG-002 | P0 | 단위 | PG 취소 실패 | 비즈니스 예외 발생 |

## 4. 판정 기준

- PG 실패 응답에서 내부 환불이 성공 처리되면 실패다.

## 현재 테스트 메서드 기준

| 메서드 | DisplayName | 문서 케이스 |
|-----|-----|-----|
| `refund_with_pg_cancel_success` | 환불 시 PG 취소가 성공하면 내부 환불이 완료된다 | `PAY-REF-PG-001` |
| `refund_with_pg_cancel_fail` | 환불 시 PG 취소가 실패하면 예외가 발생한다 | `PAY-REF-PG-002` |
