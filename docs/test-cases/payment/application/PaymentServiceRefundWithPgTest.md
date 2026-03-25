# 결제 환불 PG 연동 테스트 설계서

## 1. 대상 정보

- 테스트 파일: [PaymentServiceRefundWithPgTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/application/PaymentServiceRefundWithPgTest.java)
- 대상 계층: 결제 환불 서비스
- 테스트 유형: 단위 테스트

## 2. 문서 목적

환불 시 외부 PG 취소 결과가 내부 결제 상태와 환불 응답에 어떻게 반영되는지 검증한다.

## 3. 상세 테스트 케이스

| ID | 시나리오 | 입력 조건 | 수행 절차 | 기대 결과 |
|-----|-----|-----|-----|-----|
| PAY-REF-PG-001 | PG 취소 성공 | 완료된 결제, 유효 이용권, 성공 응답 | refund 호출 | 환불 응답 반환, 결제 `CANCELED` |
| PAY-REF-PG-002 | PG 취소 실패 | 완료된 결제, 실패 응답 | refund 호출 | 비즈니스 예외 발생 |

## 4. 합격 기준

- PG 실패 시 내부 결제가 성공 상태로 바뀌면 안 된다.
- PG 성공 시 환불 응답 금액이 0보다 커야 한다.
