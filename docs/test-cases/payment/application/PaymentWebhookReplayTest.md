# 결제 웹훅 재전송 테스트 설계서

## 1. 기본 정보

| 항목 | 내용 |
|-----|-----|
| 대상 파일 | [PaymentWebhookReplayTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/payment/application/PaymentWebhookReplayTest.java) |
| 대상 계층 | 결제 웹훅 중복 처리 |
| 테스트 유형 | 통합 |
| 주 우선순위 | P0 |
| 관련 기능 | transmissionId 중복 방지, 상태 기반 재처리 방지 |

## 2. 테스트 목적

webhook 재전송, 공급사 중복 전송, transmissionId 재사용 상황에서 상태가 한 번만 반영되는지 확인한다.

## 3. 상세 테스트 케이스

| ID | 우선순위 | 유형 | 시나리오 | 기대 결과 |
|-----|-----|-----|-----|-----|
| PAY-WEBHOOK-REPLAY-001 | P0 | 통합 | 동일 transmissionId 재전송 | 두 번째 요청 무시, 로그 1건 |
| PAY-WEBHOOK-REPLAY-002 | P1 | 통합 | transmissionId 없이 completed 재전송 | 상태 1회 반영, 재처리 skip |
| PAY-WEBHOOK-REPLAY-003 | P1 | 통합 | transmissionId 없이 canceled 재전송 | 상태 1회 반영, 재처리 skip |
| PAY-WEBHOOK-REPLAY-004 | P1 | 통합 | 동일 transmissionId, 다른 payload | 현재 정책상 두 번째 요청 무시 |

## 4. 판정 기준

- 재전송이 최종 결제 상태를 뒤틀면 실패다.

## 현재 테스트 메서드 기준

| 메서드 | DisplayName | 문서 케이스 |
|-----|-----|-----|
| `duplicated_transmission_id_is_ignored` | 동일 transmissionId 재전송이면 두 번째 요청은 무시된다 | `PAY-WEBHOOK-REPLAY-001` |
| `replay_without_transmission_id_completed_is_skipped_by_status` | transmissionId 없이 동일 completed webhook 재전송되면 상태는 한 번만 반영되고 두 번째는 skip 된다 | `PAY-WEBHOOK-REPLAY-002` |
| `replay_without_transmission_id_canceled_is_skipped_by_status` | transmissionId 없이 동일 canceled webhook 재전송되면 상태는 한 번만 반영되고 두 번째는 skip 된다 | `PAY-WEBHOOK-REPLAY-003` |
| `same_transmission_id_with_different_payload_is_ignored_by_current_policy` | 동일 transmissionId 이지만 payload가 달라도 현재 정책상 두 번째 요청은 무시된다 | `PAY-WEBHOOK-REPLAY-004` |
