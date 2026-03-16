# Package Structure

LessonRing Backend의 패키지 구조 규칙을 정의한다.

이 문서는 다음 목적을 가진다.

- Backend 패키지 구조 표준화
- 계층 분리 명확화
- 도메인 중심 구조 유지
- 코드 가독성 향상
- 유지보수성 확보

---

# 1. Package Structure Overview

LessonRing Backend는 **도메인 중심 패키지 구조(Domain-Oriented Package Structure)**를 사용한다.

기본 패키지

com.lessonring.api

이 하위에 도메인별 패키지를 구성한다.

예시

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

---

# 2. Domain Module Structure

각 도메인 모듈은 다음 구조를 따른다.

api  
application  
domain  
infrastructure

예시

booking

booking  
├ api  
├ application  
├ domain  
└ infrastructure

각 계층은 명확한 역할을 가진다.

---

# 3. Layer Responsibilities

## api

HTTP 요청을 처리하는 계층이다.

구성

Controller  
Request DTO  
Response DTO

예시

booking/api

BookingController  
request/BookingCreateRequest  
response/BookingResponse

---

## application

비즈니스 흐름을 제어하는 계층이다.

구성

Service  
Use case orchestration

예시

booking/application

BookingService

---

## domain

핵심 비즈니스 모델을 정의하는 계층이다.

구성

Entity  
Enum  
Domain Rule  
Repository Interface

예시

booking/domain

Booking  
BookingStatus  
BookingRepository

---

## infrastructure

외부 시스템 및 데이터 접근을 담당한다.

구성

JPA Repository  
Repository Implementation  
External Client

예시

booking/infrastructure

BookingJpaRepository  
BookingRepositoryImpl

---

# 4. Common Package

공통 기능은 common 패키지에 위치한다.

구성

config  
entity  
error  
event  
infrastructure  
lock  
response  
security  
swagger  
util

예시

common/error

BusinessException  
ErrorCode  
GlobalExceptionHandler

예시

common/response

ApiResponse

---

# 5. Integration Package

외부 시스템 연동은 integration 패키지에서 관리한다.

구성

feign  
kafka  
n8n  
webhook

예시

integration/kafka

EventProducer  
EventConsumer

---

# 6. Package Naming Rules

패키지명은 다음 규칙을 따른다.

- 모두 소문자 사용
- 도메인 의미를 명확히 표현
- 약어 사용 최소화

좋은 예

member  
booking  
membership

나쁜 예

usr  
bk  
mem

---

# 7. DTO Package Structure

DTO는 api 계층 하위에 위치한다.

구성

request  
response

예시

booking/api/request

BookingCreateRequest

booking/api/response

BookingResponse

---

# 8. Repository Structure

Repository 인터페이스는 domain 계층에 위치한다.

예시

booking/domain/repository

BookingRepository

Repository 구현체는 infrastructure 계층에 위치한다.

예시

booking/infrastructure/persistence

BookingJpaRepository  
BookingRepositoryImpl

---

# 9. Enum Package Structure

Enum은 domain 계층에 위치한다.

예시

BookingStatus  
ScheduleStatus  
MembershipStatus  
AttendanceStatus

Enum은 도메인 상태를 명확하게 표현해야 한다.

---

# 10. Entity Package Structure

Entity는 domain 계층에 위치한다.

예시

booking/domain

Booking

member/domain

Member

membership/domain

Membership

---

# 11. Service Package Structure

Service는 application 계층에 위치한다.

예시

booking/application

BookingService

member/application

MemberService

Service는 비즈니스 흐름을 제어한다.

---

# 12. Controller Package Structure

Controller는 api 계층에 위치한다.

예시

booking/api

BookingController

schedule/api

ScheduleController

Controller는 HTTP 요청 처리만 담당한다.

---

# 13. Cross Domain Interaction

도메인 간 상호작용은 Service 계층에서 수행한다.

예시

BookingService

Member 조회  
Schedule 조회  
Membership 조회  
Booking 생성

Domain 간 직접 호출은 최소화한다.

---

# 14. Package Dependency Direction

패키지 의존성 방향

api  
↓  
application  
↓  
domain  
↓  
repository interface  
↓  
infrastructure

domain 계층은 infrastructure 계층에 의존하지 않는다.

---

# 15. Package Design Principles

패키지 설계 시 다음 원칙을 따른다.

- 도메인 중심 구조 유지
- 계층 책임 분리
- 의존성 최소화
- 코드 가독성 유지
- 테스트 용이성 확보

---

# 16. Example Package Structure

실제 Backend 패키지 구조 예시

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

각 도메인은 동일한 내부 구조를 유지한다.

---

# 17. Benefits of This Structure

이 패키지 구조의 장점

- 도메인 중심 코드 구성
- 기능별 코드 분리
- 유지보수 용이
- 확장성 확보
- 신규 개발자 이해도 향상

---

# 18. Anti Patterns

다음 패턴은 지양한다.

Controller에서 Repository 직접 호출

Controller  
→ Repository

또는

Domain에서 Infrastructure 의존

Domain  
→ JpaRepository

또는

DTO와 Entity 혼합 사용

---

# 19. Review Checklist

패키지 구조 검토 시 다음을 확인한다.

- Domain 중심 구조인가
- 계층 책임이 분리되어 있는가
- 순환 의존이 없는가
- Common 패키지가 과도하게 커지지 않았는가
- DTO와 Domain이 분리되어 있는가

---

# 20. Summary

LessonRing Backend는 도메인 중심 패키지 구조를 사용한다.

구조

api  
application  
domain  
infrastructure

각 계층은 명확한 책임을 가진다.

이 구조를 통해 Backend 코드의 가독성, 확장성, 유지보수성을 확보한다.