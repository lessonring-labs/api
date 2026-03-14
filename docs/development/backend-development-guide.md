# LessonRing Backend Development Guide

이 문서는 LessonRing 백엔드 개발 시 **코드 스타일 / 아키텍처 규칙 / 개발 순서**를 정의한다.  
신규 개발자는 반드시 이 문서를 기준으로 개발한다.

---

# 1. 기본 코딩 규칙

## 1.1 Controller는 얇게 유지

Controller는 **요청과 응답 처리만 담당**한다.

Controller 역할

```
Request 수신
Service 호출
Response 반환
```

예시

```java
@PostMapping
public ApiResponse<MemberResponse> create(@RequestBody MemberCreateRequest request) {
    return ApiResponse.success(new MemberResponse(memberService.create(request)));
}
```

Controller에서 하면 안되는 것

```
Entity 생성
Repository 호출
비즈니스 로직 처리
```

---

# 1.2 Service는 비즈니스 로직만 담당

Service는 **도메인 흐름을 조합하는 역할**을 한다.

```java
@Transactional
public Member create(MemberCreateRequest request) {

    Member member = Member.create(...);

    return memberRepository.save(member);
}
```

Service에서 하면 안되는 것

```
DTO 생성
HTTP 처리
Response 생성
```

---

# 1.3 Entity는 Setter 사용 금지

엔티티는 setter를 사용하지 않는다.

잘못된 예

```java
member.setName("Devyn");
```

올바른 예

```java
member.changeName("Devyn");
```

또는

```java
Member.create(...)
```

---

# 1.4 Entity 생성은 Static Factory 사용

엔티티 생성은 반드시 Entity 내부에서 수행한다.

```java
public static Member create(...) {
    return new Member(...);
}
```

이유

```
상태 일관성 유지
기본값 관리
도메인 규칙 관리
```

---

# 1.5 Repository는 Interface 기준

Service는 JPA Repository를 직접 알지 않는다.

구조

```
domain
 └ repository
     MemberRepository

infrastructure
 └ persistence
     MemberJpaRepository
     MemberRepositoryImpl
```

Service는 `MemberRepository`만 의존한다.

---

# 1.6 API 응답은 ApiResponse 사용

모든 API 응답은 다음 형식을 따른다.

```json
{
  "success": true,
  "data": {},
  "error": null
}
```

Controller 반환

```java
return ApiResponse.success(...);
```

---

# 1.7 예외 처리

모든 예외는 BusinessException을 사용한다.

```java
throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
```

사용 금지

```
RuntimeException
IllegalArgumentException
```

---

# 2. 실무 필수 아키텍처 규칙

## 2.1 Soft Delete 사용

데이터는 물리 삭제하지 않는다.

예

```
MemberStatus

ACTIVE
INACTIVE
DELETED
```

삭제 예시

```java
member.delete();
```

---

# 2.2 BaseEntity 사용

모든 Entity는 BaseEntity를 상속한다.

BaseEntity 필드

```
createdAt
updatedAt
```

JpaAuditing으로 자동 관리한다.

---

# 2.3 DTO 사용

Controller는 Entity를 직접 반환하지 않는다.

잘못된 예

```java
return member;
```

올바른 예

```java
return new MemberResponse(member);
```

---

# 2.4 목록 조회는 Paging 필수

목록 조회 API는 반드시 페이징을 사용한다.

예

```
GET /members?page=0&size=20
```

---

# 2.5 도메인 검증은 Service에서 수행

비즈니스 검증은 Service에서 처리한다.

예시

```java
if(memberRepository.existsByPhone(phone)) {
    throw new BusinessException(ErrorCode.DUPLICATE_MEMBER);
}
```

---

# 3. 프로젝트 패키지 구조

## 3.1 기능 중심 패키지 구조

```
auth
member
membership
schedule
booking
attendance
payment
notification
```

잘못된 구조

```
controller
service
repository
entity
```

---

## 3.2 레이어 구조

```
api
application
domain
infrastructure
common
```

설명

```
api            → Controller
application    → Service
domain         → Entity / Repository
infrastructure → DB / 외부 시스템
common         → 공통 코드
```

---

# 4. 현재 개발 진행 상태

완료

```
1️⃣ Core Domain 설계
2️⃣ DB + JPA 기본 세팅
3️⃣ Common 공통 기반
4️⃣ Auth (JWT / RefreshToken)
```

진행 시작

```
5️⃣ Member
```

---

# 5. Member 도메인 구현

다음 API를 구현한다.

```
POST /members
GET /members/{id}
GET /members
```

---

# 6. 다음 개발 단계

Member 구현 후 다음 도메인을 개발한다.

```
Membership
Schedule
Booking
Attendance
Payment
Notification
```

개발 순서

```
1️⃣ Member
2️⃣ Membership
3️⃣ Schedule
4️⃣ Booking
5️⃣ Attendance
6️⃣ Payment
7️⃣ Notification
```

---

# 7. 개발 원칙

개발 시 반드시 다음 원칙을 따른다.

```
Controller는 얇게
Service는 비즈니스 로직
Entity는 Setter 금지
DTO 사용
Soft Delete 사용
Paging 사용
예외는 BusinessException
```