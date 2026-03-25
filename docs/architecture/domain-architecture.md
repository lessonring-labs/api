# 도메인 아키텍처

LessonRing Backend의 도메인 구조, 경계, 상태 모델, 핵심 규칙을 현재 코드 기준으로 정리한 문서다.  
2026-03 기준 구현 상태를 반영하며, 설계 의도보다 실제 동작을 우선한다.

---

## 1. 도메인 개요

이 시스템의 핵심 도메인은 다음과 같다.

- `Studio`
- `Instructor`
- `Member`
- `Membership`
- `Schedule`
- `Booking`
- `Attendance`
- `Payment`
- `Notification`
- `Auth`

지원성/공통 도메인 또는 기반 요소:

- `RefreshToken`
- `PaymentOperation`
- `PaymentWebhookEvent`
- `PaymentWebhookLog`
- `Analytics`

전체 업무 흐름은 대체로 다음 순서를 따른다.

```text
Studio
  -> Instructor / Member / Schedule
  -> Booking
  -> Attendance
  -> Payment / Membership
  -> Notification
```

실제 구현에서는 엔티티 간 JPA 연관관계를 깊게 맺기보다 `id` 기반 참조를 많이 사용한다.  
즉, 도메인 관계는 분명하지만 저장 모델은 비교적 느슨하게 유지하고 있다.

---

## 2. Bounded Context

현재 코드를 기준으로 다음과 같이 경계를 나누는 것이 적절하다.

### 2.1 Identity Context

포함 모듈:

- `auth`
- `member`
- `refresh token`

책임:

- 로그인
- JWT 발급/검증
- 리프레시 토큰 저장/재발급
- 현재 사용자 식별
- 회원 기본 정보 관리

### 2.2 Studio Operation Context

포함 모듈:

- `studio`
- `instructor`
- `schedule`

책임:

- 스튜디오 운영 단위 관리
- 강사 조회
- 수업 일정 생성/조회
- 예약 가능 상태 판단의 기준 제공

### 2.3 Booking Context

포함 모듈:

- `booking`
- `attendance`

책임:

- 예약 생성/취소
- 예약 중복 방지
- no-show 처리
- 출석 생성/취소
- 예약 상태 전이 관리

### 2.4 Membership Context

포함 모듈:

- `membership`

책임:

- 이용권 생성
- 기간/횟수 기반 사용 가능 여부 판단
- 이용권 차감/복구
- 환불 상태 관리

### 2.5 Payment Context

포함 모듈:

- `payment`
- `payment operation`
- `payment webhook`

책임:

- 결제 생성
- PG 승인
- 환불
- 웹훅 중복 방지 및 상태 동기화
- 결제 멱등 처리

### 2.6 Notification Context

포함 모듈:

- `notification`
- `common.event.handler`

책임:

- 도메인 이벤트 기반 알림 생성
- 읽음 처리
- 회원별 알림 조회

### 2.7 Analytics Context

포함 모듈:

- `analytics`

책임:

- 분석 API 확장 지점

현재는 컨트롤러와 서비스 골격은 있으나 실제 분석 응답은 아직 TODO 상태다.

---

## 3. 핵심 도메인 관계

현재 모델을 관계 중심으로 요약하면 다음과 같다.

### 3.1 Studio 중심 관계

- `Studio 1 : N Instructor`
- `Studio 1 : N Member`
- `Studio 1 : N Schedule`
- `Studio 1 : N Booking`
- `Studio 1 : N Membership`
- `Studio 1 : N Payment`
- `Studio 1 : N Notification`

`studioId`는 대부분의 주요 엔티티에 포함되며, 멀티 스튜디오 확장을 고려한 경계 역할을 한다.

### 3.2 Member 중심 관계

- `Member 1 : N Membership`
- `Member 1 : N Booking`
- `Member 1 : N Payment`
- `Member 1 : N Notification`

회원은 예약과 결제의 중심 주체다.

### 3.3 Schedule 중심 관계

- `Instructor 1 : N Schedule`
- `Schedule 1 : N Booking`
- `Schedule 1 : N Attendance(간접)`

예약은 일정에 종속되고, 출석은 예약을 통해 일정과 연결된다.

### 3.4 Booking 중심 관계

- `Booking N : 1 Member`
- `Booking N : 1 Schedule`
- `Booking N : 1 Membership`
- `Booking 1 : 0..1 Attendance`

