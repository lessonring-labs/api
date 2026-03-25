# 결제 승인 통합 테스트 설계서

## 1. 기본 정보

| 항목 | 내용 |
|-----|-----|
| 대상 파일 | [PaymentPgServiceIntegrationTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/application/PaymentPgServiceIntegrationTest.java) |
| 대상 계층 | 결제 승인 애플리케이션 서비스 |
| 테스트 유형 | 통합 |
| 주 우선순위 | P0 |
| 관련 기능 | 승인, 멱등성, operation 기록, lock 처리 |

## 2. 테스트 목적

승인 서비스가 정상 승인뿐 아니라 멱등성, 실패 처리, 락 획득 실패까지 운영 정책대로 동작하는지 검증한다.

## 3. 사전 조건

- PG 클라이언트는 mock bean으로 주입
- 결제, membership, operation 저장소가 정상 동작
- lock manager가 테스트 환경에서 제어 가능

## 4. 상세 테스트 케이스

| ID | 우선순위 | 유형 | 시나리오 | 입력 조건 | 기대 결과 |
|-----|-----|-----|-----|-----|-----|
| PAY-APP-INT-001 | P0 | 통합 | 정상 승인 성공 | `READY` 결제, 정상 PG 응답 | 결제 `COMPLETED`, membership 생성 |
| PAY-APP-INT-002 | P0 | 통합 | 동일 멱등 키 동일 요청 재호출 | 기존 operation 성공 이력 | 기존 응답 재사용 |
| PAY-APP-INT-003 | P0 | 통합 | 동일 멱등 키 다른 요청 재호출 | payload 차이 존재 | 비즈니스 예외 발생 |
| PAY-APP-INT-004 | P1 | 통합 | `READY` 아닌 결제 승인 차단 | `FAILED` 또는 `COMPLETED` 결제 | 승인 실패 |
| PAY-APP-INT-005 | P0 | 통합 | PG 실패 응답 처리 | `success=false` | operation `FAILED`, 결제 `FAILED` |
| PAY-APP-INT-006 | P0 | 통합 | 락 획득 실패 | lock false | 승인 실패 |

## 5. 판정 기준

- 승인 성공 시 membership이 반드시 1건 생성되어야 한다.
- 실패 시 operation 상태와 payment 상태가 일치해야 한다.
- 멱등성 위반 시 예외 없이 중복 처리되면 실패다.
