# 결제 승인 통합 테스트 설계서

## 1. 대상 정보

- 테스트 파일: [PaymentPgServiceIntegrationTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/application/PaymentPgServiceIntegrationTest.java)
- 대상 계층: 결제 승인 애플리케이션 서비스
- 테스트 유형: 통합 테스트

## 2. 문서 목적

결제 승인 서비스가 정상 승인, 멱등성, 상태 제약, 실패 처리, 락 처리까지 포함해 운영 기준을 만족하는지 검증한다.

## 3. 사전 조건

- 외부 PG는 테스트 mock bean 사용
- 저장소와 트랜잭션이 활성화된 테스트 컨텍스트 구성
- 승인 대상 결제는 테스트 데이터로 준비

## 4. 테스트 데이터 기준

- 결제 금액: `100,000`
- 주문 ID: `ORDER_123` 등
- PG Provider: `TOSS`
- 멱등 키: `approve-key-*`

## 5. 상세 테스트 케이스

| ID | 시나리오 | 입력 조건 | 수행 절차 | 기대 결과 |
|-----|-----|-----|-----|-----|
| PAY-APP-INT-001 | 정상 승인 성공 | `READY` 상태 결제, 정상 PG 응답 | 승인 호출 | 결제 `COMPLETED`, membership 생성, 응답 정상 |
| PAY-APP-INT-002 | 동일 `idempotencyKey` + 동일 요청 | 기존 처리 이력 존재 | 재호출 | 기존 응답 재사용 |
| PAY-APP-INT-003 | 동일 `idempotencyKey` + 다른 요청 | 기존 처리 이력 존재, payload 상이 | 재호출 | 비즈니스 예외 발생 |
| PAY-APP-INT-004 | `READY`가 아닌 결제 승인 | `FAILED` 또는 `COMPLETED` 상태 | 승인 호출 | 승인 차단 |
| PAY-APP-INT-005 | PG 실패 응답 | `success=false` | 승인 호출 | operation `FAILED`, 결제 `FAILED` |
| PAY-APP-INT-006 | 락 획득 실패 | lock manager가 false 반환 | 승인 호출 | 승인 차단 |

## 6. 합격 기준

- 승인 성공 시 membership이 정확히 1건 생성되어야 한다.
- 실패 시 operation과 결제 상태가 일관되어야 한다.
- 멱등성 정책이 응답 재사용과 예외 처리 모두에서 유지되어야 한다.