### 3.5 Payment 중심 관계

- `Payment N : 1 Member`
- `Payment 1 : 0..1 Membership`
- `Payment 1 : N PaymentOperation`
- `PaymentWebhookEvent / PaymentWebhookLog`는 결제 처리 추적 보조 모델

결제가 완료되면 이용권이 생성되고 `membershipId`가 결제에 연결된다.

---

## 4. Aggregate 관점 정리

엄격한 DDD aggregate를 코드에 완전히 강제하고 있지는 않지만, 현재 구조를 aggregate 관점으로 해석하면 다음이 자연스럽다.

### 4.1 Member Aggregate

Root:

- `Member`

연관 하위 개념:

- `Membership`
- `Booking`
- `Payment`
- `Notification`

### 4.2 Schedule Aggregate

Root:

- `Schedule`

연관 하위 개념:

- `Booking`
- `Attendance`

### 4.3 Booking Aggregate

Root:

- `Booking`

연관 하위 개념:

- 예약 상태
- 출석 연결 여부
- 취소 사유

### 4.4 Membership Aggregate

Root:

- `Membership`

연관 하위 개념:

- 사용 가능 여부
- 잔여 횟수
- 기간 유효성
- 환불 상태

### 4.5 Payment Aggregate

Root:

- `Payment`

연관 하위 개념:

- `PaymentOperation`
- `PaymentWebhookEvent`
- `PaymentWebhookLog`

결제 aggregate는 단순 결제 엔티티를 넘어 승인/환불/웹훅/멱등 처리까지 포함하는 운영형 aggregate로 보는 것이 적절하다.

---

## 5. 도메인별 책임

### 5.1 Studio

역할:

- 스튜디오 운영의 최상위 범위
- 다른 업무 데이터의 소속 단위

현재 구현 수준:

- 조회 중심

### 5.2 Instructor

역할:

- 수업 진행 주체
- 일정 배정 기준

현재 구현 수준:

- 조회 중심

### 5.3 Member

역할:

- 서비스를 이용하는 회원

책임:

- 회원 등록/조회
- 회원 상태 관리
- 인증 주체 연결

### 5.4 Membership

역할:

- 회원이 보유한 이용권

책임:

- 기간형/횟수형 구분
- 잔여 횟수 차감 및 복원
- 사용 가능 여부 검증
- 만료/소진/정지/환불 상태 관리

### 5.5 Schedule

역할:

- 실제 운영되는 수업 일정

책임:

- 일정 생성/조회
- 정원(capacity) 관리
- 예약 가능 여부 판단 기준 제공

### 5.6 Booking

역할:

- 회원의 일정 예약 기록

책임:

- 예약 생성/취소
- 중복 예약 방지
- no-show 상태 전이
- 출석과의 연결 기준 제공

### 5.7 Attendance

역할:

- 예약의 실제 출석 처리 결과

책임:

- 출석 생성
- 출석 취소 시 예약/이용권 복구
- 이용권 차감 트리거

### 5.8 Payment

역할:

- 결제 및 환불 처리의 기준 엔티티

책임:

- 결제 생성
- PG 승인 반영
- 웹훅 기반 상태 동기화
- 환불 및 미래 예약 정리
- 이용권 생성 연결

### 5.9 Notification

역할:

- 회원에게 보여줄 시스템 알림

책임:

- 예약/결제/이용권 이벤트 기반 알림 생성
- 읽음 처리

---

## 6. 상태 모델

현재 코드에 존재하는 주요 상태값은 다음과 같다.

### 6.1 `ScheduleStatus`

- `OPEN`
- `CLOSED`
- `CANCELED`

의미:

- `OPEN`: 예약 가능
- `CLOSED`: 예약 불가
- `CANCELED`: 일정 취소

### 6.2 `BookingStatus`

- `RESERVED`
- `ATTENDED`
- `CANCELED`
- `NO_SHOW`

의미:

- `RESERVED`: 정상 예약
- `ATTENDED`: 출석 완료
- `CANCELED`: 예약 취소
- `NO_SHOW`: 미출석 확정

### 6.3 `MembershipStatus`

- `ACTIVE`
- `USED_UP`
- `EXPIRED`
- `SUSPENDED`
- `REFUNDED`

의미:

