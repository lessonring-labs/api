# Domain Event Flow

이 문서는 LessonRing Backend의 **도메인 이벤트 흐름**을 정의한다.

`event-flow.md`가 시스템 전체 동작 흐름을 설명하는 문서라면,  
이 문서는 **이벤트 단위로 Producer / Consumer / 후속 처리**를 설명하는 문서다.

---

# 1. 목적

LessonRing은 Event Driven Architecture를 일부 도입하는 구조를 가진다.

이 문서의 목적

```text
도메인 이벤트 정의
이벤트 발생 주체(Producer) 정의
이벤트 소비 주체(Consumer) 정의
후속 처리 흐름 명시
```

---

# 2. 기본 원칙

## 2.1 이벤트는 과거형으로 정의한다

예

```text
BookingCreatedEvent
BookingCanceledEvent
PaymentCompletedEvent
AttendanceRecordedEvent
MembershipUsedEvent
```

---

## 2.2 이벤트는 도메인 사실을 표현한다

이벤트는 "무엇을 할지"가 아니라  
"무엇이 일어났는지"를 표현한다.

잘못된 예

```text
SendNotificationEvent
CreateMembershipEvent
```

올바른 예

```text
BookingCreatedEvent
PaymentCompletedEvent
```

---

## 2.3 이벤트는 느슨한 결합을 위한 수단이다

이벤트 발행자는 소비자를 알지 않는다.

예

```text
BookingService
 → BookingCreatedEvent 발행
```

이후

```text
NotificationService
AnalyticsService
```

가 이벤트를 소비할 수 있다.

---

# 3. 핵심 도메인 이벤트

## 3.1 BookingCreatedEvent

설명

```text
회원이 수업 예약을 생성했을 때 발생하는 이벤트
```

Producer

```text
BookingService
```

Possible Consumers

```text
NotificationService
AnalyticsService
```

후속 처리 예

```text
예약 완료 알림 발송
예약 통계 집계
```

---

## 3.2 BookingCanceledEvent

설명

```text
회원이 예약을 취소했을 때 발생하는 이벤트
```

Producer

```text
BookingService
```

Possible Consumers

```text
NotificationService
AnalyticsService
MembershipService
```

후속 처리 예

```text
예약 취소 알림 발송
취소 통계 집계
이용권 복구 정책 검토
```

---

## 3.3 PaymentCompletedEvent

설명

```text
결제가 정상 완료되었을 때 발생하는 이벤트
```

Producer

```text
PaymentService
```

Possible Consumers

```text
MembershipService
NotificationService
AnalyticsService
```

후속 처리 예

```text
이용권 생성
결제 완료 알림
매출 통계 집계
```

---

## 3.4 AttendanceRecordedEvent

설명

```text
출석 기록이 생성되었을 때 발생하는 이벤트
```

Producer

```text
AttendanceService
```

Possible Consumers

```text
MembershipService
AnalyticsService
```

후속 처리 예

```text
이용권 차감
출석 통계 집계
```

---

## 3.5 MembershipUsedEvent

설명

```text
이용권 사용이 반영되었을 때 발생하는 이벤트
```

Producer

```text
MembershipService
```

Possible Consumers

```text
AnalyticsService
NotificationService
```

후속 처리 예

```text
잔여 이용권 통계 집계
잔여 횟수 부족 알림
```

---

# 4. 이벤트 흐름 예시

## 4.1 예약 생성 흐름

```text
Member
 → BookingService
   → BookingCreatedEvent 발행
      → NotificationService
      → AnalyticsService
```

설명

```text
예약 생성 후 알림과 통계 처리를 분리한다.
```

---

## 4.2 결제 완료 흐름

```text
Member
 → PaymentService
   → PaymentCompletedEvent 발행
      → MembershipService
      → NotificationService
      → AnalyticsService
```

설명

```text
결제 완료 후 이용권 생성과 알림, 통계 집계를 분리한다.
```

---

## 4.3 출석 처리 흐름

```text
AttendanceService
 → AttendanceRecordedEvent 발행
    → MembershipService
    → AnalyticsService
```

설명

```text
출석 기록 이후 이용권 차감과 통계 처리를 분리한다.
```

---

# 5. 이벤트와 도메인 관계

```text
Booking
 ├─ BookingCreatedEvent
 └─ BookingCanceledEvent

Payment
 └─ PaymentCompletedEvent

Attendance
 └─ AttendanceRecordedEvent

Membership
 └─ MembershipUsedEvent
```

---

# 6. 현재 구현 방향

현재 프로젝트에서 우선 적용 대상 이벤트

```text
BookingCreatedEvent
BookingCanceledEvent
PaymentCompletedEvent
AttendanceRecordedEvent
```

초기에는 Spring Application Event 또는 내부 Publisher로 시작할 수 있다.

예상 확장 방향

```text
1단계  → Spring Application Event
2단계  → Kafka 연동
3단계  → Outbox Pattern 도입 검토
```

---

# 7. 구현 원칙

## 7.1 이벤트는 application layer에서 발행한다

예

```text
BookingService
PaymentService
AttendanceService
```

---

## 7.2 이벤트 객체는 domain/event 패키지에 둔다

예

```text
booking/domain/event/BookingCreatedEvent.java
booking/domain/event/BookingCanceledEvent.java
```

공통 인터페이스는 common/event에 둔다.

예

```text
common/event/DomainEvent.java
common/event/DomainEventPublisher.java
```

---

## 7.3 이벤트 소비 로직은 별도 서비스로 분리한다

예

```text
NotificationEventHandler
AnalyticsEventHandler
MembershipEventHandler
```

---

# 8. 문서 간 관계

이 문서는 아래 문서와 함께 본다.

```text
docs/architecture/event-flow.md
docs/architecture/api-architecture.md
docs/domain/booking_domain.md
docs/domain/membership_domain.md
docs/domain/payment_domain.md
docs/domain/attendance_domain.md
```

---

# 9. Summary

```text
domain-event-flow.md는 LessonRing의 Event Driven Architecture 기준으로
도메인 이벤트의 Producer, Consumer, 후속 처리 흐름을 정의하는 문서다.

이 문서는 시스템 전체 흐름 문서인 event-flow.md와는 다르게
이벤트 자체를 중심으로 설계를 설명한다.
```