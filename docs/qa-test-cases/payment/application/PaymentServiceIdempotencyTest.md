# QA - 결제 생성 멱등성 테스트 케이스

## 1. 문서 기본 정보

| 항목 | 내용 |
|-----|-----|
| 문서명 | 결제 생성 멱등성 테스트 케이스 |
| 기준 테스트 파일 | [PaymentServiceIdempotencyTest.java](/C:/wms/api/src/test/java/com/lessonring/api/payment/application/PaymentServiceIdempotencyTest.java) |
| 모듈 | 결제 생성 |
| 테스트 유형 | 단위 |
| 우선순위 | P0 |

## 2. 테스트 케이스 상세

### PAY-CREATE-QA-001 기존 멱등 키 재사용 시 기존 결제 반환

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 동일 `idempotencyKey` 결제 기존 존재 |
| 입력값 | 기존 키와 동일한 생성 요청 |
| 수행절차 | create 호출 |
| 예상결과 | 신규 결제 생성 없이 기존 Payment 반환 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-CREATE-QA-002 멱등 키 없이 신규 결제 생성

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | 결제 생성 가능한 회원 존재 |
| 입력값 | `idempotencyKey=null` |
| 수행절차 | create 호출 |
| 예상결과 | 신규 Payment 생성 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### PAY-CREATE-QA-003 새로운 멱등 키로 신규 결제 생성

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P0 |
| 사전조건 | 해당 키로 기존 결제 없음 |
| 입력값 | 새로운 `idempotencyKey` |
| 수행절차 | create 호출 |
| 예상결과 | 신규 Payment 생성 |
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
