# Booking Domain

`Booking` 도메인은 LessonRing에서 **회원의 수업 예약을 관리하는 도메인**이다.

회원(Member)은 이용권(Membership)을 사용하여 특정 수업(Schedule)을 예약할 수 있다.

---

# 1. Domain Role

Booking의 주요 책임

```text
수업 예약 생성
예약 상태 관리
예약 취소 처리
예약과 출석 연결
이용권 사용 대상 지정
```

한 줄 정의

```text
Booking은 회원이 특정 수업을 예약한 기록을 관리하는 도메인이다.
```

---

# 2. Entity Structure

## Booking

```text
Booking
- id : Long
- studioId : Long
- memberId : Long
- membershipId : Long
- scheduleId : Long
- status : BookingStatus
- reservedAt : LocalDateTime
- canceledAt : LocalDateTime
```

설명

```text
studioId      → 스튜디오 ID
memberId      → 회원 ID
membershipId  → 이용권 ID
scheduleId    → 수업 ID
status        → 예약 상태
reservedAt    → 예약 생성 시점
canceledAt    → 예약 취소 시점
```

---

# 3. BaseEntity

Booking은 BaseEntity를 상속한다.

```text
BaseEntity
- createdAt
- createdBy
- updatedAt
- updatedBy
```

---

# 4. Enum

## BookingStatus

```text
RESERVED
CANCELED
ATTENDED
NO_SHOW
```

설명

```text
RESERVED → 예약 완료
CANCELED → 예약 취소
ATTENDED → 출석 완료
NO_SHOW  → 예약 후 미출석
```

---

# 5. Relationship

Booking은 여러 도메인과 연결된다.

```text
Member
Membership
Schedule
Attendance
```

관계 구조

```text
Member 1 : N Booking
Membership 1 : N Booking
Schedule 1 : N Booking
Booking 1 : 1 Attendance
```

---

# 6. Business Rules

## 6.1 예약은 Member 기준으로 생성된다

```text
memberId는 필수 값이다.
```

예약의 주체는 회원이다.

---

## 6.2 예약은 특정 Schedule에 대해 생성된다

```text
scheduleId는 필수 값이다.
```

Schedule은 실제 수업이다.

예

```text
2026-04-01 19:00
필라테스 그룹 레슨
```

---

## 6.3 예약은 Membership을 사용한다

```text
membershipId는 필수 값이다.
```

예약 시 검증

```text
Membership.status == ACTIVE
Membership.remainCount > 0
Membership.endDate >= today
```

---

## 6.4 예약 취소 처리

예약 취소 시

```text
status = CANCELED
canceledAt 기록
```

정책에 따라

```text
이용권 복구 여부 결정
```

---

## 6.5 출석 처리

수업이 끝난 후

```text
Booking → Attendance 생성
```

상태 변경

```text
status = ATTENDED
```

---

# 7. Layered Structure

Booking 도메인 구조

```text
booking
├─ api
│  ├─ BookingController.java
│  ├─ request
│  │  └─ BookingCreateRequest.java
│  └─ response
│     └─ BookingResponse.java
│
├─ application
│  └─ BookingService.java
│
├─ domain
│  ├─ Booking.java
│  ├─ BookingStatus.java
│  └─ repository
│     └─ BookingRepository.java
│
└─ infrastructure
   └─ persistence
      ├─ BookingJpaRepository.java
      └─ BookingRepositoryImpl.java
```

---

# 8. Request / Response

## BookingCreateRequest

```text
studioId
memberId
membershipId
scheduleId
```

---

## BookingResponse

```text
id
studioId
memberId
membershipId
scheduleId
status
reservedAt
canceledAt
```

---

# 9. Service Responsibilities

BookingService의 책임

```text
예약 생성
예약 조회
예약 취소
예약 상태 변경
```

---

# 10. Booking Validation

예약 생성 시 검증

```text
회원 존재 여부 확인
이용권 상태 확인
이용권 잔여 횟수 확인
이용권 기간 확인
수업 정원 확인
```

---

# 11. Current Implementation Scope

현재 구현 범위

```text
예약 생성
예약 조회
```

---

# 12. Future Expansion

향후 추가 가능 기능

```text
예약 변경
예약 대기 리스트
예약 자동 취소
예약 제한 정책
예약 알림
```

---

# 13. Domain Flow

Booking을 중심으로 전체 흐름

```text
Member
 → Membership
   → Booking
     → Attendance
       → Payment
```

설명

```text
Member      → 회원
Membership  → 이용권
Booking     → 예약
Attendance  → 출석
Payment     → 결제
```

---

# 14. Summary

```text
Booking은 회원이 특정 수업을 예약한 기록을 관리하는 도메인이다.

예약은 Membership을 사용하여 생성되며
수업 이후 Attendance와 연결된다.
```