# LessonRing Backend Documentation

LessonRing Backend API 서버의 설계, 개발 규칙, 도메인 구조, API 규격을 정리한 문서이다.

이 문서는 다음 목적을 가진다.

- Backend 아키텍처 이해
- API 규격 통일
- 도메인 설계 공유
- 개발 규칙 표준화
- 신규 개발자 온보딩 지원

---

# Documentation Structure

LessonRing 문서는 다음 구조로 관리된다.

```
docs
├── README.md
├── api
├── architecture
├── development
├── domain
└── git.md
```

각 문서 영역은 Backend 개발에 필요한 규칙과 설계를 정의한다.

---

# Architecture

Backend 시스템 아키텍처 및 설계 문서이다.

위치

```
docs/architecture
```

포함 문서

- system-architecture.md
- security-architecture.md
- domain-architecture.md
- event-architecture.md

설명

| 문서 | 설명 |
|-----|-----|
| system-architecture.md | 전체 Backend 시스템 구조 |
| security-architecture.md | 인증 및 보안 구조 |
| domain-architecture.md | 도메인 설계 전략 |
| event-architecture.md | 이벤트 기반 처리 구조 |

---

# API

Backend API 설계 규칙을 정의한다.

위치

```
docs/api
```

문서

- api-guidelines.md

포함 내용

- API Versioning
- Response Format
- Error Handling
- HTTP Status Rules
- REST API Design Principles

API 구현 시 반드시 이 규칙을 따른다.

---

# Domain

Backend 도메인 설계를 정의한다.

위치

```
docs/domain
```

포함 도메인

- studio-domain.md
- instructor-domain.md
- member-domain.md
- schedule-domain.md
- booking-domain.md
- membership-domain.md
- attendance-domain.md
- payment-domain.md
- payment-operation-domain.md
- payment-webhook-log-domain.md
- notification-domain.md
- refresh-token-domain.md

각 문서는 다음 내용을 포함한다.

- Domain 역할
- Entity 구조
- 상태 정의
- 주요 비즈니스 로직
- 도메인 이벤트

---

# Development

Backend 개발 규칙 및 개발 가이드를 정의한다.

위치

```
docs/development
```

포함 문서

- development-guide.md
- development-process.md
- coding-rules.md
- module-dependency-rules.md
- package-structure.md
- transaction-strategy.md

설명

| 문서 | 설명 |
|-----|-----|
| development-guide.md | Backend 개발 가이드 |
| development-process.md | 개발 프로세스 및 진행 전략 |
| coding-rules.md | 코드 스타일 및 규칙 |
| module-dependency-rules.md | 모듈 의존성 규칙 |
| package-structure.md | 패키지 구조 정의 |
| transaction-strategy.md | 트랜잭션 설계 전략 |

---

# Git

Git 협업 규칙을 정의한다.

위치

```
docs/git.md
```

포함 내용

- Commit 규칙
- Branch 전략
- PR 규칙
- 코드 리뷰 규칙

---

# Backend Architecture Overview

LessonRing Backend는 **도메인 중심 아키텍처(Domain-Oriented Architecture)** 기반으로 설계되었다.

패키지 구조

```
com.lessonring.api
```

도메인 모듈

- auth
- member
- membership
- schedule
- booking
- attendance
- payment
- notification
- instructor
- studio
- analytics
- integration

각 도메인은 다음 구조를 가진다.

```
domain-module
├── api
├── application
├── domain
└── infrastructure
```

설명

| 계층 | 역할 |
|-----|-----|
| api | Controller 및 API DTO |
| application | 서비스 및 비즈니스 흐름 |
| domain | 핵심 비즈니스 모델 |
| infrastructure | DB 및 외부 시스템 |

---

# Technology Stack

Backend 기술 스택

| 영역 | 기술 |
|----|----|
| Language | Java |
| Framework | Spring Boot |
| ORM | JPA / Hibernate |
| Database | PostgreSQL |
| Build | Gradle |
| Security | Spring Security + JWT |
| API Docs | Swagger |
| Messaging | Kafka (planned) |
| Automation | n8n (planned) |

---

# Development Principles

LessonRing Backend 개발 원칙

- Domain 중심 설계
- 계층 책임 분리
- API 규격 통일
- 트랜잭션 일관성 유지
- 확장 가능한 구조 설계

---

# Quick Navigation

주요 문서 바로가기

Architecture

- docs/architecture/system-architecture.md
- docs/architecture/security-architecture.md
- docs/architecture/domain-architecture.md
- docs/architecture/event-architecture.md
- docs/architecture/technology-stack.md

API

- docs/api/api-guidelines.md

Development

- docs/development/development-guide.md
- docs/development/coding-rules.md
- docs/development/package-structure.md
- docs/development/module-dependency-rules.md
- docs/development/transaction-strategy.md
- docs/development/payment-entity-refactoring-plan.md
- docs/development/entity-refactoring-guidelines.md
- docs/development/internationalization-design.md

Domain

- docs/domain/studio-domain.md
- docs/domain/instructor-domain.md
- docs/domain/member-domain.md
- docs/domain/schedule-domain.md
- docs/domain/booking-domain.md
- docs/domain/membership-domain.md
- docs/domain/attendance-domain.md
- docs/domain/payment-domain.md
- docs/domain/payment-operation-domain.md
- docs/domain/payment-webhook-log-domain.md
- docs/domain/notification-domain.md
- docs/domain/refresh-token-domain.md

---

# Summary

LessonRing Backend 문서는 다음 영역으로 구성된다.

- Architecture
- API
- Domain
- Development
- Git

각 문서는 Backend 시스템의 설계와 개발 규칙을 정의하며  
프로젝트의 **일관성, 확장성, 유지보수성**을 확보하는 것을 목표로 한다.

---

# ERD 문서

아키텍처 ERD 관련 추가 문서

- docs/architecture/erd.md
- docs/architecture/erd-ddl.md
- docs/architecture/erd-flyway-migration.md
- docs/architecture/erd-dbml.md

---

# Migration 문서

DB migration 및 baseline 관련 참고 경로

- src/main/resources/db/migration
- src/main/resources/db/baseline/V1__init_schema.sql
- docs/architecture/erd-flyway-migration.md
