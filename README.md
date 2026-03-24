# LessonRing Backend

LessonRing Backend는 **레슨 스튜디오 운영을 위한 관리 플랫폼 API 서버**이다.  
회원 관리, 이용권 관리, 수업 스케줄, 예약, 출석, 결제, 알림, 데이터 분석 기능을 제공한다.

---

# Overview

LessonRing은 레슨 스튜디오 운영에 필요한 핵심 기능을 통합 제공하는 Backend 시스템이다.

주요 기능

```
회원 관리 (Member Management)
이용권 관리 (Membership Management)
수업 스케줄 관리 (Schedule Management)
예약 관리 (Booking Management)
출석 관리 (Attendance Management)
결제 관리 (Payment Management)
알림 시스템 (Notification)
데이터 분석 (Analytics)
```

---

# Architecture

LessonRing Backend는 다음 아키텍처 기반으로 설계되었다.

```
Modular Monolith
Domain Oriented Architecture
Event Driven Architecture
C4 Model
```

Backend Layer 구조

```
api
application
domain
infrastructure
common
```

각 계층 역할

| Layer | Description |
|------|-------------|
| api | Controller 및 HTTP API 처리 |
| application | 서비스 로직 및 비즈니스 흐름 |
| domain | 핵심 비즈니스 모델 및 도메인 규칙 |
| infrastructure | DB / 외부 시스템 연동 |
| common | 공통 모듈 |

---

# Project Structure

Backend 패키지 구조

```
src/main/java/com/lessonring/api
```

도메인 모듈

```
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
```

각 도메인은 다음 구조를 가진다.

```
domain-module
├── api
├── application
├── domain
└── infrastructure
```

---

# Documentation

프로젝트 문서는 `docs` 폴더에서 관리된다.

```
docs
├── README.md
├── api
├── architecture
├── development
├── domain
└── git.md
```

주요 문서

| 영역 | 설명 |
|-----|------|
| Architecture | 시스템 아키텍처 |
| API | API 규칙 및 설계 |
| Domain | 도메인 설계 |
| Development | 개발 규칙 및 가이드 |
| Git | Git 협업 규칙 |

신규 개발자는 다음 문서를 먼저 읽는 것을 권장한다.

```
docs/README.md
```

---

# Tech Stack

## Language

```
Java
```

## Framework

```
Spring Boot
```

## Build

```
Gradle
```

## API

```
REST API
Feign Client
```

## Validation

```
Spring Boot Validation
```

## API Documentation

```
SpringDoc OpenAPI
Swagger
```

## Database

```
PostgreSQL
```

## Cache

```
Redis
```

## Messaging

```
Kafka
```

## Authentication

```
JWT
Kakao OAuth Login
```

## ORM

```
Spring Data JPA
QueryDSL
```

## Migration

```
Flyway
```

## Mapping

```
MapStruct
```

## Automation

```
n8n
Webhook
```

## Concurrency

```
Redis Distributed Lock
```

## Monitoring

```
Scouter
OpenLens
```

## Analytics

```
Metabase
```

## Infrastructure

```
Docker
Kubernetes (k3s)
Nginx Ingress
```

## CI/CD

```
GitHub Actions
```

---

# Getting Started

로컬에서 서버 실행

```
./gradlew bootRun
```

서버 기본 주소

```
http://localhost:8080
```

---

# Development Guide

개발 규칙 및 설계 문서는 아래 위치에서 확인할 수 있다.

```
docs/README.md
```

---

# Database Migration

Flyway migration 파일은 아래 경로에서 관리한다.

```text
src/main/resources/db/migration
```

정리 규칙:

- `V1`부터 `V12`까지: 테이블 생성 및 스키마 정의
- `V13`: 인덱스와 제약조건만 관리

현재 migration 순서:

- `V1__create_studio.sql`
- `V2__create_instructor.sql`
- `V3__create_member.sql`
- `V4__create_membership.sql`
- `V5__create_schedule.sql`
- `V6__create_booking.sql`
- `V7__create_attendance.sql`
- `V8__create_payment.sql`
- `V9__create_notification.sql`
- `V10__create_refresh_token.sql`
- `V11__create_payment_webhook_log.sql`
- `V12__create_payment_operation.sql`
- `V13__add_constraints_and_indexes.sql`

설명:

- `payment` 관련 후속 스키마 변경은 `V8__create_payment.sql`에 흡수했다.
- `payment_webhook_log` 관련 후속 스키마 변경은 `V11__create_payment_webhook_log.sql`에 흡수했다.
- 인덱스와 unique 제약 정의는 `V13__add_constraints_and_indexes.sql`에 모아뒀다.
- 기존 DB에 이전 migration 이력이 이미 적용돼 있다면, re-baseline 계획 없이 파일명이나 순서를 바꾸면 안 된다.
- baseline 초기 스키마 파일 위치: `src/main/resources/db/baseline/V1__init_schema.sql`

baseline 사용 기준:

- `src/main/resources/db/baseline/V1__init_schema.sql`은 새 데이터베이스를 처음부터 구성할 때만 사용한다.
- 현재 활성 Flyway `migration` 디렉터리에는 이미 `V1`이 있으므로, baseline 파일을 그대로 옮겨 넣으면 안 된다.
- 기존 데이터베이스는 현재 `V1`부터 `V13`까지의 migration 이력을 그대로 유지한다.
- 기존 환경을 baseline 방식으로 전환하려면 별도의 re-baseline 계획이 필요하다.

---

# License

Internal Project
