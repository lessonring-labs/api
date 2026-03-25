# 웹훅 서명 검증 테스트 설계서

## 1. 기본 정보

| 항목 | 내용 |
|-----|-----|
| 대상 파일 | [PaymentWebhookSignatureVerifierTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/infrastructure/webhook/PaymentWebhookSignatureVerifierTest.java) |
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
