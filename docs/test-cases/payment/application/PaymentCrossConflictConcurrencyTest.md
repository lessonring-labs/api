# 결제 교차 충돌 동시성 테스트 설계서

## 1. 대상 정보

- 테스트 파일: [PaymentCrossConflictConcurrencyTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/application/PaymentCrossConflictConcurrencyTest.java)
- 대상 계층: 결제 승인/환불/웹훅 교차 충돌 처리
- 테스트 유형: 통합 동시성 테스트

## 2. 문서 목적

서로 다른 경로에서 동일 결제 상태를 변경하려고 할 때 중복 반영, 중복 membership 생성, 중복 webhook 처리 같은 문제가 발생하지 않는지 확인한다.

## 3. 검증 범위

- 승인 API와 completed webhook의 동시 충돌
- 환불 API와 canceled webhook의 동시 충돌
- webhook log 적재 정책
- PG 호출 중복 방지

## 4. 사전 조건

- 동일 결제에 대해 여러 스레드가 동시에 진입할 수 있는 테스트 환경이어야 한다.
- 외부 PG 응답은 mock으로 제어 가능해야 한다.

## 5. 상세 테스트 케이스

| ID | 시나리오 | 입력 조건 | 수행 절차 | 기대 결과 |
|-----|-----|-----|-----|-----|
| PAY-CROSS-001 | 승인 API와 completed webhook 동시 진입 | 동일 paymentId, 동일 주문 | 승인 스레드와 webhook 스레드 동시 실행 | 최종 상태 `COMPLETED`, membership 1건만 생성, PG 승인 1회 |
| PAY-CROSS-002 | 환불 API와 canceled webhook 동시 진입 | 완료된 동일 결제 | 환불 스레드와 webhook 스레드 동시 실행 | 최종 상태 `CANCELED`, 이용권 `REFUNDED`, webhook log 정상 기록 |

## 6. 합격 기준

- 최종 상태가 중복 처리 없이 일관되어야 한다.
- 동일 결제에 대해 승인 또는 환불이 이중 반영되면 실패다.

## 7. 운영 관점 중요도

매우 높음. 결제 API와 외부 PG 웹훅이 실제 운영에서 동시에 도착할 수 있기 때문이다.
