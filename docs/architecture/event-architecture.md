# Event Architecture

LessonRing Backend의 이벤트 기반 아키텍처(Event Driven Architecture) 설계 원칙을 정의한다.

이 문서는 다음 목적을 가진다.

- 도메인 이벤트 구조 정의
- 도메인 간 결합도 감소
- 후속 처리 로직 분리
- 비동기 확장 기반 제공
- Notification / Analytics / Integration 확장 기반 마련

---

# 1. Event Architecture Overview

LessonRing Backend는 현재 도메인 중심 구조를 사용하며  
확장성을 고려하여 Event Driven Architecture를 함께 설계한다.

현재 기본 구조

Controller
↓
Application Service
↓
Domain
↓
Repository
↓
Database

이벤트 확장 구조

Application Service
↓
Domain Event 발행
↓
Event Handler
↓
후속 처리

이벤트 기반 구조 목적

- 도메인 간 결합 감소
- 후속 처리 로직 분리
- 비동기 처리 가능
- Notification 확장
- Analytics 확장
- 외부 시스템 연동

---

# 2. Domain Event 정의

Domain Event란 도메인 상태 변화가 발생했음을 표현하는 객체이다.

예시

예약 생성  
예약 취소  
출석 처리  
결제 완료  
이용권 생성

좋은 이벤트 이름

BookingCreatedEvent  
BookingCanceledEvent  
AttendanceMarkedEvent  
PaymentCompletedEvent

나쁜 예

CreateBookingEvent  
DoPaymentEvent

이벤트는 "무엇이 발생했는지"를 표현해야 한다.

---

# 3. Event 설계 원칙

LessonRing Event 설계 원칙

1 이벤트 이름은 과거형 사용  
2 이벤트는 상태 변화를 표현  
3 이벤트는 비즈니스 의미 중심  
4 이벤트는 읽기 전용 데이터 객체  
5 이벤트는 도메인 로직 포함 금지

---

# 4. Core Domain Events

LessonRing 핵심 이벤트

BookingCreatedEvent  
BookingCanceledEvent

AttendanceMarkedEvent  
AttendanceCanceledEvent

MembershipCreatedEvent  
MembershipUsedEvent  
MembershipExpiredEvent

PaymentCompletedEvent  
PaymentCanceledEvent

---

# 5. Event Flow

예약 생성

BookingService  
→ Booking 생성  
→ BookingCreatedEvent 발행

후속 처리

NotificationHandler  
AnalyticsHandler

---

예약 취소

BookingService  
→ Booking 취소  
→ BookingCanceledEvent 발행

후속 처리

NotificationHandler  
MembershipRestoreHandler

---

출석 처리

AttendanceService  
→ Attendance 생성  
→ Booking 상태 변경  
→ Membership 차감  
→ AttendanceMarkedEvent 발행

후속 처리

AnalyticsHandler

---

결제 완료

PaymentService  
→ Payment 완료  
→ PaymentCompletedEvent 발행

후속 처리

MembershipCreateHandler  
NotificationHandler

---

# 6. Event Publisher

이벤트는 일반적으로 Application Service에서 발행한다.

예시

BookingService  
AttendanceService  
PaymentService  
MembershipService

예시 코드

applicationEventPublisher.publishEvent(
new BookingCreatedEvent(
bookingId,
memberId,
scheduleId,
membershipId,
occurredAt
)
);

---

# 7. Event Handler

Event Handler는 이벤트를 수신하여 후속 처리를 수행한다.

예시

NotificationEventHandler  
AnalyticsEventHandler  
MembershipEventHandler

예시 코드

@Component
public class BookingEventHandler {

    @EventListener
    public void handle(BookingCreatedEvent event) {
        // 예약 생성 후 알림 처리
    }
}

---

# 8. Transaction Event 처리

이벤트는 트랜잭션 이후 실행하는 것이 안전하다.

권장 방식

@TransactionalEventListener

예시

@TransactionalEventListener(phase = AFTER_COMMIT)
public void handle(BookingCreatedEvent event) {
// 트랜잭션 커밋 이후 처리
}

이유

- 롤백 데이터 처리 방지
- 정합성 유지

---

# 9. Sync Event vs Async Event

Sync Event

- 같은 Thread 실행
- 구현 단순
- 디버깅 용이

단점

- 처리 시간 증가

---

Async Event

방법

@Async  
Message Queue  
Kafka

장점

- 응답 속도 향상
- 서비스 분리

단점

- 재시도
- 중복 처리
- 순서 보장 고려 필요

---

# 10. Event Payload Rule

Event Payload에는 최소 정보만 포함한다.

예시

BookingCreatedEvent

bookingId  
memberId  
scheduleId  
membershipId  
occurredAt

포함하지 않는 것

Entity 전체 객체  
Repository  
Service  
외부 API Client

---

# 11. Event Package Structure

권장 구조

common
└ event
├ domain
├ publisher
├ handler

예시

common/event/domain/BookingCreatedEvent  
common/event/handler/BookingEventHandler

또는 도메인별 구조

booking/domain/event  
membership/domain/event  
attendance/domain/event

---

# 12. Event Reliability 고려사항

이벤트 구조 도입 시 고려 사항

중복 이벤트 처리  
멱등성(idempotency)  
재시도 정책  
이벤트 순서 보장  
이벤트 유실 방지

---

# 13. Outbox Pattern (향후)

향후 Message Broker 도입 시 Outbox Pattern 적용 가능

구조

Domain 변경  
→ Outbox Table 저장  
→ Event Publisher  
→ Message Queue

목적

- 이벤트 유실 방지
- 트랜잭션 정합성 확보

---

# 14. LessonRing Event Use Cases

이벤트 기반으로 분리하기 좋은 기능

예약 알림  
예약 취소 알림  
결제 완료 알림  
출석 통계 처리  
회원 활동 로그  
외부 Webhook

---

# 15. Recommended Initial Events

초기 도입 추천 이벤트

BookingCreatedEvent  
BookingCanceledEvent  
AttendanceMarkedEvent  
PaymentCompletedEvent

---

# 16. Summary

LessonRing Backend는 Domain 중심 구조 위에  
Event 기반 확장 구조를 설계한다.

핵심 이벤트

BookingCreatedEvent  
BookingCanceledEvent  
AttendanceMarkedEvent  
PaymentCompletedEvent  
MembershipCreatedEvent

이 구조는 향후

Notification  
Analytics  
External Integration  
Message Queue

확장을 위한 기반이 된다.