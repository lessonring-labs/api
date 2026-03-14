# Attendance Domain

`Attendance` 도메인은 **회원의 수업 출석 기록을 관리하는 도메인**이다.

예약(Booking)을 기반으로 출석이 기록된다.

---

# 1. Domain Role

Attendance의 주요 책임

```text
출석 기록 생성
출석 상태 관리
이용권 차감 처리
```

---

# 2. Entity Structure

```text
Attendance
- id
- studioId
- memberId
- bookingId
- scheduleId
- status
- attendedAt
```

---

# 3. Enum

```text
ATTENDED
ABSENT
CANCELED
```

---

# 4. Relationship

```text
Booking 1 : 1 Attendance
Member 1 : N Attendance
Schedule 1 : N Attendance
```

---

# 5. Business Rules

## 출석 처리

수업 종료 후

```text
Attendance 생성
Booking.status = ATTENDED
Membership.remainCount -1
```

---

# 6. Summary

```text
Attendance는 회원의 실제 수업 출석 기록을 관리한다.
```