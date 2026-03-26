# 웹훅 서명 검증 테스트 설계서

## 1. 기본 정보

| 항목 | 내용 |
|-----|-----|
| 대상 파일 | [PaymentWebhookSignatureVerifierTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/payment/infrastructure/webhook/PaymentWebhookSignatureVerifierTest.java) |
| 대상 계층 | webhook 보안 검증 인프라 |
| 테스트 유형 | 보안 / 단위 |
| 주 우선순위 | P0 |
| 관련 기능 | signature 검증, timestamp 검증, body 무결성 검증 |

## 2. 테스트 목적

외부 결제 webhook 요청이 위조 또는 변조되지 않았는지 검증하는 로직의 신뢰성을 확인한다.

## 3. 범위

- 정상 서명 허용
- signature 헤더 검증
- timestamp 유효성 검증
- raw body 무결성 검증
- 허용 시간 범위 검증
- secret 불일치 검증

## 4. 상세 테스트 케이스

| ID | 우선순위 | 유형 | 시나리오 | 기대 결과 |
|-----|-----|-----|-----|-----|
| WEBHOOK-SIGN-001 | P0 | 보안 | 정상 서명 | 예외 없이 통과 |
| WEBHOOK-SIGN-002 | P0 | 보안 | signature 누락 | 비즈니스 예외 |
| WEBHOOK-SIGN-003 | P0 | 보안 | timestamp 누락 | 비즈니스 예외 |
| WEBHOOK-SIGN-004 | P0 | 보안 | raw body 누락 | 비즈니스 예외 |
| WEBHOOK-SIGN-005 | P1 | 보안 | timestamp 형식 오류 | 비즈니스 예외 |
| WEBHOOK-SIGN-006 | P0 | 보안 | 허용 시간 초과 | 비즈니스 예외 |
| WEBHOOK-SIGN-007 | P0 | 보안 | body 변조 | 비즈니스 예외 |
| WEBHOOK-SIGN-008 | P0 | 보안 | secret 불일치 | 비즈니스 예외 |
| WEBHOOK-SIGN-009 | P0 | 보안 | signature 불일치 | 비즈니스 예외 |

## 5. 판정 기준

- 정상 요청만 통과해야 한다.
- 보안 검증 누락 또는 변조 허용 시 즉시 실패다.

## 현재 테스트 메서드 기준

| 메서드 | DisplayName | 문서 케이스 |
|-----|-----|-----|
| `verify_success` | 정상 signature면 검증에 성공한다 | `WEBHOOK-SIGN-001` |
| `verify_fail_when_signature_missing` | signature 헤더가 없으면 예외가 발생한다 | `WEBHOOK-SIGN-002` |
| `verify_fail_when_timestamp_missing` | timestamp 헤더가 없으면 예외가 발생한다 | `WEBHOOK-SIGN-003` |
| `verify_fail_when_raw_body_blank` | raw body가 비어 있으면 예외가 발생한다 | `WEBHOOK-SIGN-004` |
| `verify_fail_when_timestamp_invalid` | timestamp 형식이 잘못되면 예외가 발생한다 | `WEBHOOK-SIGN-005` |
| `verify_fail_when_timestamp_expired` | 허용 시간 범위를 초과하면 예외가 발생한다 | `WEBHOOK-SIGN-006` |
| `verify_fail_when_body_tampered` | body가 변조되면 예외가 발생한다 | `WEBHOOK-SIGN-007` |
| `verify_fail_when_secret_mismatch` | secret이 다르면 예외가 발생한다 | `WEBHOOK-SIGN-008` |
| `verify_fail_when_signature_mismatch` | signature가 다르면 예외가 발생한다 | `WEBHOOK-SIGN-009` |
