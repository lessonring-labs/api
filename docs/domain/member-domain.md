# Member Domain

`Member` 도메인은 LessonRing에서 **회원 관리의 기준이 되는 핵심 도메인**이다.  
이용권, 예약, 출석, 결제 등 주요 기능은 모두 Member를 기준으로 연결된다.

---

# 1. Domain Role

`Member`는 스튜디오에 등록된 회원이다.

주요 책임

```text
회원 등록
회원 조회
회원 상태 관리
예약 / 이용권 / 결제의 기준 사용자 역할
```

한 줄 정의

```text
Member는 스튜디오에 등록되어 이용권, 예약, 출석, 결제의 기준이 되는 핵심 회원 도메인이다.
```

---

# 2. Entity Structure

## Member

```text
Member
- id : Long
- studioId : Long
- name : String
- phone : String
- email : String
- gender : Gender
- birthDate : LocalDate
- status : MemberStatus
- joinedAt : LocalDateTime
- memo : String
```

## BaseEntity

```text
BaseEntity
- createdAt : LocalDateTime
- createdBy : Long
- updatedAt : LocalDateTime
- updatedBy : Long
```

실제 테이블 기준으로 보면 Member는 아래 구조를 가진다.

```text
member
- id
- studio_id
- name
- phone
- email
- gender
- birth_date
- status
- joined_at
- memo
- created_at
- created_by
- updated_at
- updated_by
```

---

# 3. Enum

## Gender

```text
MALE
FEMALE
NONE
```

설명

```text
MALE   → 남성
FEMALE → 여성
NONE   → 미입력 / 선택 안 함
```

## MemberStatus

```text
ACTIVE
INACTIVE
BLOCKED
```

설명

```text
ACTIVE   → 정상 이용 가능
INACTIVE → 비활성 상태
BLOCKED  → 이용 제한 상태
```

---

# 4. Business Rules

## 4.1 회원은 반드시 스튜디오에 소속된다

```text
studioId는 필수값이다.
```

Member는 단독으로 존재하지 않고 반드시 특정 스튜디오에 소속된다.

---

## 4.2 회원 기본 정보 중 핵심값은 name, phone 이다

```text
name  → 회원명
phone → 연락처
```

초기 운영 기준에서는 전화번호가 가장 중요한 식별값 중 하나다.

---

## 4.3 회원 상태는 status로 관리한다

회원의 활성/비활성/제한 여부는 `MemberStatus`로 관리한다.

예

```text
ACTIVE   → 예약, 이용권, 결제 가능
INACTIVE → 운영상 비활성
BLOCKED  → 정책상 이용 제한
```

---

## 4.4 가입 시점은 joinedAt으로 관리한다

`joinedAt`은 비즈니스 기준의 가입 시점이다.

구분

```text
createdAt → 레코드 생성 시간
joinedAt  → 회원 가입 기준 시간
```

초기에는 두 값이 같을 수 있지만 의미는 다르다.

---

# 5. Current Implementation Scope

현재 Member 도메인에서 구현한 범위

```text
회원 등록
회원 단건 조회
회원 목록 조회
```

현재 API

```text
POST /members
GET /members/{id}
GET /members
```

---

# 6. Layered Structure

Member 도메인은 아래 구조를 따른다.

```text
member
├─ api
│  ├─ MemberController.java
│  ├─ request
│  │  └─ MemberCreateRequest.java
│  └─ response
│     └─ MemberResponse.java
│
├─ application
│  └─ MemberService.java
│
├─ domain
│  ├─ Gender.java
│  ├─ Member.java
│  ├─ MemberStatus.java
│  └─ repository
│     └─ MemberRepository.java
│
└─ infrastructure
   └─ persistence
      ├─ MemberJpaRepository.java
      └─ MemberRepositoryImpl.java
```

---

# 7. Request / Response

## MemberCreateRequest

```text
studioId
name
phone
email
gender
birthDate
memo
```

## MemberResponse

```text
id
studioId
name
phone
email
gender
birthDate
status
joinedAt
memo
```

---

# 8. Service Responsibilities

`MemberService`의 현재 책임은 아래와 같다.

```text
회원 생성
회원 단건 조회
회원 목록 조회
```

예외 처리 기준

```java
throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
```

---

# 9. Repository Responsibilities

`MemberRepository`는 도메인 저장소 인터페이스다.

현재 제공 기능

```text
save
findById
findAll
```

JPA 구현체는 `MemberJpaRepository`, `MemberRepositoryImpl`에서 담당한다.

---

# 10. Future Expansion

다음 단계에서 Member 도메인에 추가될 가능성이 높은 기능

```text
전화번호 중복 검사
회원 수정
회원 상태 변경
회원 삭제(soft delete)
회원 검색 / 페이징 조회
회원별 예약 목록 조회
회원별 이용권 목록 조회
```

---

# 11. Relationship with Other Domains

Member는 아래 도메인의 기준이 된다.

```text
Member
 ├─ Membership
 ├─ Booking
 ├─ Attendance
 └─ Payment
```

설명

```text
Membership → 회원이 보유한 이용권
Booking    → 회원이 생성한 예약
Attendance → 회원의 출석 이력
Payment    → 회원의 결제 이력
```

---

# 12. Development Rules

Member 도메인 개발 시 아래 원칙을 따른다.

```text
Controller는 얇게 유지
Service는 비즈니스 로직만 담당
Entity setter 사용 금지
Entity는 static factory 사용
Response는 DTO로 반환
예외는 BusinessException 사용
```

예시

```java
Member member = Member.create(
    request.getStudioId(),
    request.getName(),
    request.getPhone(),
    request.getEmail(),
    request.getGender(),
    request.getBirthDate(),
    request.getMemo()
);
```

---

# 13. Summary

```text
Member는 LessonRing에서 회원 관리의 기준이 되는 핵심 도메인이다.
이용권, 예약, 출석, 결제는 모두 Member를 중심으로 연결된다.
현재는 등록 / 단건 조회 / 목록 조회까지 구현되었으며,
이후 Membership, Booking, Payment 도메인의 기준 엔티티로 사용된다.
```