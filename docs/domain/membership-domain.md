# Membership Domain

`Membership` 도메인은 LessonRing에서 **회원이 보유한 이용권을 관리하는 도메인**이다.

회원(Member)은 하나 이상의 이용권(Membership)을 가질 수 있으며  
예약, 출석, 결제 등은 이 이용권을 기준으로 동작한다.

---

# 1. Domain Role

Membership의 주요 책임

```text
이용권 생성
이용권 사용 횟수 관리
이용권 유효기간 관리
이용권 상태 관리
예약과 출석의 기준 제공
```

한 줄 정의

```text
Membership은 회원이 보유한 이용권을 관리하는 도메인이다.
```

---

# 2. Entity Structure

## Membership

```text
Membership
- id : Long
- studioId : Long
- memberId : Long
- name : String
- totalCount : Integer
- remainCount : Integer
- startDate : LocalDate
- endDate : LocalDate
- status : MembershipStatus
```

설명

```text
studioId     → 스튜디오 ID
memberId     → 회원 ID
name         → 이용권 이름
totalCount   → 총 이용 횟수
remainCount  → 남은 이용 횟수
startDate    → 이용권 시작일
endDate      → 이용권 종료일
status       → 이용권 상태
```

---

# 3. BaseEntity

Membership은 BaseEntity를 상속한다.

```text
BaseEntity
- createdAt
- createdBy
- updatedAt
- updatedBy
```

---

# 4. Enum

## MembershipStatus

```text
ACTIVE
EXPIRED
SUSPENDED
USED_UP
```

설명

```text
ACTIVE    → 정상 사용 가능
EXPIRED   → 기간 만료
SUSPENDED → 운영 중지
USED_UP   → 이용 횟수 모두 사용
```

---

# 5. Business Rules

## 5.1 Membership은 반드시 Member에 속한다

```text
memberId는 필수 값이다.
```

관계

```text
Member 1 : N Membership
```

---

## 5.2 이용권 사용 시 remainCount가 감소한다

예약이 출석 처리되면 이용권 횟수가 차감된다.

```text
remainCount = remainCount - 1
```

remainCount가 0이 되면

```text
status = USED_UP
```

---

## 5.3 이용권은 기간 제한을 가진다

```text
startDate
endDate
```

현재 날짜가 endDate 이후이면

```text
status = EXPIRED
```

---

## 5.4 예약은 ACTIVE 상태 이용권만 사용 가능

예약 생성 시 검증

```text
status == ACTIVE
remainCount > 0
today <= endDate
```

---

# 6. Relationship

Membership은 아래 도메인과 연결된다.

```text
Member
 └─ Membership
     ├─ Booking
     └─ Attendance
```

설명

```text
Member     → 회원
Membership → 이용권
Booking    → 예약
Attendance → 출석
```

---

# 7. Layered Structure

Membership 도메인 구조

```text
membership
├─ api
│  ├─ MembershipController.java
│  ├─ request
│  │  └─ MembershipCreateRequest.java
│  └─ response
│     └─ MembershipResponse.java
│
├─ application
│  └─ MembershipService.java
│
├─ domain
│  ├─ Membership.java
│  ├─ MembershipStatus.java
│  └─ repository
│     └─ MembershipRepository.java
│
└─ infrastructure
   └─ persistence
      ├─ MembershipJpaRepository.java
      └─ MembershipRepositoryImpl.java
```

---

# 8. Request / Response

## MembershipCreateRequest

```text
studioId
memberId
name
totalCount
startDate
endDate
```

---

## MembershipResponse

```text
id
studioId
memberId
name
totalCount
remainCount
startDate
endDate
status
```

---

# 9. Service Responsibilities

MembershipService의 책임

```text
이용권 생성
이용권 조회
이용권 상태 관리
이용권 사용 처리
```

---

# 10. Current Implementation Scope

현재 구현 범위

```text
이용권 생성
이용권 조회
```

향후 확장

```text
이용권 사용 처리
이용권 상태 변경
회원별 이용권 조회
이용권 만료 처리
```

---

# 11. Future Expansion

향후 추가될 가능성이 높은 기능

```text
이용권 자동 만료 처리
이용권 일시 정지
이용권 연장
이용권 환불
이용권 양도
```

---

# 12. Summary

```text
Membership은 회원이 보유한 이용권을 관리하는 도메인이다.

예약과 출석은 Membership을 기준으로 처리되며
이용 횟수와 기간을 기반으로 이용권 상태가 관리된다.
```