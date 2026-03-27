# 데이터 추적 및 분석 (Analytics Plan)

이 문서는 2026-03 기준 LessonRing Backend 코드베이스를 분석한 결과를 바탕으로, **로그 설계와 데이터 기반 의사결정 지표**를 정의하기 위한 문서다.

현재 프로젝트는 `analytics` 모듈이 아직 TODO 상태이며, 별도의 대시보드나 배치 집계 체계도 구현되어 있지 않다. 따라서 이 문서는 "이미 존재하는 데이터와 로그 자산을 기반으로 어떤 분석 체계를 단계적으로 구축할 것인가"에 초점을 둔다.

---

# 1. 현재 프로젝트 분석 요약

## 1.1 프로젝트 성격

LessonRing Backend는 스튜디오 운영형 백엔드이며, 다음 흐름이 서로 강하게 연결되어 있다.

- 회원 등록
- 회원권 생성 및 사용
- 수업 일정 운영
- 예약 생성/취소/no-show
- 출석 처리
- 결제 승인/환불
- 알림 생성

따라서 분석 체계도 단순 방문자 수나 요청 수보다 **운영 정합성**, **결제 안정성**, **예약 운영 효율**, **사용자 행동 흐름**을 중심으로 설계해야 한다.

## 1.2 현재 존재하는 분석 자산

현재 코드 기준으로 이미 활용 가능한 데이터 원천은 다음과 같다.

- 도메인 테이블
  - `member`
  - `membership`
  - `schedule`
  - `booking`
  - `attendance`
  - `payment`
  - `notification`
- 결제 추적 테이블
  - `payment_operation`
  - `payment_webhook_log`
  - `payment_webhook_event`
- 공통 운영 로그
  - `GlobalExceptionHandler` 예외 로그
  - `JwtAuthenticationFilter` 인증 오류 로그
  - `BookingNoShowScheduler` 배치 로그
  - `PaymentPgService`, `PaymentService`, `PaymentWebhookService` 처리 로그
- 도메인 이벤트 기반 후속 처리
  - `BookingCreatedEvent`
  - `BookingCanceledEvent`
  - `MembershipUsedEvent`
  - `PaymentCompletedEvent`
  - `PaymentCanceledEvent`
- 알림 엔티티
  - `notification.type`
  - `notification.readAt`

즉, 분석용 모듈은 아직 비어 있지만, 분석 가능한 운영 데이터는 이미 상당 부분 쌓일 수 있는 구조다.

## 1.3 현재 상태의 한계

현재 분석 관점에서 부족한 점은 다음과 같다.

- 요청 단위 추적 ID가 없다.
- API 접근 로그 표준이 없다.
- 구조화된 애플리케이션 로그 포맷이 없다.
- 운영 지표를 조회하는 `analytics` API가 미구현 상태다.
- 이벤트가 알림 생성에는 쓰이지만, 분석 적재에는 아직 연결되지 않았다.
- 장애/정합성 KPI를 자동 집계하는 배치나 SQL 리포트가 없다.

따라서 본 문서의 목표는 이 공백을 메우는 설계 기준을 만드는 것이다.

---

# 2. Analytics 목표

LessonRing Backend의 Analytics Plan은 다음 4가지 목표를 가진다.

## 2.1 운영 정합성 추적

다음과 같은 "처리는 되었지만 상태가 맞지 않는" 문제를 빠르게 발견해야 한다.

- 결제 완료 후 회원권 미생성
- 환불 완료 후 회원권 미환불
- 환불 완료 후 미래 예약 미취소
- 출석 처리 후 회원권 차감 불일치
- 중복 예약 또는 중복 결제 시도

## 2.2 운영 성과 측정

스튜디오 운영 관점에서 다음 흐름을 숫자로 봐야 한다.

- 신규 회원 증가
- 활성 회원권 규모
- 예약 완료/취소/no-show 비율
- 출석 전환율
- 결제 성공/실패/환불 추이

## 2.3 시스템 안정성 측정

개발/운영 관점에서 다음을 지속적으로 확인해야 한다.

- API 성공률과 오류율
- 결제 API 응답 시간
- Redis 락 충돌 징후
- webhook 처리 실패 또는 중복 수신 비율
- 예외 로그 증가 추세

## 2.4 데이터 기반 의사결정 지원

향후 다음 의사결정에 활용할 수 있어야 한다.

- 어떤 수업 시간대의 예약률이 높은가
- no-show가 높은 회원군/시간대가 있는가
- 어떤 결제 실패 사유가 반복되는가
- 어떤 알림 유형이 실제 읽히는가
- 어떤 운영 흐름에서 병목이 발생하는가

---

# 3. 데이터 원천 설계

## 3.1 1차 데이터 원천: 운영 DB

