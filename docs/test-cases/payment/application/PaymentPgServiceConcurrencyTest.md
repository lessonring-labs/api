# 결제 승인 동시성 테스트 설계서

## 1. 대상 정보

- 테스트 파일: [PaymentPgServiceConcurrencyTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/application/PaymentPgServiceConcurrencyTest.java)
- 대상 계층: 결제 승인 서비스
- 테스트 유형: 통합 동시성 테스트

## 2. 문서 목적

승인 요청이 동시에 여러 번 들어오는 상황에서 멱등성과 operation 기록 정책이 지켜지는지 검증한다.

## 3. 검증 범위

- 다른 `idempotencyKey` 동시 승인
- 같은 `idempotencyKey` 동시 승인
- PG 호출 횟수
- `PaymentOperation` 성공/실패 기록

## 4. 상세 테스트 케이스

| ID | 시나리오 | 입력 조건 | 수행 절차 | 기대 결과 |
|-----|-----|-----|-----|-----|
| PAY-APP-CON-001 | 서로 다른 `idempotencyKey` 동시 승인 | 동일 payment, 다른 키 | 2개 스레드 동시 approve | 1건 성공, 1건 실패, PG 호출 1회 |
| PAY-APP-CON-002 | 동일 `idempotencyKey` 동시 승인 | 동일 payment, 동일 키 | 2개 스레드 동시 approve | 최종 성공 1회, 응답 재사용 또는 재처리 방지, PG 호출 1회 |

## 5. 합격 기준

- membership은 1건만 생성되어야 한다.
- `PaymentOperation`은 정책에 맞는 성공/실패 상태를 남겨야 한다.
