# QA - 결제 승인 서비스 단위 테스트 케이스

## 1. 문서 기본 정보

| 항목 | 내용 |
|-----|-----|
| 문서명 | 결제 승인 서비스 단위 테스트 케이스 |
| 기준 테스트 파일 | [PaymentPgServiceTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/payment/application/PaymentPgServiceTest.java) |
| 모듈 | 결제 승인 |
| 테스트 유형 | 단위 |
| 우선순위 | P1 |

## 2. 테스트 케이스 상세

### PAY-APP-UNIT-QA-001 PG 승인 성공

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | `READY` 상태 결제, 성공 PG 응답 |
| 입력값 | 승인 요청 |
| 수행절차 | approve 호출 |
| 예상결과 | 결제 `COMPLETED`, membership 생성 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-APP-UNIT-QA-002 PG 승인 실패

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | `READY` 상태 결제, 실패 PG 응답 |
| 입력값 | 승인 요청 |
| 수행절차 | approve 호출 |
| 예상결과 | 결제 `FAILED`, 실패 사유 저장 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-APP-UNIT-QA-003 READY 상태 아닌 결제 승인 차단

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | 결제 상태 `FAILED` |
| 입력값 | 승인 요청 |
| 수행절차 | approve 호출 |
| 예상결과 | 비즈니스 예외 발생 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-APP-UNIT-QA-004 이미 완료된 결제 재승인 차단

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | 결제 상태 `COMPLETED` |
| 입력값 | 승인 요청 |
| 수행절차 | approve 호출 |
| 예상결과 | 비즈니스 예외 발생 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-APP-UNIT-QA-005 이미 실패한 결제 재승인 차단

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | 결제 상태 `FAILED` |
| 입력값 | 승인 요청 |
| 수행절차 | approve 호출 |
| 예상결과 | 비즈니스 예외 발생 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

## 3. 수행 요약

| 항목 | 내용 |
|-----|-----|
| 수행자 |  |
| 수행일 |  |
| 수행 버전 |  |
| 결과 요약 |  |

## 현재 테스트 메서드 기준

| 메서드 | DisplayName | QA 케이스 |
|-----|-----|-----|
| `approve_success` | PG 승인 성공 시 결제가 완료되고 이용권이 생성된다 | `PAY-APP-UNIT-QA-001` |
| `approve_fail_when_pg_fails` | PG 승인 실패 시 결제는 FAILED 상태가 된다 | `PAY-APP-UNIT-QA-002` |
| `approve_fail_when_payment_not_ready` | READY 상태가 아닌 결제는 승인할 수 없다 | `PAY-APP-UNIT-QA-003` |
| `approve_fail_when_payment_already_completed` | 이미 COMPLETED 상태인 결제는 재승인할 수 없다 | `PAY-APP-UNIT-QA-004` |
| `approve_fail_when_payment_already_failed` | 이미 FAILED 상태인 결제는 재승인할 수 없다 | `PAY-APP-UNIT-QA-005` |
