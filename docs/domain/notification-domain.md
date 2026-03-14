# Notification Domain

`Notification` 도메인은 LessonRing에서 **회원 또는 관리자에게 전달되는 알림을 관리하는 도메인**이다.

---

# 1. Domain Role

Notification의 주요 책임

```text
알림 생성
알림 조회
알림 읽음 처리
```

---

# 2. Entity Structure

```text
Notification
- id
- studioId
- memberId
- title
- content
- type
- readAt
```

---

# 3. Notification Type

```text
BOOKING_CREATED
BOOKING_CANCELED
CLASS_REMINDER
PAYMENT_COMPLETED
```

---

# 4. Relationship

```text
Member 1 : N Notification
```

---

# 5. Business Rules

## 읽음 처리

```text
readAt != null
```

이면 읽음 상태이다.

---

# 6. Notification Trigger

알림 생성 시점

```text
예약 생성
예약 취소
수업 시작 알림
결제 완료
```

---

# 7. Summary

```text
Notification은 시스템 이벤트 발생 시 사용자에게 전달되는 알림을 관리한다.
```