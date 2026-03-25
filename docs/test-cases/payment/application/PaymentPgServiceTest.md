# 결제 승인 서비스 단위 테스트 설계서

## 1. 대상 정보

- 테스트 파일: [PaymentPgServiceTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/application/PaymentPgServiceTest.java)
- 대상 계층: 결제 승인 서비스
- 테스트 유형: 단위 테스트

## 2. 문서 목적

승인 서비스의 기본 분기와 상태 전이가 설계대로 동작하는지 빠르게 검증한다.

## 3. 상세 테스트 케이스

| ID | 시나리오 | 입력 조건 | 수행 절차 | 기대 결과 |
|-----|-----|-----|-----|-----|
| PAY-APP-UNIT-001 | PG 승인 성공 | 정상 payment, 정상 PG 응답 | approve 호출 | 결제 `COMPLETED`, membership 생성 |
| PAY-APP-UNIT-002 | PG 승인 실패 | 정상 payment, 실패 PG 응답 | approve 호출 | 결제 `FAILED`, 실패 사유 저장 |
| PAY-APP-UNIT-003 | READY 아닌 결제 승인 시도 | `FAILED` 상태 결제 | approve 호출 | 예외 발생 |
| PAY-APP-UNIT-004 | 이미 완료된 결제 재승인 | `COMPLETED` 상태 결제 | approve 호출 | 예외 발생 |
| PAY-APP-UNIT-005 | 이미 실패한 결제 재승인 | `FAILED` 상태 결제 | approve 호출 | 예외 발생 |

## 4. 합격 기준

- 각 상태 분기에서 의도한 예외 또는 상태 전이가 정확해야 한다.
