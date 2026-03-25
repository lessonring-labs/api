# QA 제출용 테스트 케이스 문서

## 1. 문서 목적

본 디렉터리는 QA 수행, 결과 기록, 릴리스 검수, 결재용 제출을 목적으로 작성한 테스트 케이스 문서 모음이다.

이 문서는 다음 상황에서 사용한다.

- 기능 개발 완료 후 QA 검수
- 배포 전 회귀 테스트 수행
- 장애 수정 후 재검증
- 릴리스 승인 자료 첨부

## 2. 문서 운영 원칙

- 문서는 테스트 클래스 1개당 1개를 유지한다.
- 문서 제목은 QA 제출 기준으로 한글화한다.
- 자동화 테스트 기준이더라도 QA가 수동으로 확인 가능한 표현으로 재작성한다.
- 모든 케이스는 결과 기록이 가능해야 한다.

## 3. 공통 기록 규칙

### 3.1 판정 값

- `합격`
- `불합격`
- `보류`
- `미수행`

### 3.2 우선순위 기준

| 우선순위 | 의미 |
|-----|-----|
| P0 | 금전, 인증, 보안, 데이터 정합성, 중복 처리 |
| P1 | 핵심 기능의 성공/실패 흐름 |
| P2 | 보조 예외, 포맷, 운영 편의성 |

### 3.3 증빙 예시

- API 응답 캡처
- 로그 링크
- DB 조회 결과
- 테스트 리포트 링크
- 영상 또는 스크린샷

## 4. 권장 수행 절차

1. 대상 문서의 테스트 환경을 확인한다.
2. 사전조건을 충족시키고 테스트 데이터를 준비한다.
3. 케이스를 수행하고 실제결과와 판정을 기록한다.
4. 불합격 시 결함 ID와 재현 조건을 함께 남긴다.
5. 문서 하단 수행 요약을 업데이트한다.

## 5. 문서 목록

### 인증 및 보안

- [QA - 인증 컨트롤러 테스트 케이스](/C:/wms/api/docs/qa-test-cases/auth/api/AuthControllerTest.md)
- [QA - JWT 토큰 제공자 테스트 케이스](/C:/wms/api/docs/qa-test-cases/common/security/JwtTokenProviderImplTest.md)
- [QA - 웹훅 서명 검증 테스트 케이스](/C:/wms/api/docs/qa-test-cases/payment/infrastructure/webhook/PaymentWebhookSignatureVerifierTest.md)

### 결제

- [QA - 결제 컨트롤러 테스트 케이스](/C:/wms/api/docs/qa-test-cases/payment/api/PaymentControllerTest.md)
- [QA - 결제 승인 서비스 단위 테스트 케이스](/C:/wms/api/docs/qa-test-cases/payment/application/PaymentPgServiceTest.md)
- [QA - 결제 승인 통합 테스트 케이스](/C:/wms/api/docs/qa-test-cases/payment/application/PaymentPgServiceIntegrationTest.md)
- [QA - 결제 승인 동시성 테스트 케이스](/C:/wms/api/docs/qa-test-cases/payment/application/PaymentPgServiceConcurrencyTest.md)
- [QA - 결제 생성 멱등성 테스트 케이스](/C:/wms/api/docs/qa-test-cases/payment/application/PaymentServiceIdempotencyTest.md)
- [QA - 결제 환불 PG 연동 테스트 케이스](/C:/wms/api/docs/qa-test-cases/payment/application/PaymentServiceRefundWithPgTest.md)
- [QA - 결제 환불 통합 테스트 케이스](/C:/wms/api/docs/qa-test-cases/common/security/payment/application/PaymentServiceIntegrationTest.md)
- [QA - 결제 웹훅 서비스 테스트 케이스](/C:/wms/api/docs/qa-test-cases/payment/application/PaymentWebhookServiceTest.md)
- [QA - 결제 웹훅 재전송 테스트 케이스](/C:/wms/api/docs/qa-test-cases/payment/application/PaymentWebhookReplayTest.md)
- [QA - 결제 교차 충돌 동시성 테스트 케이스](/C:/wms/api/docs/qa-test-cases/payment/application/PaymentCrossConflictConcurrencyTest.md)
- [QA - 결제 2순위 충돌 동시성 테스트 케이스](/C:/wms/api/docs/qa-test-cases/payment/application/PaymentCrossConflictSecondPriorityConcurrencyTest.md)

### 참고

- [QA - 테스트 외부 연동 목 설정 참고 문서](/C:/wms/api/docs/qa-test-cases/support/TestExternalMockConfig.md)