가장 먼저 신뢰해야 하는 데이터 원천은 운영 테이블이다.

### 핵심 운영 테이블

- `member`
- `membership`
- `schedule`
- `booking`
- `attendance`
- `payment`
- `notification`

### 핵심 추적 테이블

- `payment_operation`
- `payment_webhook_log`
- `payment_webhook_event`

이 테이블들은 "최종 상태"와 "처리 이력"을 함께 보여주기 때문에 KPI 산출의 기준점이 된다.

## 3.2 2차 데이터 원천: 애플리케이션 로그

현재 코드상 로그가 이미 존재하는 주요 구간은 다음과 같다.

- 결제 승인 요청/락 획득/성공/실패
- 결제 환불 요청/락 획득/성공/실패
- webhook 요청/중복 무시/락 획득/처리 성공/처리 실패
- 전역 예외 처리
- JWT 인증 오류
- no-show 배치 처리 건수

이 로그는 장애 추적과 운영 이벤트 계수에 직접 활용할 수 있다.

## 3.3 3차 데이터 원천: 도메인 이벤트

현재 이벤트는 주로 알림 생성용으로 사용되지만, 향후 analytics 적재에도 가장 자연스러운 확장 지점이다.

분석 적재 대상으로 우선 추천하는 이벤트는 다음과 같다.

- `BookingCreatedEvent`
- `BookingCanceledEvent`
- `MembershipUsedEvent`
- `PaymentCompletedEvent`
- `PaymentCanceledEvent`

---

# 4. 로그 설계 원칙

## 4.1 구조화 로그를 기본으로 한다

운영 로그는 문장형 로그만 남기지 말고, 검색 가능한 키를 포함해야 한다.

최소 포함 필드:

- `timestamp`
- `level`
- `service`
- `module`
- `action`
- `result`
- `memberId`
- `studioId`
- `paymentId`
- `bookingId`
- `membershipId`
- `scheduleId`
- `orderId`
- `transmissionId`
- `idempotencyKey`
- `errorCode`
- `durationMs`

모든 필드를 항상 넣을 필요는 없지만, 해당 컨텍스트에서 존재하는 식별자는 최대한 로그에 남겨야 한다.

## 4.2 비즈니스 키와 기술 키를 함께 남긴다

예를 들어 결제 로그는 다음을 함께 남겨야 한다.

- 비즈니스 키: `memberId`, `studioId`, `paymentId`
- 외부 연동 키: `orderId`, `paymentKey`, `transmissionId`
- 기술 제어 키: `idempotencyKey`

이 조합이 있어야 운영 이슈와 외부 연동 이슈를 함께 추적할 수 있다.

## 4.3 성공 로그와 실패 로그를 모두 정의한다

분석은 실패 로그만으로는 충분하지 않다. 성공 모수도 있어야 비율 계산이 가능하다.

예:

- 결제 승인 요청 수
- 결제 승인 성공 수
- 결제 승인 실패 수
- 결제 승인 중복 요청 수

## 4.4 개인정보와 민감정보는 직접 남기지 않는다

로그에 남기지 말아야 할 항목:

- JWT 원문
- 결제 비밀키
- 민감한 개인식별정보
- 결제 원문 전체 중 불필요한 개인정보

`payment_webhook_log.payload`처럼 원문 보관이 필요한 경우에도 보존 기간과 접근 권한 기준이 필요하다.

---

# 5. 로그 설계 상세

## 5.1 API 접근 로그

현재 별도 API access log 표준이 없으므로 우선 다음 항목을 남기는 것이 좋다.

- `requestId`
- `method`
- `path`
- `status`
- `userId` 또는 `memberId`
- `clientIp`
- `durationMs`
- `exceptionClass`

우선 적용 대상 API:

- `/api/v1/bookings`
- `/api/v1/attendances`
- `/api/v1/payments`
- `/api/v1/payments/webhook`
- `/api/v1/auth/*`

## 5.2 결제 로그

현재 가장 잘 로그가 설계되어 있는 영역은 결제다. 이 영역은 분석의 핵심 축이 된다.

### 결제 승인 로그

남겨야 할 항목:

- `paymentId`
- `memberId`
- `studioId`
- `orderId`
- `paymentKey`
- `idempotencyKey`
- `operationType=APPROVE`
- `result`
- `errorCode`
- `durationMs`

### 환불 로그

남겨야 할 항목:

- `paymentId`
- `membershipId`
- `refundAmount`
- `canceledBookings`
- `idempotencyKey`
- `operationType=REFUND`
- `result`
- `errorCode`
- `durationMs`

### webhook 로그

현재 이미 좋은 추적 키가 있다.

- `provider`
- `transmissionId`
- `eventType`
- `orderId`
- `paymentId`
- `result`
- `errorCode`

