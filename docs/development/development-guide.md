# Development Guide

LessonRing Backend 개발 가이드를 정의한다.

이 문서는 다음 목적을 가진다.

- Backend 개발 절차 표준화
- 코드 품질 유지
- 협업 효율 향상
- 도메인 중심 개발 방식 유지
- 신규 개발자 온보딩 지원

---

# 1. 개발 원칙

LessonRing Backend는 다음 개발 원칙을 따른다.

- Domain 중심 설계를 유지한다.
- Controller는 얇게 유지한다.
- Service는 비즈니스 흐름을 제어한다.
- Domain은 상태와 행위를 표현한다.
- Repository는 데이터 접근만 담당한다.
- 모든 API는 일관된 응답 구조를 사용한다.
- 코드 가독성을 최우선으로 한다.

---

# 2. Backend Architecture Overview

LessonRing Backend는 다음 구조를 기반으로 개발한다.

Layer 구조

Controller  
↓  
Application Service  
↓  
Domain  
↓  
Repository  
↓  
Database

각 Layer 역할

Controller
- HTTP 요청 처리
- Request DTO 검증
- Service 호출
- Response 반환

Application Service
- 비즈니스 흐름 제어
- 트랜잭션 관리
- 도메인 조합

Domain
- 핵심 비즈니스 모델
- 상태 변경 로직
- 도메인 규칙

Repository
- 데이터 조회
- 데이터 저장

---

# 3. 패키지 구조

Backend 기본 패키지 구조

com.lessonring.api

auth  
member  
membership  
schedule  
booking  
attendance  
payment  
notification  
studio  
instructor  
analytics  
integration  
common

각 도메인 패키지는 다음 구조를 따른다.

api  
application  
domain  
infrastructure

예시

member  
├ api  
├ application  
├ domain  
└ infrastructure

---

# 4. 개발 순서

Backend 기능 개발은 다음 순서를 따른다.

1 Domain 설계  
2 Entity 생성  
3 Repository 인터페이스 작성  
4 Service 구현  
5 Controller 구현  
6 API 테스트  
7 문서 업데이트

---

# 5. 기능 개발 절차

새 기능을 개발할 때 다음 절차를 따른다.

1 Domain 요구사항 정의  
2 Domain Entity 설계  
3 Domain 상태 정의  
4 Repository 인터페이스 정의  
5 Application Service 구현  
6 Controller API 구현  
7 DTO 작성  
8 Validation 적용  
9 테스트 수행  
10 문서 업데이트

---

# 6. Domain 설계

Domain은 비즈니스 의미를 표현하는 핵심 모델이다.

Domain 설계 시 고려 사항

- 비즈니스 용어 사용
- 상태 변화 명확화
- 불변 조건 유지
- 의미 있는 메서드 제공

예시

Booking

create()  
cancel()  
attend()

Membership

create()  
useOnce()  
expire()

---

# 7. API 설계 규칙

API는 REST 구조를 따른다.

기본 URL

/api/v1

예시

/api/v1/auth/login  
/api/v1/members  
/api/v1/schedules  
/api/v1/bookings  
/api/v1/memberships

---

# 8. Request / Response 구조

모든 API는 공통 응답 구조를 사용한다.

응답 예시

{
"success": true,
"data": {},
"error": null
}

Controller 예시

return ApiResponse.success(response);

---

# 9. DTO 규칙

DTO는 다음 규칙을 따른다.

Request DTO

CreateRequest  
UpdateRequest  
SearchRequest

예시

MemberCreateRequest  
ScheduleCreateRequest  
BookingCreateRequest

Response DTO

Response suffix 사용

예시

MemberResponse  
BookingResponse  
ScheduleResponse

---

# 10. Validation 규칙

입력값 검증은 Request DTO에서 수행한다.

사용 예시

@NotNull  
@NotBlank  
@Positive  
@Future

예시

public class BookingCreateRequest {

    @NotNull
    private Long memberId;

    @NotNull
    private Long scheduleId;

}

비즈니스 검증은 Service 또는 Domain에서 수행한다.

---

# 11. 트랜잭션 규칙

트랜잭션은 Service Layer에서 관리한다.

조회

@Transactional(readOnly = true)

쓰기

@Transactional

Controller에는 트랜잭션을 두지 않는다.

---

# 12. Repository 규칙

Repository는 다음 규칙을 따른다.

- Domain 패키지에 인터페이스 정의
- Infrastructure 패키지에 구현체 작성

예시

MemberRepository  
MemberJpaRepository  
MemberRepositoryImpl

Repository는 비즈니스 로직을 포함하지 않는다.

---

# 13. 예외 처리 규칙

예외 처리는 공통 구조를 사용한다.

구성

BusinessException  
ErrorCode  
GlobalExceptionHandler

예시

throw new BusinessException(ErrorCode.INVALID_REQUEST);

Controller에서는 try-catch 남용을 피한다.

---

# 14. 로그 규칙

로그는 필요한 경우에만 사용한다.

권장 상황

- 로그인 실패
- 예약 생성
- 예약 취소
- 결제 실패
- 외부 연동 실패

로그 레벨

INFO  
WARN  
ERROR

민감 정보는 로그에 기록하지 않는다.

---

# 15. 테스트 방법

Backend 기능 테스트는 다음 방법을 사용한다.

1 curl 테스트  
2 Postman 테스트  
3 Swagger 테스트

예시

curl -X POST http://localhost:8080/api/v1/schedules \
-H "Authorization: Bearer ACCESS_TOKEN"

---

# 16. 개발 환경

Backend 개발 환경

Language  
Java

Framework  
Spring Boot

Database  
PostgreSQL

ORM  
Spring Data JPA  
QueryDSL

Migration  
Flyway

Authentication  
JWT

Cache  
Redis

Messaging  
Kafka

---

# 17. 개발 도구

개발에 사용하는 주요 도구

IDE  
IntelliJ

Version Control  
Git

API Test  
Postman  
curl

Container  
Docker

Orchestration  
Kubernetes (k3s)

Monitoring  
Scouter  
OpenLens

Analytics  
Metabase

---

# 18. Git Workflow

개발은 다음 Git Workflow를 따른다.

main  
develop  
feature

예시

feature/member-api  
feature/booking-api

커밋 타입

기능  
수정  
리팩토링  
문서  
테스트  
설정

---

# 19. 문서 관리

모든 설계 문서는 docs 디렉토리에서 관리한다.

구조

docs

api  
architecture  
development  
domain

개발 시 문서 업데이트를 함께 수행한다.

---

# 20. 코드 리뷰 기준

코드 리뷰 시 다음을 확인한다.

- 네이밍이 명확한가
- 계층 책임이 분리되어 있는가
- Controller가 비대하지 않은가
- Domain이 의미 있는 상태를 가지는가
- 예외 처리가 일관적인가
- DTO와 Domain이 분리되어 있는가

---

# 21. 신규 개발자 온보딩

신규 개발자는 다음 순서로 프로젝트를 이해한다.

1 System Architecture 문서 확인  
2 Package Structure 문서 확인  
3 Domain 문서 확인  
4 Development Guide 확인  
5 API 테스트 수행

이 과정을 통해 전체 시스템 구조를 빠르게 이해할 수 있다.

---

# 22. Summary

LessonRing Backend 개발은 다음 원칙을 기반으로 진행된다.

- Domain 중심 설계
- 계층 책임 분리
- 일관된 API 구조
- 공통 예외 처리
- 표준화된 코드 스타일

이 문서는 Backend 개발 시 반드시 참고해야 하는 기본 가이드이다.