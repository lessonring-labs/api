# 테스트 케이스 설계서

## 1. 문서 목적

본 디렉터리는 `src/test/java` 하위 자동화 테스트를 현재 구현 기준으로 설명하는 설계 문서 인덱스다.

- 어떤 테스트가 어떤 요구사항을 보장하는지 확인
- 회귀 테스트 범위와 우선순위 공유
- QA 문서와 개발 테스트 자산을 연결
- 결제/인증 고위험 시나리오의 커버리지를 추적

## 2. 현재 기준선

| 항목 | 내용 |
|-----|-----|
| 기준일 | 2026-03-25 |
| 기준 명령 | `./gradlew test` |
| 최근 결과 | 전체 통과 |
| 테스트 클래스 수 | 14 |
| 주요 범위 | 인증, 결제 승인, 환불, webhook, 멱등성, 동시성 |

## 3. 우선순위 기준

| 우선순위 | 의미 |
|-----|-----|
| P0 | 금전, 인증, 보안, 멱등성, 동시성, 정합성 |
| P1 | 핵심 API 및 서비스 흐름 |
| P2 | 보조 예외, 운영 편의성, 표현 계층 검증 |

## 4. 테스트 유형 기준

| 유형 | 설명 |
|-----|-----|
| API | 컨트롤러 요청/응답, validation, 예외 변환 |
| 단위 | 단일 클래스 로직 검증 |
| 통합 | 저장소, 트랜잭션, 이벤트, 직렬화 포함 |
| 동시성 | 멱등성, 경쟁 상태, 락 검증 |
| 보안 | 인증, 토큰, 서명 검증 |

## 5. 테스트 인벤토리

| 테스트 클래스 | 유형 | 우선순위 | 핵심 보장 |
|-----|-----|-----|-----|
| [`AuthControllerTest.java`](../../src/test/java/com/lessonring/api/auth/api/AuthControllerTest.java) | API | P0 | 로그인/재발급/로그아웃 validation 및 예외 응답 |
| [`JwtTokenProviderImplTest.java`](../../src/test/java/com/lessonring/api/common/security/JwtTokenProviderImplTest.java) | 보안 | P0 | JWT 생성/검증 안정성 |
| [`PaymentWebhookSignatureVerifierTest.java`](../../src/test/java/com/lessonring/api/payment/infrastructure/webhook/PaymentWebhookSignatureVerifierTest.java) | 보안 | P0 | webhook 서명 검증 |
| [`PaymentControllerTest.java`](../../src/test/java/com/lessonring/api/payment/api/PaymentControllerTest.java) | API | P1 | 승인 API 성공/validation |
| [`PaymentPgServiceTest.java`](../../src/test/java/com/lessonring/api/payment/application/PaymentPgServiceTest.java) | 단위 | P0 | 승인 성공/실패/상태 전이 |
| [`PaymentPgServiceIntegrationTest.java`](../../src/test/java/com/lessonring/api/payment/application/PaymentPgServiceIntegrationTest.java) | 통합 | P0 | 승인 멱등성, operation 기록, 락 실패 처리 |
| [`PaymentPgServiceConcurrencyTest.java`](../../src/test/java/com/lessonring/api/payment/application/PaymentPgServiceConcurrencyTest.java) | 동시성 | P0 | 동시 승인 시 1회 처리 보장 |
| [`PaymentServiceIdempotencyTest.java`](../../src/test/java/com/lessonring/api/payment/application/PaymentServiceIdempotencyTest.java) | 단위 | P1 | 결제 생성 멱등성 |
| [`PaymentServiceRefundWithPgTest.java`](../../src/test/java/com/lessonring/api/payment/application/PaymentServiceRefundWithPgTest.java) | 단위 | P0 | 환불 시 PG 취소 성공/실패 분기 |
| [`PaymentServiceIntegrationTest.java`](../../src/test/java/com/lessonring/api/common/security/payment/application/PaymentServiceIntegrationTest.java) | 통합 | P0 | 환불 금액, 예약 취소, 이벤트, 멱등성 |
| [`PaymentWebhookServiceTest.java`](../../src/test/java/com/lessonring/api/payment/application/PaymentWebhookServiceTest.java) | 단위 | P0 | completed/failed/canceled webhook 반영 |
| [`PaymentWebhookReplayTest.java`](../../src/test/java/com/lessonring/api/payment/application/PaymentWebhookReplayTest.java) | 통합 | P0 | transmission 재전송 및 동일 상태 재처리 방지 |
| [`PaymentCrossConflictConcurrencyTest.java`](../../src/test/java/com/lessonring/api/payment/application/PaymentCrossConflictConcurrencyTest.java) | 동시성 | P0 | approve/refund/webhook 교차 충돌 조정 |
| [`PaymentCrossConflictSecondPriorityConcurrencyTest.java`](../../src/test/java/com/lessonring/api/payment/application/PaymentCrossConflictSecondPriorityConcurrencyTest.java) | 동시성 | P0 | completed/canceled 우선순위 규칙 검증 |

## 6. 요구사항 추적 요약

| 요구사항 | 대표 테스트 |
|-----|-----|
| 인증 요청 validation 및 토큰 흐름 | `AuthControllerTest`, `JwtTokenProviderImplTest` |
| 결제 승인 | `PaymentControllerTest`, `PaymentPgServiceTest`, `PaymentPgServiceIntegrationTest` |
| 결제 멱등성 | `PaymentServiceIdempotencyTest`, `PaymentPgServiceIntegrationTest`, `PaymentPgServiceConcurrencyTest` |
| 결제 환불 | `PaymentServiceRefundWithPgTest`, `PaymentServiceIntegrationTest` |
| webhook 처리 | `PaymentWebhookServiceTest`, `PaymentWebhookReplayTest` |
| 교차 충돌 및 경쟁 상태 | `PaymentPgServiceConcurrencyTest`, `PaymentCrossConflictConcurrencyTest`, `PaymentCrossConflictSecondPriorityConcurrencyTest` |

## 7. 현재 설계상 특징

- 결제 도메인 테스트 밀도가 가장 높다.
- 단위, 통합, 동시성 레벨이 분리되어 있다.
- `payment_operation`, Redis 기반 상태 락, webhook 재전송 정책까지 테스트 범위에 포함된다.
- webhook `completed`는 내부 이용권 생성까지, `canceled`는 이용권 환불까지 동기화하도록 현재 구현이 반영돼 있다.

## 8. 현재 한계

- 결제 외 member, booking, attendance, notification 독립 테스트는 부족하다.
- `@MockBean` 사용 테스트는 경고가 남아 있어 향후 Spring Boot 업그레이드 시 정리가 필요하다.
- 일부 `skipped` 테스트는 현재 환경 또는 정책 기준으로 실행 제외 상태다.

## 9. 관련 문서

- [`../qa-test-cases/README.md`](../qa-test-cases/README.md)
- [`../requirements/lessonring-requirements-specification.md`](../requirements/lessonring-requirements-specification.md)
- [`support/TestExternalMockConfig.md`](support/TestExternalMockConfig.md)
