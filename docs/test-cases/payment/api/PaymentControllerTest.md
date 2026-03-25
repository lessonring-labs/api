# 결제 컨트롤러 테스트 설계서

## 1. 대상 정보

- 테스트 파일: [PaymentControllerTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/api/PaymentControllerTest.java)
- 대상 계층: 결제 승인 API 컨트롤러
- 테스트 유형: Web MVC 테스트

## 2. 문서 목적

결제 승인 요청이 컨트롤러 레벨에서 정상 응답 포맷을 유지하는지, 필수값 누락 시 validation 에러가 발생하는지 확인한다.

## 3. 검증 범위

- 승인 API 성공 응답
- `paymentKey` 필수값 검증
- `amount` 필수값 검증

## 4. 사전 조건

- `PaymentPgService`는 mock bean으로 대체되어야 한다.
- `JwtAuthenticationFilter`는 테스트에서 비활성 또는 mock 처리되어야 한다.
- 글로벌 예외 처리기가 import 되어 있어야 한다.

## 5. 상세 테스트 케이스

| ID | 시나리오 | 입력 조건 | 수행 절차 | 기대 결과 |
|-----|-----|-----|-----|-----|
| PAY-CTRL-001 | 결제 승인 요청 성공 | 유효한 `paymentKey`, `orderId`, `amount` | `/api/v1/payments/{id}/approve` 호출 | `200 OK`, 성공 응답, 주요 필드 반환 |
| PAY-CTRL-002 | `paymentKey` 누락 | `paymentKey` 없는 요청 | 승인 API 호출 | `400 Bad Request`, validation 실패 |
| PAY-CTRL-003 | `amount` 누락 | `amount` 없는 요청 | 승인 API 호출 | `400 Bad Request`, validation 실패 |

## 6. 합격 기준

- 정상 요청은 표준 성공 응답 포맷을 반환해야 한다.
- 필수값 누락은 비즈니스 로직 진입 전에 차단되어야 한다.
