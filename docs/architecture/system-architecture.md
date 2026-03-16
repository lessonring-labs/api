# System Architecture

LessonRing Backend 시스템 아키텍처를 정의한다.

이 문서는 다음 목적을 가진다.

- 전체 시스템 구조 정의
- Backend 아키텍처 설명
- Infrastructure 구성 설명
- 데이터 흐름 정의
- 확장 아키텍처 방향 제시

---

# 1. System Overview

LessonRing은 필라테스 / 피트니스 스튜디오 운영을 위한 예약 및 회원 관리 플랫폼이다.

핵심 기능

- 회원 관리
- 스케줄 관리
- 예약 관리
- 출석 관리
- 이용권 관리
- 결제 관리
- 알림
- 운영 분석

시스템은 **API 기반 Backend + Web Frontend 구조**로 설계된다.

---

# 2. High Level Architecture

전체 시스템 구조

Client (Web / Mobile)
↓
API Gateway (Nginx)
↓
Backend API (Spring Boot)
↓
Database (PostgreSQL)

추가 구성

Redis (Cache / RateLimit)  
Message Queue (Kafka 예정)  
Monitoring (Scouter / Prometheus)  
Analytics (Metabase)

---

# 3. Backend Architecture

Backend는 **Domain 중심 아키텍처**로 설계된다.

구조

Controller
↓
Application Service
↓
Domain Model
↓
Repository
↓
Database

핵심 원칙

- Domain 중심 설계
- Layer 책임 분리
- Domain Event 기반 확장
- Stateless API

---

# 4. Backend Module Structure

Backend 주요 도메인

auth  
member  
membership  
schedule  
booking  
attendance  
payment  
notification

공통 모듈

common  
security  
config  
event

---

# 5. Package Structure

Backend 패키지 구조

com.lessonring.api

common  
auth  
member  
membership  
schedule  
booking  
attendance  
payment  
notification

각 도메인 구조

api  
application  
domain  
infrastructure

---

# 6. API Architecture

API는 REST 기반 구조를 사용한다.

API Version

/api/v1

예시

/api/v1/auth/login  
/api/v1/schedules  
/api/v1/bookings  
/api/v1/memberships

---

# 7. Authentication Architecture

인증 방식

JWT 기반 인증

구성

AccessToken  
RefreshToken

인증 흐름

Client  
→ Login API 호출  
→ AccessToken 발급  
→ API 요청 시 Authorization Header 사용

Header 예시

Authorization: Bearer {accessToken}

---

# 8. Database Architecture

Primary Database

PostgreSQL

선택 이유

- 안정성
- 트랜잭션 지원
- 확장성
- 오픈소스

ORM

Spring Data JPA

Migration

Flyway

---

# 9. Cache Architecture

Cache 시스템

Redis (예정)

사용 목적

AccessToken blacklist  
Rate Limiting  
Session Cache  
Hot Data Cache

---

# 10. Event Architecture

시스템은 Event Driven 구조 확장을 고려하여 설계된다.

예시 이벤트

BookingCreatedEvent  
BookingCanceledEvent  
AttendanceMarkedEvent  
PaymentCompletedEvent

이벤트 처리 목적

알림 처리  
통계 처리  
외부 시스템 연동

---

# 11. Messaging Architecture (Future)

향후 Message Queue 도입 가능

Kafka

사용 목적

Notification Event  
Analytics Event  
External Integration

---

# 12. Monitoring Architecture

운영 모니터링 도구

Application Monitoring

Scouter / Prometheus

Metrics

CPU  
Memory  
API Response Time  
Error Rate

---

# 13. Logging Architecture

Logging 정책

Application Log  
Security Log  
Error Log

로그 레벨

INFO  
WARN  
ERROR

Log Stack (확장 가능)

ELK Stack  
Loki

---

# 14. Deployment Architecture

배포 구조

Docker 기반 배포

구성

Backend Container  
Database Container  
Cache Container

Orchestration

Kubernetes (향후)

---

# 15. CI/CD Architecture

CI/CD Pipeline

GitHub  
↓
GitHub Actions  
↓
Docker Build  
↓
Container Registry  
↓
Server Deploy

---

# 16. Scalability Strategy

확장 전략

Stateless Backend  
Horizontal Scaling  
Cache Layer 추가  
Message Queue 도입

---

# 17. Availability Strategy

가용성 확보 전략

Health Check  
Container Restart  
Load Balancer 사용

---

# 18. Security Layer

보안 구성

Spring Security  
JWT Authentication  
HTTPS  
CORS 정책

---

# 19. Future Architecture

향후 확장 구조

API Gateway  
Microservice 분리  
Event Driven Architecture 강화  
Data Analytics Pipeline 구축

---

# 20. Architecture Principles

LessonRing 아키텍처 설계 원칙

Domain 중심 설계  
Stateless API  
Event 기반 확장  
확장 가능한 인프라 구조

---

# 21. Summary

LessonRing 시스템은 다음 구조로 설계된다.

Frontend Client  
↓  
API Server (Spring Boot)  
↓  
PostgreSQL Database

확장 구성

Redis  
Kafka  
Monitoring  
Analytics

이 구조는 향후 서비스 확장을 고려하여 설계된 아키텍처이다.