- `ACTIVE`: 사용 가능
- `USED_UP`: 횟수 소진
- `EXPIRED`: 기간 만료
- `SUSPENDED`: 일시 정지
- `REFUNDED`: 환불 완료

기존 문서에 없던 `REFUNDED` 상태는 현재 구현에 포함되어 있으므로 반드시 반영해야 한다.

### 6.4 `AttendanceStatus`

- `ATTENDED`
- `ABSENT`
- `CANCELED`

현재 서비스 로직상 출석 생성은 주로 `ATTENDED`를 사용한다.

### 6.5 `PaymentStatus`

- `READY`
- `COMPLETED`
- `CANCELED`
- `REFUNDED`
- `FAILED`

주의:

- 코드상 환불 로직은 `Payment.cancelWithPg(...)`를 호출해 상태를 `CANCELED`로 전환한다.
- 즉 enum에 `REFUNDED`가 존재하지만, 현재 환불 완료 결과를 항상 `REFUNDED` 상태로 저장하는 구조는 아니다.
- 문서와 구현이 어긋나지 않도록 “환불 기능 존재”와 “최종 상태 저장 방식”을 구분해서 이해해야 한다.

### 6.6 `PaymentOperationStatus`

- `PROCESSING`
- `SUCCEEDED`
- `FAILED`

### 6.7 `PaymentWebhookEventStatus`

- `RECEIVED`
- `PROCESSING`
- `SUCCEEDED`
- `FAILED`

---

## 7. 핵심 비즈니스 규칙

### 7.1 예약 생성 규칙

`BookingService.createWithLock` 기준 규칙:

- 회원이 존재해야 한다.
- 일정이 존재해야 한다.
- 이용권이 존재해야 한다.
- 이용권의 소유 회원과 요청 회원이 같아야 한다.
- 이용권의 스튜디오와 요청 스튜디오가 같아야 한다.
- 이용권이 사용 가능 상태여야 한다.
- 일정 상태가 `OPEN`이어야 한다.
- 일정 시작 시간이 현재보다 미래여야 한다.
- 예약 수가 정원을 초과하면 안 된다.
- 같은 회원이 같은 일정에 활성 예약을 중복 생성할 수 없다.

예약 성공 시:

- `Schedule.bookedCount` 증가
- `BookingCreatedEvent` 발행

### 7.2 예약 취소 규칙

`BookingService.cancelWithLock` 기준 규칙:

- 이미 취소된 예약은 다시 취소할 수 없다.
- 취소는 `RESERVED` 상태에서만 가능하다.

취소 성공 시:

- `BookingStatus.CANCELED`
- 일정 예약 수 감소
- `BookingCanceledEvent` 발행

### 7.3 no-show 규칙

`BookingService.markNoShow` 기준 규칙:

- 예약 상태가 `RESERVED`여야 한다.
- 이미 출석이 생성된 예약은 no-show 처리할 수 없다.
- 일정 종료 시각이 현재보다 이전이어야 한다.

횟수형 이용권의 경우:

- no-show 처리 시 1회 차감
- `MembershipUsedEvent` 발행

### 7.4 출석 처리 규칙

`AttendanceService.create` 기준 규칙:

- 예약이 존재해야 한다.
- 취소/출석완료/no-show 예약은 출석 생성할 수 없다.
- 동일 예약에 대해 중복 출석을 생성할 수 없다.
- 연결된 이용권이 존재해야 한다.
- 이용권은 사용 가능해야 한다.

출석 성공 시:

- 이용권 차감
- 예약 상태 `ATTENDED`
- 출석 엔티티 생성
- `MembershipUsedEvent` 발행

### 7.5 출석 취소 규칙

`AttendanceService.cancel` 기준 규칙:

- 예약 상태가 `ATTENDED`여야 한다.

취소 성공 시:

- 예약 상태를 `RESERVED`로 복구
- 횟수형 이용권 잔여 횟수 복구
- 출석 엔티티 삭제

### 7.6 이용권 사용 규칙

`Membership` 기준 규칙:

- 상태가 `ACTIVE`여야 한다.
- 오늘 날짜가 이용 가능 기간 안에 있어야 한다.
- 횟수형이면 `remainingCount > 0` 이어야 한다.
- 환불된 이용권은 사용할 수 없다.

횟수형 이용권은 사용 시 잔여 횟수를 차감하며, 0이 되면 `USED_UP`으로 전이한다.

### 7.7 결제 승인 규칙

