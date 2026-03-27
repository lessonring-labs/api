# Logging Standard

이 문서는 LessonRing Backend의 애플리케이션 로그 표준안을 정의한다.

목표:

- 장애 추적 가능한 로그 구조 정의
- 운영 지표 집계 가능한 로그 키 표준화
- 민감정보 노출 방지 기준 정리
- 결제/예약/출석/인증 구간 로그 품질 향상

---

# 1. 로그 목적

## 1.1 장애 추적

- 어떤 요청이 실패했는가
- 어느 도메인에서 실패했는가
- 어떤 식별자에 영향이 있었는가

## 1.2 정합성 검증

- 결제는 되었는데 회원권은 생성되었는가
- 환불은 되었는데 예약은 취소되었는가
- 출석 처리 후 회원권 차감은 반영되었는가

## 1.3 운영 계수

- 결제 요청 수 / 성공 수 / 실패 수
- 예약 생성 수 / 취소 수 / no-show 수
- webhook 수신 수 / 중복 무시 수

---

# 2. 로그 레벨 기준

## 2.1 `INFO`

정상 처리 흐름과 운영 집계에 필요한 이벤트

## 2.2 `WARN`

비정상 입력, 비즈니스 예외, 재시도 가능한 문제

## 2.3 `ERROR`

예상하지 못한 예외, 시스템 장애, 데이터 손상 가능성

---

# 3. 공통 필드 표준

## 3.1 필수 권장 필드

- `timestamp`
- `level`
- `service`
- `module`
- `action`
- `result`

## 3.2 요청 추적 필드

- `requestId`
- `traceId`
- `path`
- `method`
- `clientIp`

## 3.3 도메인 식별자 필드

- `studioId`
- `memberId`
- `bookingId`
- `attendanceId`
- `membershipId`
- `paymentId`
- `scheduleId`

## 3.4 외부 연동 식별자 필드

- `orderId`
- `paymentKey`
- `provider`
- `transmissionId`
- `idempotencyKey`

## 3.5 오류 및 성능 필드

- `errorCode`
- `exceptionClass`
- `message`
- `durationMs`

---

# 4. 로그 메시지 형식 기준

```text
{module} {action} {result}. key1={}, key2={}, key3={}
```

예:

```text
payment approve requested. paymentId={}, orderId={}, idempotencyKey={}
payment approve succeeded. paymentId={}, membershipId={}, paymentKey={}
payment refund failed. paymentId={}, errorCode={}, message={}
payment webhook handled. provider={}, eventType={}, orderId={}, paymentId={}, transmissionId={}
```

---

# 5. 요청 단위 추적 표준

## 5.1 `requestId` 도입

모든 HTTP 요청에 대해 `requestId`를 발급하고 응답 헤더에도 포함하는 것을 권장한다.

## 5.2 `traceId` 확장

향후 외부 관측성 도구와 연계할 가능성을 고려하면 `traceId`도 지원하는 것이 좋다.

---

# 6. 영역별 로그 표준

## 6.1 API Access Log

```text
api request started. requestId={}, method={}, path={}, memberId={}
api request completed. requestId={}, method={}, path={}, status={}, durationMs={}
api request failed. requestId={}, method={}, path={}, status={}, errorCode={}, durationMs={}
```

## 6.2 결제 로그

```text
payment approve requested. paymentId={}, orderId={}, idempotencyKey={}
payment approve succeeded. paymentId={}, membershipId={}, paymentKey={}, durationMs={}
payment approve failed. paymentId={}, errorCode={}, message={}, durationMs={}
payment refund requested. paymentId={}, idempotencyKey={}
payment refund succeeded. paymentId={}, membershipId={}, refundAmount={}, canceledBookings={}, durationMs={}
payment refund failed. paymentId={}, errorCode={}, message={}, durationMs={}
```

## 6.3 webhook 로그

```text
payment webhook requested. provider={}, transmissionId={}, eventType={}, orderId={}
duplicated payment webhook ignored. provider={}, transmissionId={}, eventType={}, orderId={}
payment webhook handled. provider={}, eventType={}, orderId={}, paymentId={}, transmissionId={}
payment webhook failed. provider={}, transmissionId={}, eventType={}, orderId={}, errorCode={}, message={}
```

## 6.4 예약/출석 로그

```text
booking create requested. memberId={}, scheduleId={}, membershipId={}
booking create succeeded. bookingId={}, memberId={}, scheduleId={}, membershipId={}
booking create failed. memberId={}, scheduleId={}, membershipId={}, errorCode={}
booking cancel succeeded. bookingId={}, memberId={}, scheduleId={}
attendance create succeeded. attendanceId={}, bookingId={}, memberId={}, membershipId={}, remainingCountAfter={}
attendance cancel succeeded. attendanceId={}, bookingId={}, memberId={}, membershipId={}
```

## 6.5 인증/배치 로그

```text
authentication failed. requestId={}, path={}, memberId={}, errorCode={}, message={}
jwt validation failed. requestId={}, path={}, message={}
no-show batch completed. batchStartedAt={}, batchEndedAt={}, processed={}, failedCount={}
```

---

# 7. 금지 항목

- JWT 원문
- 비밀키
- Access Token / Refresh Token 전문
- 카드 정보 또는 결제 민감정보
- 불필요한 개인정보 원문
- 비밀번호성 값

---

# 8. 구현 우선순위

1. `requestId` 도입
2. API access log 도입
3. 예약/출석 로그 보강
4. 결제 성공/실패 로그에 `durationMs` 추가
5. webhook 로그에 결과 필드 보강
6. 배치 로그 표준화