여기에 추가로 다음이 있으면 더 좋다.

- `verified=true/false`
- `duplicated=true/false`
- `lockAcquired=true/false`
- `pgStatus`

## 5.3 예약/출석 로그

현재 결제에 비해 예약/출석은 로그 설계가 약하다. 다음 로그를 추가하는 것이 좋다.

### 예약 생성 로그

- `bookingId`
- `memberId`
- `scheduleId`
- `membershipId`
- `studioId`
- `result`
- `errorCode`
- `remainingCount`

### 예약 취소 로그

- `bookingId`
- `memberId`
- `scheduleId`
- `canceledAt`
- `result`

### no-show 배치 로그

현재 `processed` 건수만 남기고 있으므로 다음 보강이 필요하다.

- `batchStartedAt`
- `batchEndedAt`
- `processed`
- `affectedMembershipCount`
- `failedCount`

### 출석 처리 로그

- `attendanceId`
- `bookingId`
- `memberId`
- `membershipId`
- `remainingCountBefore`
- `remainingCountAfter`
- `result`

## 5.4 인증/보안 로그

현재 `JwtAuthenticationFilter`와 전역 예외 처리 로그가 있으므로 다음 항목을 기준화하면 좋다.

- `requestId`
- `path`
- `memberId`
- `authResult`
- `errorCode`
- `exceptionClass`

이 로그는 보안 이벤트보다 먼저 "잘못된 토큰 사용 증가"나 "클라이언트 인증 오류 급증"을 보는 데 유용하다.

## 5.5 알림 로그 및 읽음 데이터

`notification` 테이블은 이미 분석 가능한 행동 데이터다.

활용 가능한 지표:

- 알림 유형별 발송 건수
- 알림 유형별 읽음률
- 읽기까지 걸린 시간
- 회원별 미확인 알림 수

이를 위해 알림 생성 시점과 `readAt` 차이를 조회하는 집계가 필요하다.

---

# 6. KPI 설계

## 6.1 운영 KPI

### 회원/회원권

- 총 회원 수
- 활성 회원 수
- 활성 회원권 수
- 만료 예정 회원권 수
- 환불 회원권 수

### 예약/출석

- 예약 생성 수
- 예약 취소 수
- no-show 수
- 출석 수
- 예약 대비 출석 전환율
- 예약 대비 no-show 비율

### 결제

- 결제 생성 수
- 결제 승인 성공 수
- 결제 실패 수
- 환불 완료 수
- 결제 성공률
- 환불 비율

## 6.2 정합성 KPI

이 프로젝트에서 가장 중요한 KPI는 아래 항목들이다.

- 결제 완료 후 회원권 미생성 건수
- 환불 완료 후 회원권 미환불 건수
- 환불 완료 후 미래 예약 미취소 건수
- 출석 완료 후 회원권 차감 불일치 건수
- 중복 webhook 차단 건수
- 중복 결제 요청 차단 건수
- 예약 중복 생성 오류 건수

이 지표들은 "운영 사고 예방 KPI"로 봐야 한다.

## 6.3 시스템 KPI

- API 전체 성공률
- 주요 API 5xx 비율
- 주요 API 평균 응답 시간
- 주요 API P95 응답 시간
- 결제 승인 API 평균 응답 시간
- 환불 API 평균 응답 시간
- webhook 처리 실패율
- 락 획득 실패 건수
- 전역 예외 발생 건수

## 6.4 사용자 행동 KPI

- 신규 회원 가입 추이
- 회원권 구매 후 첫 예약까지 걸린 시간
- 예약 후 실제 출석 전환율
- 알림 유형별 읽음률
- 시간대별 예약 집중도
- 강사별 예약/출석 분포

---

# 7. 우선 관리해야 할 핵심 지표

현재 프로젝트 단계에서 모든 지표를 동일하게 보는 것은 비효율적이다. 우선순위는 다음과 같다.

## 1순위: 정합성/장애 예방

- 결제 완료 후 회원권 미생성 건수
- 환불 완료 후 회원권 미환불 건수
- 환불 완료 후 예약 미취소 건수
- webhook 중복 차단 건수
- 결제 멱등 처리 히트 건수
- 락 획득 실패 건수
- 5xx 오류 건수

## 2순위: 운영 성과

- 일별 결제 성공 수
- 일별 환불 수
- 일별 예약 수
- 일별 출석 수
- no-show 비율
- 활성 회원권 수

## 3순위: 사용자 행동 분석

- 알림 읽음률
- 시간대별 예약률
- 강사별 출석률
- 회원권 구매 후 예약 전환율

---

# 8. 대시보드 권장 구성

## 8.1 운영 대시보드

