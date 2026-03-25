# 웹훅 서명 검증 테스트 설계서

## 1. 대상 정보

- 테스트 파일: [PaymentWebhookSignatureVerifierTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/infrastructure/webhook/PaymentWebhookSignatureVerifierTest.java)
- 대상 계층: 웹훅 보안 검증 인프라
- 테스트 유형: 단위 테스트

## 2. 문서 목적

외부 결제사 webhook 요청이 위조되지 않았는지 확인하는 HMAC 서명 검증 로직이 정상 동작하는지 검증한다.

## 3. 검증 범위

- 정상 서명 허용
- signature 헤더 유효성
- timestamp 헤더 유효성
- body 무결성
- 허용 시간 검증
- secret 불일치 검증

## 4. 상세 테스트 케이스

| ID | 시나리오 | 입력 조건 | 수행 절차 | 기대 결과 |
|-----|-----|-----|-----|-----|
| WEBHOOK-SIGN-001 | 정상 서명 검증 성공 | 유효한 signature, timestamp, body | verify 호출 | 예외 없이 통과 |
| WEBHOOK-SIGN-002 | signature 누락 | signature 없음 | verify 호출 | 비즈니스 예외 발생 |
| WEBHOOK-SIGN-003 | timestamp 누락 | timestamp 없음 | verify 호출 | 비즈니스 예외 발생 |
| WEBHOOK-SIGN-004 | raw body 누락 | 빈 body | verify 호출 | 비즈니스 예외 발생 |
| WEBHOOK-SIGN-005 | timestamp 형식 오류 | 숫자가 아닌 값 | verify 호출 | 비즈니스 예외 발생 |
| WEBHOOK-SIGN-006 | 허용 시간 초과 | 만료된 timestamp | verify 호출 | 비즈니스 예외 발생 |
| WEBHOOK-SIGN-007 | body 변조 | 서명 생성 body와 다른 body | verify 호출 | 비즈니스 예외 발생 |
| WEBHOOK-SIGN-008 | secret 불일치 | 다른 secret로 생성한 서명 | verify 호출 | 비즈니스 예외 발생 |
| WEBHOOK-SIGN-009 | signature 불일치 | 임의 서명 값 | verify 호출 | 비즈니스 예외 발생 |

## 5. 합격 기준

- 정상 요청만 통과해야 한다.
- 하나라도 누락/변조되면 예외가 발생해야 한다.

## 6. 운영 관점 중요도

매우 높음. webhook 위변조 차단과 재생 공격 완화의 핵심 검증이다.