`PaymentPgService.approve` 기준 규칙:

- 결제 상태가 `READY`여야 한다.
- 요청 금액과 내부 결제 금액이 일치해야 한다.
- 동일 idempotency key 조합은 중복 처리되지 않는다.
- 결제 단위 Redis 락을 획득해야 한다.

승인 성공 시:

- PG 승인 성공 응답 저장
- 이용권 생성
- 결제 상태 `COMPLETED`
- `PaymentCompletedEvent` 발행
- `PaymentOperation` 성공 기록 저장

### 7.8 환불 규칙

`PaymentService.refund` 기준 규칙:

- idempotency key가 필수다.
- 결제 상태가 `COMPLETED`여야 한다.
- 연결된 이용권이 존재해야 한다.
- 이미 환불된 이용권은 재환불할 수 없다.
- 결제 단위 Redis 락을 획득해야 한다.

환불 성공 시:

- PG 취소 요청 수행
- 아직 시작되지 않은 미래 예약 취소
- 이용권 `REFUNDED`
- 결제 상태 변경
- `PaymentCanceledEvent` 발행
- `PaymentOperation` 성공 기록 저장

### 7.9 웹훅 처리 규칙

`PaymentWebhookService.handle` 기준 규칙:

- `orderId`, `eventType`는 필수다.
- `transmissionId`가 중복이면 무시한다.
- 내부 결제를 `pgOrderId`로 찾아야 한다.
- 결제 단위 Redis 락으로 동시 처리 충돌을 방지한다.
- PG 상태 재검증 후 내부 상태를 갱신한다.

지원 이벤트:

- `PAYMENT_COMPLETED`
- `PAYMENT_FAILED`
- `PAYMENT_CANCELED`

---

## 8. 도메인 이벤트

현재 실제 발행/소비되는 주요 도메인 이벤트는 다음과 같다.

- `BookingCreatedEvent`
- `BookingCanceledEvent`
- `MembershipUsedEvent`
- `PaymentCompletedEvent`
- `PaymentCanceledEvent`

현재 대표 소비 흐름:

```text
Domain Service
  -> DomainEventPublisher
  -> NotificationEventHandler
  -> Notification 저장
```

즉, 알림은 별도 메시지 브로커가 아니라 현재는 **동일 애플리케이션 내부 이벤트 처리**로 구현되어 있다.

---

## 9. 현재 구현 수준 평가

현재 구현 완성도가 높은 영역:

- `auth`
- `member`
- `membership`
- `schedule`
- `booking`
- `attendance`
- `payment`
- `notification`

부분 구현 또는 확장 중인 영역:

- `analytics`
- 외부 메시징/비동기 파이프라인
- 스튜디오/강사의 쓰기 유스케이스 고도화

기존 문서에서 “예정”으로 표현되던 결제 승인, 환불, 웹훅, 분산 락, 멱등 처리 등은 현재 이미 구현되어 있다.

---

## 10. 설계 원칙

현재 코드베이스에서 읽히는 도메인 설계 원칙은 다음과 같다.

- 도메인별 패키지로 모듈을 나눈다.
- Controller는 HTTP 진입점에 집중한다.
- Application Service가 유스케이스와 트랜잭션을 조합한다.
- 상태 전이는 엔티티 메서드로 제한한다.
- 공통 예외는 `BusinessException`과 `ErrorCode`를 사용한다.
- 공통 응답은 `ApiResponse`를 사용한다.
- 강한 JPA 그래프보다 ID 기반 참조를 선호한다.
- 동시성 충돌 가능성이 큰 예약/결제는 Redis 락으로 보강한다.
- 결제 외부 연동은 멱등성과 처리 이력을 별도 모델로 관리한다.

---

## 11. 요약

LessonRing의 도메인 아키텍처는 예약, 출석, 이용권, 결제를 중심으로 맞물린다.

핵심 연결은 다음과 같다.

- 회원은 이용권을 보유한다.
- 이용권은 예약과 출석 가능 여부를 결정한다.
- 일정은 예약 가능 수량을 통제한다.
- 결제는 이용권 생성과 환불 흐름의 시작점이다.
- 도메인 이벤트는 알림 생성으로 이어진다.

현재 시스템은 단순 CRUD 단계가 아니라, **상태 전이 규칙과 운영 안정성 제어가 포함된 도메인 모델**로 발전한 상태다.
