# QA 제출용 테스트 케이스 문서

## 1. 문서 목적

본 디렉터리는 현재 자동화 테스트 자산을 QA 수행 기준으로 재정리한 문서 세트다.

- 릴리스 전 회귀 테스트 범위 확인
- QA 수행 항목과 우선순위 공유
- 테스트 결과 기록 기준 통일
- 결제, 인증, webhook 관련 고위험 시나리오 추적

## 2. 현재 기준선

| 항목 | 내용 |
|-----|-----|
| 기준일 | 2026-03-25 |
| 기준 코드 | `main` 작업본 |
| 기준 명령 | `./gradlew test` |
| 최근 확인 결과 | `BUILD SUCCESSFUL` |
| 테스트 현황 | 65 passed, 7 skipped |
| 주의사항 | 일부 테스트는 `@MockBean` deprecation warning이 있으나 실패 원인은 아님 |

## 3. QA 운영 원칙

- 문서는 실제 존재하는 테스트 클래스 기준으로 유지한다.
- P0는 금전, 인증, 보안, 멱등성, 동시성, 데이터 정합성에 해당한다.
- 자동화 테스트가 있더라도 QA 문서는 수동 검증 관점으로 읽히도록 작성한다.
- 실패 시 재현 조건, 실제 응답, DB 상태를 함께 기록한다.

## 4. 권장 수행 절차

1. 대상 기능의 사전 데이터와 실행 환경을 맞춘다.
2. 관련 자동화 테스트 문서와 요구사항 문서를 함께 확인한다.
3. 수동 재현이 필요한 케이스는 API 응답, 로그, DB 상태를 함께 기록한다.
4. 자동화 테스트와 수동 검증 결과가 다르면 구현 변경 여부를 먼저 확인한다.

## 5. 테스트 자산 목록

### 5.1 인증 및 보안

| 구분 | 우선순위 | 목적 | 기준 소스 |
|-----|-----|-----|-----|
| 인증 컨트롤러 | P0 | 로그인, 토큰 재발급, 로그아웃 요청 검증 | [`AuthControllerTest.java`](../../src/test/java/com/lessonring/api/auth/api/AuthControllerTest.java) |
| JWT 토큰 제공자 | P0 | 토큰 생성 및 검증 로직 확인 | [`JwtTokenProviderImplTest.java`](../../src/test/java/com/lessonring/api/common/security/JwtTokenProviderImplTest.java) |
| webhook 서명 검증 | P0 | 외부 PG webhook 보안 검증 | [`PaymentWebhookSignatureVerifierTest.java`](../../src/test/java/com/lessonring/api/payment/infrastructure/webhook/PaymentWebhookSignatureVerifierTest.java) |

### 5.2 결제 API 및 서비스

| 구분 | 우선순위 | 목적 | 기준 소스 |
|-----|-----|-----|-----|
| 결제 컨트롤러 | P1 | 승인 API 요청/응답/validation 검증 | [`PaymentControllerTest.java`](../../src/test/java/com/lessonring/api/payment/api/PaymentControllerTest.java) |
| 결제 승인 단위 | P0 | 승인 성공, 실패, 상태 전이 확인 | [`PaymentPgServiceTest.java`](../../src/test/java/com/lessonring/api/payment/application/PaymentPgServiceTest.java) |
| 결제 승인 통합 | P0 | DB, 멱등성, operation 기록, 락 처리 검증 | [`PaymentPgServiceIntegrationTest.java`](../../src/test/java/com/lessonring/api/payment/application/PaymentPgServiceIntegrationTest.java) |
| 결제 승인 동시성 | P0 | 동일 결제 경쟁 승인 시 1회 처리 보장 | [`PaymentPgServiceConcurrencyTest.java`](../../src/test/java/com/lessonring/api/payment/application/PaymentPgServiceConcurrencyTest.java) |
| 결제 생성 멱등성 | P1 | 동일 `idempotencyKey` 재요청 시 기존 결제 반환 | [`PaymentServiceIdempotencyTest.java`](../../src/test/java/com/lessonring/api/payment/application/PaymentServiceIdempotencyTest.java) |
| 환불 PG 연동 | P0 | PG 취소 성공/실패에 따른 내부 환불 흐름 확인 | [`PaymentServiceRefundWithPgTest.java`](../../src/test/java/com/lessonring/api/payment/application/PaymentServiceRefundWithPgTest.java) |
| 환불 통합 | P0 | 이용권 환불, 미래 예약 취소, 이벤트 발행 검증 | [`PaymentServiceIntegrationTest.java`](../../src/test/java/com/lessonring/api/common/security/payment/application/PaymentServiceIntegrationTest.java) |
| webhook 서비스 | P0 | completed, failed, canceled 이벤트 처리 | [`PaymentWebhookServiceTest.java`](../../src/test/java/com/lessonring/api/payment/application/PaymentWebhookServiceTest.java) |
| webhook 재전송 | P0 | transmission 재전송 및 동일 상태 재수신 중복 방지 | [`PaymentWebhookReplayTest.java`](../../src/test/java/com/lessonring/api/payment/application/PaymentWebhookReplayTest.java) |
| 교차 충돌 동시성 | P0 | approve/refund/webhook 간 경쟁 처리 검증 | [`PaymentCrossConflictConcurrencyTest.java`](../../src/test/java/com/lessonring/api/payment/application/PaymentCrossConflictConcurrencyTest.java) |
| 2순위 충돌 동시성 | P0 | completed/canceled/approve/refund 우선순위 규칙 검증 | [`PaymentCrossConflictSecondPriorityConcurrencyTest.java`](../../src/test/java/com/lessonring/api/payment/application/PaymentCrossConflictSecondPriorityConcurrencyTest.java) |

## 6. QA 핵심 확인 포인트

### 6.1 인증

- 필수 입력 누락 시 `400`
- 잘못된 타입/path variable 처리
- 토큰 재발급과 로그아웃의 상태 일관성

### 6.2 결제 승인

- `READY` 상태에서만 승인 가능
- 동일 `idempotencyKey` 재시도 시 기존 응답 재사용
- 서로 다른 `idempotencyKey` 동시 승인 시 1건만 성공
- 승인 완료 시 이용권이 1건만 생성

### 6.3 결제 환불

- `COMPLETED` 상태만 환불 가능
- 이용권 상태와 미래 예약이 함께 반영
- PG 실패 시 내부 환불 미완료 유지

### 6.4 webhook

- signature 검증 필요
- 동일 transmission 재전송 무시
- 이미 반영된 상태의 재수신은 재처리하지 않음
- completed webhook는 필요 시 이용권 생성
- canceled webhook는 필요 시 이용권 환불 반영

## 7. 보류 및 참고 사항

- `skipped` 테스트 7건은 릴리스 차단 이슈가 아니라 현재 테스트 정책 또는 환경 차이로 제외된 케이스다.
- `@MockBean` 경고는 향후 Spring Boot 업그레이드 대응 항목이며 현재 QA 판정과 분리한다.

## 8. 관련 문서

- [`lessonring-requirements-specification.md`](../requirements/lessonring-requirements-specification.md)
- [`../test-cases/README.md`](../test-cases/README.md)
- [`support/TestExternalMockConfig.md`](support/TestExternalMockConfig.md)
