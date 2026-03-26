# QA - 결제 웹훅 재전송 테스트 케이스

## 1. 문서 기본 정보

| 항목 | 내용 |
|-----|-----|
| 문서명 | 결제 웹훅 재전송 테스트 케이스 |
| 기준 테스트 파일 | [PaymentWebhookReplayTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/payment/application/PaymentWebhookReplayTest.java) |
| 모듈 | 결제 웹훅 |
| 테스트 유형 | 통합 |
| 우선순위 | P0 |

## 2. 테스트 케이스 상세

### PAY-WEBHOOK-REPLAY-QA-001 동일 transmissionId 재전송 시 두 번째 요청 무시

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 동일 transmissionId 처리 가능 |
| 입력값 | 동일 transmissionId, 동일 payload |
| 수행절차 | 동일 webhook 두 번 호출 |
| 예상결과 | 첫 번째만 처리, 로그 1건 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-WEBHOOK-REPLAY-QA-002 transmissionId 없이 completed 재전송

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | completed webhook 처리 가능 |
| 입력값 | transmissionId 없음 |
| 수행절차 | 동일 webhook 두 번 호출 |
| 예상결과 | 상태 1회 반영, 재처리 skip |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-WEBHOOK-REPLAY-QA-003 transmissionId 없이 canceled 재전송

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | canceled webhook 처리 가능 |
| 입력값 | transmissionId 없음 |
| 수행절차 | 동일 webhook 두 번 호출 |
| 예상결과 | 상태 1회 반영, 재처리 skip |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-WEBHOOK-REPLAY-QA-004 동일 transmissionId, 다른 payload 재전송

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | transmissionId 기반 중복 정책 적용 |
| 입력값 | 동일 transmissionId, 다른 payload |
| 수행절차 | 순차 호출 |
| 예상결과 | 현재 정책상 두 번째 요청 무시 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

## 3. 수행 요약

| 항목 | 내용 |
|-----|-----|
| 수행자 |  |
| 수행일 |  |
| 수행 버전 |  |
| 결과 요약 |  |

## 현재 테스트 메서드 기준

| 메서드 | DisplayName | QA 케이스 |
|-----|-----|-----|
| `duplicated_transmission_id_is_ignored` | 동일 transmissionId 재전송이면 두 번째 요청은 무시된다 | `PAY-WEBHOOK-REPLAY-QA-001` |
| `replay_without_transmission_id_completed_is_skipped_by_status` | transmissionId 없이 동일 completed webhook 재전송되면 상태는 한 번만 반영되고 두 번째는 skip 된다 | `PAY-WEBHOOK-REPLAY-QA-002` |
| `replay_without_transmission_id_canceled_is_skipped_by_status` | transmissionId 없이 동일 canceled webhook 재전송되면 상태는 한 번만 반영되고 두 번째는 skip 된다 | `PAY-WEBHOOK-REPLAY-QA-003` |
| `same_transmission_id_with_different_payload_is_ignored_by_current_policy` | 동일 transmissionId 이지만 payload가 달라도 현재 정책상 두 번째 요청은 무시된다 | `PAY-WEBHOOK-REPLAY-QA-004` |
