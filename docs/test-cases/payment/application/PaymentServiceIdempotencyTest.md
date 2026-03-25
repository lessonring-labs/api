# 결제 생성 멱등성 테스트 설계서

## 1. 대상 정보

- 테스트 파일: [PaymentServiceIdempotencyTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/application/PaymentServiceIdempotencyTest.java)
- 대상 계층: 결제 생성 서비스
- 테스트 유형: 단위 테스트

## 2. 문서 목적

결제 생성 요청이 `idempotencyKey`를 기준으로 중복 생성되지 않도록 보장하는지 검증한다.

## 3. 상세 테스트 케이스

| ID | 시나리오 | 입력 조건 | 수행 절차 | 기대 결과 |
|-----|-----|-----|-----|-----|
| PAY-CREATE-IDEMP-001 | 기존 멱등 키 재사용 | 동일 `idempotencyKey`가 이미 존재 | create 호출 | 기존 Payment 반환 |
| PAY-CREATE-IDEMP-002 | 멱등 키 없음 | `idempotencyKey=null` | create 호출 | 신규 Payment 생성 |
| PAY-CREATE-IDEMP-003 | 새로운 멱등 키 | 저장소 조회 결과 없음 | create 호출 | 신규 Payment 생성 |

## 4. 합격 기준

- 중복 요청은 새 결제를 만들면 안 된다.
- 새 요청은 기존 데이터와 충돌 없이 생성되어야 한다.