- 오늘 신규 회원 수
- 오늘 활성 회원권 수
- 오늘 예약 수
- 오늘 출석 수
- 오늘 no-show 수
- 오늘 결제 성공 수
- 오늘 환불 수

## 8.2 정합성 대시보드

- 회원권 미생성 결제 건수
- 회원권 미환불 건수
- 환불 후 미취소 예약 건수
- 중복 webhook 차단 건수
- 결제 멱등 처리 히트 건수
- 예약 중복 오류 건수

## 8.3 시스템 대시보드

- API 5xx 비율
- 주요 API P95 응답 시간
- 결제 API 실패율
- webhook 실패율
- 락 획득 실패 건수
- 인증 실패 건수

---

# 9. 단계별 도입 계획

## Phase 1. SQL 기반 리포트

가장 먼저 해야 할 일은 운영 DB 기반 집계 SQL을 만드는 것이다.

우선 구현 대상:

- 일별 예약/취소/no-show 집계
- 일별 결제/환불 집계
- 활성 회원권 집계
- 정합성 이상 징후 집계
- 알림 읽음률 집계

이 단계에서는 `analytics` API가 없어도 직접 조회 가능한 SQL과 관리자용 쿼리 문서가 중요하다.

## Phase 2. Analytics Query Repository 구현

현재 비어 있는 `AnalyticsQueryRepository`를 중심으로 다음 집계를 구현한다.

- 운영 KPI 조회
- 결제 KPI 조회
- 정합성 KPI 조회
- 사용자 행동 KPI 조회

## Phase 3. Analytics API 구현

추천 엔드포인트 예시:

- `GET /api/v1/analytics/overview`
- `GET /api/v1/analytics/payments`
- `GET /api/v1/analytics/bookings`
- `GET /api/v1/analytics/integrity`
- `GET /api/v1/analytics/notifications`

## Phase 4. 이벤트 기반 적재

이후에는 도메인 이벤트를 활용해 분석 적재를 분리한다.

- 예약 생성 이벤트 적재
- 예약 취소 이벤트 적재
- 회원권 사용 이벤트 적재
- 결제 완료/환불 이벤트 적재

이 단계부터는 운영 DB 집계와 별도 분석 적재 구조를 병행할 수 있다.

## Phase 5. 관측성 통합

향후 다음 도구와 연계 가능하다.

- Metabase
- Grafana
- Scouter
- ELK 또는 Loki
- Sentry

---

# 10. 구현 권장사항

## 10.1 requestId 도입

모든 요청과 후속 로그를 연결하기 위해 `requestId` 또는 `traceId`를 도입하는 것이 우선이다.

## 10.2 로그 포맷 표준화

최소한 다음 패턴은 팀 표준으로 정하는 것이 좋다.

- `module`
- `action`
- `result`
- `resourceId`
- `durationMs`
- `errorCode`

## 10.3 분석 SQL과 운영 로그를 분리해서 관리

분석 SQL은 문서 또는 쿼리 파일로 관리하고, 운영 로그는 애플리케이션에서 구조화해서 남겨야 한다.

## 10.4 정합성 지표는 배치 점검으로 보완

정합성 KPI는 단순 실시간 로그만으로는 부족하다. 하루 1회 또는 주기 배치로 이상 징후를 다시 점검하는 것이 좋다.

## 10.5 Analytics는 "숫자 조회"보다 "이상 징후 탐지"를 먼저 구현

현재 프로젝트 단계에서는 멋진 대시보드보다 다음이 더 중요하다.

- 잘못 처리된 결제 찾기
- 잘못 처리된 환불 찾기
- 예약/출석/회원권 불일치 찾기
- 반복 장애 징후 찾기

---

# 11. 결론

현재 LessonRing Backend는 분석 시스템이 완성된 상태는 아니지만, 이미 다음과 같은 강한 기반을 갖고 있다.

- 운영 핵심 테이블
- 결제 작업 이력 테이블
- webhook 로그 테이블
- 도메인 이벤트 구조
- 알림 및 읽음 데이터
- 예외 및 결제 처리 로그

따라서 현재 Analytics Plan의 핵심은 새 플랫폼을 크게 도입하는 것이 아니라, **기존 운영 데이터와 로그를 구조화하고 KPI로 연결하는 것**이다.

우선순위는 명확하다.

1. 정합성 지표 설계
2. 결제/예약/출석 로그 표준화
3. SQL 기반 운영 리포트
4. `analytics` 조회 API 구현
5. 이벤트 기반 분석 적재 확장

이 순서로 진행하면 LessonRing Backend는 단순 기능 제공 시스템을 넘어, 운영 상태를 숫자로 추적하고 문제를 조기에 발견할 수 있는 백엔드로 발전할 수 있다.
