# Payment Follow-up Status

## 목적

2026-03-26 기준으로 결제 승인, 환불, webhook, 테스트 실행 환경에 대한 후속 작업 상태를 정리한다.
이 문서는 "이어서 할 일" 체크리스트를 현재 코드와 테스트 기준으로 재평가한 결과다.

## 상태 요약

| 항목 | 상태 | 비고 |
| --- | --- | --- |
| 1. 코드 기준점 정리 | 완료 | `PgClient`, `PaymentWebhookLogRepository`, `PaymentWebhookSignatureVerifier` 반영 확인 |
| 2. 테스트 실행 환경 준비 마무리 | 완료 | `TestExternalMockConfig`, Redis, 테스트 DB, JPA repository 동작 확인 |
| 3. 가장 가벼운 테스트부터 실행 | 완료 | 단위 테스트부터 통합/동시성 테스트까지 단계별 실행 후 정리 |
| 4. 테스트 깨지는 부분 보수 | 완료 | schema mismatch, bean 충돌, mock/stub 누락, 동시성 정합성 수정 |
| 5. 에러 코드 정리 | 완료 | 결제 승인/락/webhook 검증 전용 코드 추가 및 적용 |
| 6. 운영 로그 실제 코드 반영 | 완료 | approve/refund/webhook 요청, 성공, 실패, 락, 멱등 로그 반영 |
| 7. 마지막 배포 준비 | 부분 완료 | DB migration 정리는 완료, 운영 인프라 항목은 미완료 |

## 1. 코드 기준점 정리

- [완료] `PgClient`에 `getPayment(String paymentKey)` 존재
  - [PgClient.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/main/java/com/lessonring/api/payment/infrastructure/pg/PgClient.java)
- [완료] `PaymentWebhookLogRepository`가 `JpaRepository` 기반
  - [PaymentWebhookLogRepository.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/main/java/com/lessonring/api/payment/domain/repository/PaymentWebhookLogRepository.java)
- [완료] `PaymentWebhookSignatureVerifier` 네이밍 확정 및 사용처 연결
  - [PaymentWebhookSignatureVerifier.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/main/java/com/lessonring/api/payment/infrastructure/webhook/PaymentWebhookSignatureVerifier.java)
  - [PaymentWebhookController.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/main/java/com/lessonring/api/payment/api/PaymentWebhookController.java)

## 2. 테스트 실행 환경 준비 마무리

- [완료] `TestExternalMockConfig`에서 `PgClient` mock 제공
  - [TestExternalMockConfig.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/support/TestExternalMockConfig.java)
- [완료] Redis 락 기반 테스트 실행 가능 상태 확인
  - 승인/환불/webhook 동시성 테스트 통과 기준으로 확인
- [완료] 테스트 DB 테이블 생성 가능 상태 확인
  - Flyway 재적용 및 JPA schema validation 오류 수정 완료
- [완료] `findAll`, `deleteAll`, `count` 등 repository 기본 메서드 동작 확인
  - 결제/웹훅 통합 테스트에서 `deleteAll`, `findAll` 사용 기준으로 확인

## 3. 가장 가벼운 테스트부터 실행

- [완료] `PaymentWebhookSignatureVerifierTest`
- [완료] `PaymentPgServiceTest`
- [완료] `PaymentWebhookServiceTest`
- [완료] `PaymentPgServiceIntegrationTest`
- [대체 완료] `PaymentWebhookServiceIntegrationTest`
  - 현재 별도 클래스는 없고, [PaymentWebhookReplayTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/payment/application/PaymentWebhookReplayTest.java) 와 [PaymentWebhookServiceTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/payment/application/PaymentWebhookServiceTest.java) 조합으로 webhook 처리와 재전송 시나리오를 검증
- [완료] approve / refund / webhook 동시성 테스트
  - [PaymentPgServiceConcurrencyTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/payment/application/PaymentPgServiceConcurrencyTest.java)
  - [PaymentCrossConflictConcurrencyTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/payment/application/PaymentCrossConflictConcurrencyTest.java)
  - [PaymentCrossConflictSecondPriorityConcurrencyTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/payment/application/PaymentCrossConflictSecondPriorityConcurrencyTest.java)

