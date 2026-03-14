# Schedule Domain

`Schedule` 도메인은 LessonRing에서 **실제 수업 시간표를 관리하는 도메인**이다.

회원은 이 Schedule을 기준으로 예약(Booking)을 생성한다.

---

# 1. Domain Role

Schedule의 주요 책임

```text
수업 시간표 관리
수업 정원 관리
예약 대상 수업 제공
```

한 줄 정의

```text
Schedule은 스튜디오의 실제 수업 시간을 관리하는 도메인이다.
```

---

# 2. Entity Structure

```text
Schedule
- id
- studioId
- instructorId
- name
- startTime
- endTime
- capacity
- reservedCount
```

설명

```text
studioId       → 스튜디오
instructorId   → 강사
name           → 수업 이름
startTime      → 시작 시간
endTime        → 종료 시간
capacity       → 정원
reservedCount  → 현재 예약 수
```

---

# 3. Relationship

```text
Instructor 1 : N Schedule
Schedule 1 : N Booking
```

---

# 4. Business Rules

## 정원 제한

예약 가능 조건

```text
reservedCount < capacity
```

---

## 시간 기준 수업

```text
startTime
endTime
```

예약은 반드시 수업 시작 이전에 생성되어야 한다.

---

# 5. Layered Structure

```
schedule
├─ api
├─ application
├─ domain
└─ infrastructure
```

---

# 6. Summary

```text
Schedule은 실제 수업 시간을 정의하는 도메인이다.
Booking은 Schedule을 기준으로 생성된다.
```