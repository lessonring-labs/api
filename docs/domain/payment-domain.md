# Payment Domain

`Payment` 도메인은 LessonRing에서 **회원의 결제 기록을 관리하는 도메인**이다.

이용권(Membership)은 결제를 통해 생성된다.

---

# 1. Domain Role

Payment의 주요 책임

```text
결제 기록 생성
결제 상태 관리
이용권 생성 트리거
```

---

# 2. Entity Structure

```text
Payment
- id
- studioId
- memberId
- amount
- method
- status
- paidAt
```

---

# 3. Enum

## PaymentMethod

```text
CARD
CASH
TRANSFER
```

## PaymentStatus

```text
PAID
REFUNDED
CANCELED
```

---

# 4. Relationship

```text
Member 1 : N Payment
Payment 1 : 1 Membership
```

---

# 5. Business Rules

## 결제 완료

```text
status = PAID
```

결제 완료 시

```text
Membership 생성
```

---

# 6. Summary

```text
Payment은 회원의 결제 기록을 관리하는 도메인이다.
결제가 완료되면 Membership이 생성된다.
```