## 4. 테스트 깨지는 부분 보수

- [완료] repository 메서드 누락 여부 점검 및 정리
- [완료] bean 충돌 정리
  - `PgClient` mock 중복 등록 이슈 해소
- [완료] `PgClient.getPayment()` mock/stub 누락 정리
- [완료] `FAILED` 상태 처리 및 operation 기록 정리
- [완료] 엔티티 필드명 및 DDL mismatch 수정
  - `payment_operation.response_payload` 매핑 정리
- [완료] Redis 락 및 트랜잭션 완료 시점 정합성 수정

## 5. 에러 코드 정리

이번 반영으로 아래 전용 코드가 추가됐다.

- [완료] `PAYMENT_APPROVE_IN_PROGRESS`
- [완료] `PAYMENT_APPROVE_ALREADY_COMPLETED`
- [완료] `PAYMENT_REFUND_NOT_ALLOWED`
  - 기존 코드 유지
- [완료] `PAYMENT_LOCK_ACQUISITION_FAILED`
- [완료] `PAYMENT_WEBHOOK_INVALID_SIGNATURE`
- [완료] `PAYMENT_WEBHOOK_VERIFICATION_FAILED`

적용 위치:

- [ErrorCode.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/main/java/com/lessonring/api/common/error/ErrorCode.java)
- [PaymentPgService.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/main/java/com/lessonring/api/payment/application/PaymentPgService.java)
- [PaymentService.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/main/java/com/lessonring/api/payment/application/PaymentService.java)
- [PaymentWebhookPgVerificationService.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/main/java/com/lessonring/api/payment/application/PaymentWebhookPgVerificationService.java)
- [PaymentWebhookSignatureVerifier.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/main/java/com/lessonring/api/payment/infrastructure/webhook/PaymentWebhookSignatureVerifier.java)

## 6. 운영 로그 실제 코드 반영

이번 반영으로 아래 로그를 코드에 추가했다.

- [완료] approve request / success / fail
- [완료] refund request / success / fail
- [완료] webhook request / duplicated / success / fail
- [완료] lock acquired / fail / released
- [완료] idempotent hit
- [완료] PG verify mismatch

적용 위치:

- [PaymentPgService.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/main/java/com/lessonring/api/payment/application/PaymentPgService.java)
- [PaymentService.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/main/java/com/lessonring/api/payment/application/PaymentService.java)
- [PaymentWebhookService.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/main/java/com/lessonring/api/payment/application/PaymentWebhookService.java)
- [PaymentWebhookPgVerificationService.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/main/java/com/lessonring/api/payment/application/PaymentWebhookPgVerificationService.java)

## 7. 마지막 배포 준비

- [완료] DB migration
  - `V13` webhook event 생성, `V14` 인덱스/제약 정리로 재배치 완료
- [미완료] webhook secret 설정
  - 운영 환경 값 주입과 비밀 관리 정책 확정 필요
- [미완료] Redis 운영 설정
  - 단일 인스턴스/클러스터, timeout, 장애 대응 기준 확정 필요
- [미완료] 로그/모니터링 대시보드
  - 수집 대상과 알람 임계치 미정
- [미완료] 알림 기준 정의
  - 승인 실패율, webhook 검증 실패율, 락 획득 실패율 기준 정의 필요

## 이번에 실제 처리한 내용

- 결제 승인 중복 요청과 락 획득 실패를 전용 에러 코드로 분리했다.
- 이미 완료된 결제의 재승인을 전용 에러 코드로 분리했다.
- webhook signature 오류와 PG 검증 실패를 전용 에러 코드로 분리했다.
- approve, refund, webhook 흐름에 요청/성공/실패/락 로그를 추가했다.
- 관련 테스트 assertion을 현재 에러 코드 기준으로 정리했다.
