# 테스트 케이스 설계서

## 1. 문서 목적

본 디렉터리는 `src/test/java` 하위 자동화 테스트를 기준으로 작성한 테스트 케이스 설계 문서 모음이다.

이 문서 세트는 다음 목적을 가진다.

- 자동화 테스트가 무엇을 보장하는지 명확히 설명
- 코드 리뷰 시 테스트 범위 누락 여부를 빠르게 확인
- QA와 개발 간 테스트 책임 경계를 정리
- 회귀 테스트 우선순위를 운영 리스크 기준으로 관리

## 2. 문서 적용 원칙

- 테스트 클래스 1개당 문서 1개를 작성한다.
- 문서는 현재 구현된 테스트를 기준으로 작성한다.
- 구현되지 않은 테스트 아이디어는 본 문서가 아닌 별도 개선 문서에서 관리한다.
- 각 테스트 케이스는 `ID`, `우선순위`, `유형`, `시나리오`, `기대 결과`를 포함한다.

## 3. 우선순위 기준

| 우선순위 | 의미 | 기준 |
|-----|-----|-----|
| P0 | 장애 직결 | 금전, 인증, 중복 처리, 데이터 정합성, 보안 |
| P1 | 핵심 기능 | 주요 API 성공/실패 흐름, 회귀 영향 큼 |
| P2 | 보조 기능 | 포맷, 일부 예외 케이스, 운영 편의성 |

## 4. 테스트 유형 기준

| 유형 | 설명 |
|-----|-----|
| 단위 | 단일 클래스/로직 중심 검증 |
| API | 컨트롤러 경계, 요청/응답 검증 |
| 통합 | 저장소, 트랜잭션, 이벤트 포함 검증 |
| 동시성 | 락, 멱등성, 경쟁 상태 검증 |
| 보안 | 서명, 토큰, 인증/인가 관련 검증 |

## 5. 문서 목록

### 5.1 인증 및 보안

- [인증 컨트롤러 테스트 설계서](/C:/wms/api/docs/test-cases/auth/api/AuthControllerTest.md)
- [JWT 토큰 제공자 테스트 설계서](/C:/wms/api/docs/test-cases/common/security/JwtTokenProviderImplTest.md)
- [웹훅 서명 검증 테스트 설계서](/C:/wms/api/docs/test-cases/payment/infrastructure/webhook/PaymentWebhookSignatureVerifierTest.md)

### 5.2 결제 API 및 애플리케이션

- [결제 컨트롤러 테스트 설계서](/C:/wms/api/docs/test-cases/payment/api/PaymentControllerTest.md)
- [결제 승인 서비스 단위 테스트 설계서](/C:/wms/api/docs/test-cases/payment/application/PaymentPgServiceTest.md)
- [결제 승인 통합 테스트 설계서](/C:/wms/api/docs/test-cases/payment/application/PaymentPgServiceIntegrationTest.md)
- [결제 승인 동시성 테스트 설계서](/C:/wms/api/docs/test-cases/payment/application/PaymentPgServiceConcurrencyTest.md)
- [결제 생성 멱등성 테스트 설계서](/C:/wms/api/docs/test-cases/payment/application/PaymentServiceIdempotencyTest.md)
- [결제 환불 PG 연동 테스트 설계서](/C:/wms/api/docs/test-cases/payment/application/PaymentServiceRefundWithPgTest.md)
- [결제 환불 통합 테스트 설계서](/C:/wms/api/docs/test-cases/common/security/payment/application/PaymentServiceIntegrationTest.md)
- [결제 웹훅 서비스 테스트 설계서](/C:/wms/api/docs/test-cases/payment/application/PaymentWebhookServiceTest.md)
- [결제 웹훅 재전송 테스트 설계서](/C:/wms/api/docs/test-cases/payment/application/PaymentWebhookReplayTest.md)
- [결제 교차 충돌 동시성 테스트 설계서](/C:/wms/api/docs/test-cases/payment/application/PaymentCrossConflictConcurrencyTest.md)
- [결제 2순위 충돌 동시성 테스트 설계서](/C:/wms/api/docs/test-cases/payment/application/PaymentCrossConflictSecondPriorityConcurrencyTest.md)

### 5.3 테스트 지원 설정

- [테스트 외부 연동 목 설정 문서](/C:/wms/api/docs/test-cases/support/TestExternalMockConfig.md)

## 6. 현재 테스트 자산 평가

### 강점

- 결제 도메인의 정합성 검증이 강하다.
- 승인, 환불, 웹훅, 멱등성, 동시성 시나리오가 분리되어 있다.
- 단위, API, 통합, 동시성 테스트가 함께 존재한다.

### 약점

- 결제 외 도메인 테스트 자산이 부족하다.
- 인증 성공 플로우, member, booking, attendance, notification 계열의 독립 테스트가 부족하다.
- 일부 패키지 경로와 실제 도메인 관심사가 일치하지 않는다.

## 7. 활용 기준

- 개발자는 기능 변경 시 해당 문서의 `합격 기준`을 유지해야 한다.
- QA는 문서의 상세 케이스를 수동 시나리오와 연결할 수 있다.
- 리뷰어는 테스트 변경이 `P0` 또는 `P1` 범위를 약화시키는지 확인해야 한다.
