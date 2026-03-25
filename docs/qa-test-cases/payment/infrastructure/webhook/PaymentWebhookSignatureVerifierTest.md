# QA - 웹훅 서명 검증 테스트 케이스

## 1. 문서 기본 정보

| 항목 | 내용 |
|-----|-----|
| 문서명 | 웹훅 서명 검증 테스트 케이스 |
| 기준 테스트 파일 | [PaymentWebhookSignatureVerifierTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/infrastructure/webhook/PaymentWebhookSignatureVerifierTest.java) |
| 모듈 | 웹훅 보안 |
| 테스트 유형 | 보안 |
| 우선순위 | P0 |

## 2. 테스트 케이스 상세

### WEBHOOK-SIGN-QA-001 정상 서명 검증 성공

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 유효한 secret과 timestamp 생성 가능 |
| 입력값 | 정상 signature, timestamp, raw body |
| 수행절차 | verify 호출 |
| 예상결과 | 예외 없이 통과 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### WEBHOOK-SIGN-QA-002 signature 헤더 누락

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | verify 호출 가능 |
| 입력값 | signature 없음 |
| 수행절차 | verify 호출 |
| 예상결과 | 비즈니스 예외 발생 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### WEBHOOK-SIGN-QA-003 timestamp 헤더 누락

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | verify 호출 가능 |
| 입력값 | timestamp 없음 |
| 수행절차 | verify 호출 |
| 예상결과 | 비즈니스 예외 발생 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### WEBHOOK-SIGN-QA-004 raw body 누락

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | verify 호출 가능 |
| 입력값 | 빈 body |
| 수행절차 | verify 호출 |
| 예상결과 | 비즈니스 예외 발생 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### WEBHOOK-SIGN-QA-005 timestamp 형식 오류

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | verify 호출 가능 |
| 입력값 | 숫자가 아닌 timestamp |
| 수행절차 | verify 호출 |
| 예상결과 | 비즈니스 예외 발생 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### WEBHOOK-SIGN-QA-006 허용 시간 초과

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 과거 timestamp 생성 가능 |
| 입력값 | 허용 범위를 초과한 timestamp |
| 수행절차 | verify 호출 |
| 예상결과 | 비즈니스 예외 발생 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### WEBHOOK-SIGN-QA-007 body 변조

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 원본 서명 생성 완료 |
| 입력값 | 서명 생성 후 변경된 body |
| 수행절차 | verify 호출 |
| 예상결과 | 비즈니스 예외 발생 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### WEBHOOK-SIGN-QA-008 secret 불일치

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 다른 secret로 서명 생성 가능 |
| 입력값 | 잘못된 secret 기반 signature |
| 수행절차 | verify 호출 |
| 예상결과 | 비즈니스 예외 발생 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### WEBHOOK-SIGN-QA-009 signature 불일치

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | verify 호출 가능 |
| 입력값 | 임의 signature |
| 수행절차 | verify 호출 |
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
