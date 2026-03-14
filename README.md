# LessonRing Backend

Backend API for Lesson Studio Management Platform

---

# Overview

LessonRing은 레슨 스튜디오 운영을 위한 백엔드 시스템이다.

주요 기능

```
회원 관리
이용권 관리
수업 스케줄 관리
예약 관리
출석 관리
결제 관리
알림 시스템
데이터 분석
```

---

# Architecture

LessonRing Backend는 다음 아키텍처를 기반으로 설계되었다.

```
Modular Monolith
Event Driven Architecture
C4 Model
```

Core Layer Structure

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
common         → 공통 모듈
```

---

# Documentation

프로젝트 문서는 `docs` 폴더에서 관리된다.

```
docs/
```

주요 문서

```
docs/README.md
docs/architecture/core-domain.md
docs/architecture/api-architecture.md
docs/architecture/security-architecture.md
docs/development/backend-development-guide.md
docs/api/api-versioning.md
```

신규 개발자는 아래 문서를 먼저 읽는 것을 권장한다.

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

# Project Structure

```
src/main/java/com/lessonring/api

auth
member
membership
schedule
booking
attendance
payment
notification
```

레이어 구조

```
api
application
domain
infrastructure
common
```

---

# Getting Started

로컬에서 서버 실행

```
./gradlew bootRun
```

서버 기본 포트

```
http://localhost:8080
```

---

# Development Guide

개발 가이드는 아래 문서에서 확인할 수 있다.

```
docs/README.md
```

---

# License

Internal Project