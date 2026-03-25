# 결제 승인 서비스 단위 테스트 설계서

## 1. 기본 정보

| 항목 | 내용 |
|-----|-----|
| 대상 파일 | [PaymentPgServiceTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/application/PaymentPgServiceTest.java) |
| 대상 계층 | 결제 승인 서비스 |
| 테스트 유형 | 단위 |
| 주 우선순위 | P1 |
| 관련 기능 | 승인 성공/실패, 상태 제약 |

## 2. 테스트 목적

승인 서비스의 핵심 분기와 상태 전이 규칙을 저장소/외부 연동 mock 기반으로 빠르게 검증한다.

## 3. 상세 테스트 케이스

| ID | 우선순위 | 유형 | 시나리오 | 기대 결과 |
|-----|-----|-----|-----|-----|
| PAY-APP-UNIT-001 | P1 | 단위 | 정상 승인 성공 | 결제 `COMPLETED`, membership 생성 |
| PAY-APP-UNIT-002 | P1 | 단위 | PG 승인 실패 | 결제 `FAILED`, 실패 사유 반영 |
| PAY-APP-UNIT-003 | P1 | 단위 | `READY` 아닌 결제 승인 시도 | 비즈니스 예외 발생 |
| PAY-APP-UNIT-004 | P1 | 단위 | 이미 완료된 결제 재승인 | 비즈니스 예외 발생 |
| PAY-APP-UNIT-005 | P1 | 단위 | 이미 실패한 결제 재승인 | 비즈니스 예외 발생 |

## 4. 판정 기준

- 각 상태 분기에서 설계된 결과와 다르면 실패다.
