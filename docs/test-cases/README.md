# 테스트 케이스 설계 문서

## 1. 문서 개요

이 디렉터리는 `src/test/java` 하위 테스트 파일을 기준으로 정리한 테스트 케이스 설계 문서 모음이다.

본 문서 세트의 목적은 다음과 같다.

- 테스트 코드의 의도와 검증 범위를 비개발자도 이해할 수 있도록 문서화
- 회귀 테스트 수행 시 어떤 시나리오가 보장되는지 명확화
- 신규 기능 추가 또는 리팩터링 시 누락되면 안 되는 품질 기준 제공
- 운영 장애 대응 시 어떤 영역이 자동 검증되고 있는지 추적 가능하게 정리

## 2. 문서 작성 기준

- 테스트 클래스 1개당 문서 1개를 작성한다.
- 제목과 설명은 한글로 작성한다.
- 실제 코드에 존재하는 테스트 시나리오를 기준으로 작성한다.
- 문서는 테스트 케이스 설계서 수준으로 유지하되, 구현 세부 코드 설명으로 과도하게 흐르지 않는다.

## 3. 문서 분류

### 3.1 인증 및 보안

- [인증 컨트롤러 테스트](/C:/wms/api/docs/test-cases/auth/api/AuthControllerTest.md)
- [JWT 토큰 제공자 테스트](/C:/wms/api/docs/test-cases/common/security/JwtTokenProviderImplTest.md)
- [웹훅 서명 검증 테스트](/C:/wms/api/docs/test-cases/payment/infrastructure/webhook/PaymentWebhookSignatureVerifierTest.md)

### 3.2 결제 API 및 애플리케이션

- [결제 컨트롤러 테스트](/C:/wms/api/docs/test-cases/payment/api/PaymentControllerTest.md)
- [결제 승인 서비스 단위 테스트](/C:/wms/api/docs/test-cases/payment/application/PaymentPgServiceTest.md)
- [결제 승인 통합 테스트](/C:/wms/api/docs/test-cases/payment/application/PaymentPgServiceIntegrationTest.md)
- [결제 승인 동시성 테스트](/C:/wms/api/docs/test-cases/payment/application/PaymentPgServiceConcurrencyTest.md)
- [결제 생성 멱등성 테스트](/C:/wms/api/docs/test-cases/payment/application/PaymentServiceIdempotencyTest.md)
- [결제 환불 PG 연동 테스트](/C:/wms/api/docs/test-cases/payment/application/PaymentServiceRefundWithPgTest.md)
- [결제 환불 통합 테스트](/C:/wms/api/docs/test-cases/common/security/payment/application/PaymentServiceIntegrationTest.md)
- [결제 웹훅 서비스 테스트](/C:/wms/api/docs/test-cases/payment/application/PaymentWebhookServiceTest.md)
- [결제 웹훅 재전송 테스트](/C:/wms/api/docs/test-cases/payment/application/PaymentWebhookReplayTest.md)
- [결제 교차 충돌 동시성 테스트](/C:/wms/api/docs/test-cases/payment/application/PaymentCrossConflictConcurrencyTest.md)
- [결제 2순위 충돌 동시성 테스트](/C:/wms/api/docs/test-cases/payment/application/PaymentCrossConflictSecondPriorityConcurrencyTest.md)

### 3.3 테스트 지원 설정

- [테스트 외부 연동 목 설정](/C:/wms/api/docs/test-cases/support/TestExternalMockConfig.md)

## 4. 현재 테스트 자산의 특징

- 테스트는 결제 도메인에 집중되어 있다.
- 단위 테스트, 통합 테스트, 컨트롤러 테스트, 동시성 테스트가 함께 존재한다.
- 운영 장애 가능성이 높은 승인, 환불, 웹훅, 멱등성, 동시성 시나리오가 우선 검증되고 있다.
- 반면 `member`, `booking`, `attendance`, `notification` 등은 독립 테스트 문서와 케이스가 상대적으로 부족하다.

## 5. 활용 가이드

- 개발자는 기능 수정 전 해당 문서의 합격 기준을 먼저 확인한다.
- QA는 문서의 시나리오를 수동 테스트 케이스와 연결할 수 있다.
- 리뷰어는 테스트 변경이 문서 범위를 약화시키지 않는지 확인한다.
