# 결제 웹훅 재전송 테스트 설계서

## 1. 대상 정보

- 테스트 파일: [PaymentWebhookReplayTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/application/PaymentWebhookReplayTest.java)
- 대상 계층: 결제 웹훅 중복 처리 정책
- 테스트 유형: 통합 테스트

## 2. 문서 목적

동일 webhook 이벤트가 네트워크 재전송, 공급사 재시도, 중복 발송 등의 이유로 다시 들어올 때 중복 반영이 발생하지 않는지 검증한다.

## 3. 상세 테스트 케이스

| ID | 시나리오 | 입력 조건 | 수행 절차 | 기대 결과 |
|-----|-----|-----|-----|-----|
| PAY-WEBHOOK-REPLAY-001 | 동일 `transmissionId` 재전송 | 동일 transmissionId, 동일 payload | 두 번 호출 | 두 번째 요청 무시, 로그 1건 |
| PAY-WEBHOOK-REPLAY-002 | transmissionId 없이 completed 재전송 | transmissionId 없음 | 두 번 호출 | 상태는 1회만 반영, 로그는 호출 수만큼 적재 가능 |
| PAY-WEBHOOK-REPLAY-003 | transmissionId 없이 canceled 재전송 | transmissionId 없음 | 두 번 호출 | 상태는 1회만 반영 |
| PAY-WEBHOOK-REPLAY-004 | 동일 transmissionId, 다른 payload | eventType 상이, transmissionId 동일 | 두 번 호출 | 현재 정책상 두 번째 요청 무시 |

## 4. 합격 기준

- 중복 수신이 최종 상태를 왜곡하면 실패다.
- transmissionId 기반 정책과 상태 기반 정책이 함께 작동해야 한다.
