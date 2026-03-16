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

# License

Internal Project