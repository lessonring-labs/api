# 테스트 외부 연동 목 설정 문서

## 1. 기본 정보

| 항목 | 내용 |
|-----|-----|
| 대상 파일 | [`TestExternalMockConfig.java`](../../../src/test/java/com/lessonring/api/support/TestExternalMockConfig.java) |
| 역할 | 테스트 지원 설정 |
| 주요 대상 | 결제 승인, 환불, webhook 관련 통합 테스트 |
| 현재 상태 | 사용 중 |

## 2. 문서 목적

이 문서는 테스트 케이스가 아니라 테스트 실행 환경의 외부 연동 대체 구성을 설명한다.

## 3. 주요 역할

- `PgClient`를 mock bean으로 등록
- 실 결제 네트워크 호출 차단
- 통합 테스트가 테스트 시나리오별로 PG 응답을 제어할 수 있도록 지원
- 외부 PG 가용성과 무관하게 테스트 재현성을 확보

## 4. 사용 이유

- 외부 PG 가용성과 무관하게 테스트를 반복 실행하기 위함
- 승인 성공/실패, 환불 성공/실패, webhook 검증용 응답을 안정적으로 재현하기 위함
- CI 또는 로컬 환경에서 네트워크 의존성 없이 테스트를 통과시키기 위함

## 5. 리스크

- 실제 PG 계약 변화는 이 mock 설정만으로 감지되지 않는다.
- 계약 테스트 또는 샌드박스 연동 테스트가 별도로 필요하다.

## 6. 관련 테스트

- [`PaymentPgServiceIntegrationTest.java`](../../../src/test/java/com/lessonring/api/payment/application/PaymentPgServiceIntegrationTest.java)
- [`PaymentPgServiceConcurrencyTest.java`](../../../src/test/java/com/lessonring/api/payment/application/PaymentPgServiceConcurrencyTest.java)
- [`PaymentWebhookReplayTest.java`](../../../src/test/java/com/lessonring/api/payment/application/PaymentWebhookReplayTest.java)
- [`PaymentCrossConflictConcurrencyTest.java`](../../../src/test/java/com/lessonring/api/payment/application/PaymentCrossConflictConcurrencyTest.java)
