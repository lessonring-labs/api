# 결제 컨트롤러 테스트 설계서

## 1. 기본 정보

| 항목 | 내용 |
|-----|-----|
| 대상 파일 | [PaymentControllerTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/api/PaymentControllerTest.java) |
| 대상 계층 | 결제 승인 API 컨트롤러 |
| 테스트 유형 | API |
| 주 우선순위 | P1 |
| 관련 기능 | 승인 요청 validation, 성공 응답 구조 |

## 2. 테스트 목적

컨트롤러 레벨에서 승인 요청이 정상 수신되는지와 필수값 누락 시 validation이 즉시 동작하는지 확인한다.

## 3. 범위

### 포함 범위

- 승인 성공 응답 구조
- `paymentKey` 필수값 검증
- `amount` 필수값 검증

### 제외 범위

- 실제 승인 서비스 로직
- PG 연동 처리

## 4. 사전 조건

- `PaymentPgService`는 mock bean이어야 한다.
- 보안 필터는 테스트를 방해하지 않도록 mock 또는 비활성 처리되어야 한다.

## 5. 상세 테스트 케이스

| ID | 우선순위 | 유형 | 시나리오 | 입력 | 기대 결과 |
|-----|-----|-----|-----|-----|-----|
| PAY-CTRL-001 | P1 | API | 결제 승인 요청 성공 | 유효한 `paymentKey`, `orderId`, `amount` | `200`, 성공 응답, `paymentId/status/paymentKey/amount` 반환 |
| PAY-CTRL-002 | P1 | API | `paymentKey` 누락 | `paymentKey=null` | `400`, validation 실패 |
| PAY-CTRL-003 | P1 | API | `amount` 누락 | `amount=null` | `400`, validation 실패 |

## 6. 판정 기준

- 필수값 누락은 서비스 계층 진입 전에 차단되어야 한다.
- 성공 시 응답 포맷이 표준 API 응답 규격과 일치해야 한다.
