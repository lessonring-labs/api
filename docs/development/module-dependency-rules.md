# Module Dependency Rules

LessonRing Backend의 모듈 의존성 규칙을 정의한다.

이 문서는 다음 목적을 가진다.

- 모듈 간 의존성 구조 명확화
- 도메인 간 결합도 최소화
- 유지보수성 향상
- 코드 확장성 확보
- 안정적인 아키텍처 유지

---

# 1. Module Overview

LessonRing Backend는 **도메인 중심 모듈 구조**를 사용한다.

모듈 구조 예시

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

각 모듈은 하나의 도메인 책임을 가진다.

예시

member → 회원 관리  
schedule → 수업 스케줄 관리  
booking → 예약 관리  
attendance → 출석 관리  
membership → 이용권 관리

---

# 2. Module Dependency Principle

모듈 의존성은 다음 원칙을 따른다.

1 도메인 간 결합 최소화  
2 순환 의존 금지  
3 공통 로직은 common 모듈 사용  
4 외부 시스템 연동은 integration 모듈 사용  
5 도메인은 다른 도메인의 내부 구현에 의존하지 않는다

---

# 3. 기본 모듈 구조

각 도메인 모듈은 다음 구조를 따른다.

api  
application  
domain  
infrastructure

예시

booking  
├ api  
├ application  
├ domain  
└ infrastructure

각 Layer 역할

api  
HTTP 요청 처리

application  
비즈니스 흐름 제어

domain  
핵심 비즈니스 모델

infrastructure  
데이터 접근 및 외부 연동

---

# 4. Module Dependency Direction

모듈 의존성 방향은 다음과 같다.

api  
↓  
application  
↓  
domain  
↓  
repository interface  
↓  
infrastructure

infrastructure는 domain에 의존하지만  
domain은 infrastructure에 의존하지 않는다.

---

# 5. 허용되는 의존성

도메인 모듈 간 의존성은 최소화해야 한다.

허용되는 경우

booking → member  
booking → schedule  
booking → membership

이유

예약 생성 시 다음 정보가 필요하기 때문

- 회원 정보
- 스케줄 정보
- 이용권 정보

---

# 6. 금지되는 의존성

다음 의존성은 금지한다.

순환 의존

예

member → booking  
booking → member

이 구조는 아키텍처 복잡도를 증가시킨다.

---

또한 다음 의존성도 금지한다.

domain → controller  
domain → api dto  
domain → repository implementation

---

# 7. Domain Isolation

각 Domain은 독립적으로 설계되어야 한다.

Domain이 직접 다른 Domain의 내부 구조를 참조하는 것은 지양한다.

예

Booking Domain

memberId  
scheduleId  
membershipId

같이 **식별자 기반 참조**를 사용한다.

Entity 직접 참조는 최소화한다.

---

# 8. Common Module

공통 기능은 common 모듈에 위치한다.

common 모듈 구성

config  
entity  
error  
event  
response  
security  
util

예시

ApiResponse  
ErrorCode  
BusinessException  
BaseEntity  
JwtProvider

---

# 9. Integration Module

외부 시스템 연동은 integration 모듈에서 관리한다.

구성

feign  
kafka  
n8n  
webhook

예시

외부 결제 시스템  
메시지 큐  
웹훅 이벤트

---

# 10. Event 기반 의존성 완화

도메인 간 강한 의존성을 줄이기 위해 Event 기반 구조를 사용할 수 있다.

예시

BookingCreatedEvent  
AttendanceMarkedEvent  
PaymentCompletedEvent

이벤트 처리

EventPublisher  
EventHandler

---

# 11. Analytics Module

analytics 모듈은 운영 데이터 분석을 담당한다.

역할

- 통계 데이터 생성
- 운영 리포트
- 데이터 집계

analytics는 다른 도메인의 데이터를 조회할 수 있지만  
비즈니스 로직 변경 권한은 없다.

---

# 12. Notification Module

notification 모듈은 알림 기능을 담당한다.

예시

예약 알림  
예약 취소 알림  
결제 완료 알림

notification은 도메인 이벤트를 기반으로 동작한다.

---

# 13. Integration Dependency Rule

integration 모듈은 외부 시스템과의 연결을 담당한다.

다른 도메인은 integration을 직접 의존하지 않는다.

권장 구조

application service  
→ integration client

---

# 14. Cross Domain Interaction

도메인 간 상호작용은 Application Service에서 관리한다.

예시

BookingService

- Member 조회
- Schedule 조회
- Membership 조회
- Booking 생성

Domain끼리 직접 호출하는 구조는 지양한다.

---

# 15. Dependency Management Guidelines

모듈 의존성을 관리할 때 다음을 고려한다.

- 순환 의존 방지
- Domain 독립성 유지
- 공통 로직 재사용
- Event 기반 확장 고려

---

# 16. Dependency Example

예약 생성 흐름

BookingService  
→ MemberRepository 조회  
→ ScheduleRepository 조회  
→ MembershipRepository 조회  
→ Booking 생성

Booking Domain은 Member Entity에 직접 의존하지 않는다.

---

# 17. Module Responsibility

각 모듈은 하나의 책임만 가진다.

예시

member → 회원 관리  
schedule → 수업 관리  
booking → 예약 관리  
attendance → 출석 관리  
membership → 이용권 관리  
payment → 결제 처리

---

# 18. Dependency Violations

다음 구조는 잘못된 예이다.

Domain에서 Controller 호출

Booking Domain  
→ BookingController

또는

Domain에서 Repository Implementation 사용

Booking Domain  
→ BookingJpaRepository

---

# 19. Dependency Review Checklist

모듈 의존성을 검토할 때 다음을 확인한다.

- 순환 의존이 없는가
- Domain이 Infrastructure에 의존하지 않는가
- Common 모듈이 과도하게 커지지 않는가
- 도메인 간 결합이 최소화되어 있는가
- 이벤트 기반 구조를 고려했는가

---

# 20. Summary

LessonRing Backend는 도메인 중심 모듈 구조를 사용한다.

모듈 의존성 원칙

- Domain 독립성 유지
- 순환 의존 금지
- Common 모듈 재사용
- Event 기반 확장 고려

이 규칙을 통해 안정적인 Backend 아키텍처를 유지한다.