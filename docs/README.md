# LessonRing Documentation

LessonRing Backend 프로젝트의 기술 문서 모음이다.

이 문서는 시스템 아키텍처, 도메인 설계, API 규칙, 개발 가이드를 설명한다.

---

# Documentation Structure

```text
docs
│
├─ README.md
│
├─ architecture
│  ├─ system-architecture.md
│  ├─ core-domain.md
│  ├─ security-architecture.md
│  └─ domain-event-flow.md
│
├─ api
│  ├─ api-versioning.md
│  ├─ common-response-format.md
│  └─ error-handling.md
│
├─ domain
│  ├─ member_domain.md
│  ├─ membership_domain.md
│  ├─ schedule_domain.md
│  ├─ booking_domain.md
│  ├─ attendance_domain.md
│  ├─ payment_domain.md
│  └─ notification_domain.md
│
└─ development
   ├─ backend-development-guide.md
   ├─ coding-convention.md
   ├─ module-dependency-rules.md
   ├─ package-structure.md
   └─ transaction-strategy.md
```

---

# Architecture

시스템 전체 구조와 아키텍처 설계를 설명하는 문서

```
architecture/
```

문서 목록

```
system-architecture.md
core-domain.md
security-architecture.md
domain-event-flow.md
```

설명

```text
system-architecture.md  → 전체 시스템 아키텍처 및 내부 모듈 구조
core-domain.md          → 핵심 도메인 구조
security-architecture.md→ 인증 및 보안 구조
domain-event-flow.md    → 도메인 이벤트 설계
```

---

# API

API 설계 규칙과 공통 응답 형식을 설명한다.

```
api/
```

문서

```
api-versioning.md
common-response-format.md
error-handling.md
```

설명

```text
api-versioning.md        → API 버전 관리 정책
common-response-format.md → 공통 API 응답 구조
error-handling.md         → API 에러 처리 규칙
```

---

# Domain

LessonRing의 핵심 비즈니스 도메인 설계를 설명한다.

```
domain/
```

문서

```
member-domain.md
membership-domain.md
schedule-domain.md
booking-domain.md
attendance-domain.md
payment-domain.md
notification-domain.md
```

핵심 도메인 흐름

```text
Member
 → Membership
   → Schedule
     → Booking
       → Attendance
         → Payment
           → Notification
```

---

# Development Guide

개발 규칙과 프로젝트 개발 가이드를 설명한다.

```
development/
```

문서

```
backend-development-guide.md
coding-convention.md
module-dependency-rules.md
package-structure.md
transaction-strategy.md
```

설명

```text
backend-development-guide.md → 백엔드 개발 가이드
coding-convention.md         → 코드 스타일 규칙
module-dependency-rules.md   → 모듈 의존성 규칙
package-structure.md         → 패키지 구조 가이드
transaction-strategy.md      → 트랜잭션 전략
```

---

# Recommended Reading Order

신규 개발자는 아래 순서로 문서를 읽는 것을 권장한다.

```
1. architecture/system-architecture.md
2. architecture/core-domain.md
3. architecture/security-architecture.md
4. domain/member_domain.md
5. domain/membership_domain.md
6. domain/schedule_domain.md
7. domain/booking_domain.md
```

---

# System Overview

LessonRing Backend는 다음 기술 스택을 기반으로 구축된다.

```text
Spring Boot
PostgreSQL
Redis
Kafka
Docker
Kubernetes
Metabase
n8n
```

아키텍처 스타일

```text
Modular Monolith
Event Driven Architecture
C4 Model
```

---

# Summary

```text
docs 폴더는 LessonRing Backend의 기술 문서를 관리한다.

Architecture → 시스템 설계
API          → API 규칙
Domain       → 비즈니스 도메인 설계
Development  → 개발 규칙 및 가이드
```
