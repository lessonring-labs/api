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

현재 사용 중인 기술과 도입 예정 기술을 구분해서 관리한다.

상세 기술 문서:

```text
docs/architecture/technology-stack.md
```

## 현재 사용 중인 기술

### Language / Runtime

- `Java 21`: 애플리케이션 실행 언어 및 런타임

### Framework / Build

- `Spring Boot 3.5.9`: 백엔드 애플리케이션 프레임워크
- `Gradle`: 빌드 및 의존성 관리

### API / Web

- `Spring Web`: REST API와 웹 요청 처리
- `Spring Validation`: 요청 DTO 검증
- `Springdoc OpenAPI`: OpenAPI 스펙 생성
- `Swagger UI`: API 문서 조회 화면

### Security

- `Spring Security`: 인증/인가와 보안 필터 체인
- `JWT`: access token, refresh token 발급 및 검증

### Database

- `PostgreSQL`: 주 데이터베이스
- `Spring Data JPA`: 리포지토리 기반 데이터 접근
- `Hibernate`: JPA 구현체
- `Querydsl`: 타입 세이프 쿼리 작성
- `Flyway`: DB migration 관리

### Cache / Lock

- `Redis`: 캐시 및 키 기반 저장소
- `Redisson`: Redis 기반 분산 락 구현
- `Redis Distributed Lock`: 예약/결제 동시성 제어

### External Integration

- `Toss Payments`: 결제 승인/취소 PG 연동
- `Webhook`: 외부 결제 이벤트 수신 처리

### Productivity / Test

- `Lombok`: 반복 보일러플레이트 코드 축소
- `AssertJ`: 테스트 assertion 지원
- `Spring Boot Test`: 통합 테스트 기반 제공
- `Spring Security Test`: 보안 테스트 지원

## 도입 예정 또는 준비 중인 기술

현재 코드베이스에 TODO 또는 플레이스홀더는 있으나, 실사용 스택으로 확정하지 않은 항목이다.

### Integration / Messaging

- `Kafka`: 이벤트 발행 및 비동기 메시징
- `Feign`: 외부 HTTP API 선언형 클라이언트
- `Kakao OAuth Login`: 카카오 로그인 연동

### Mapping / Automation

- `MapStruct`: DTO 매핑 자동화
- `n8n`: 운영 자동화 및 워크플로우 처리

### Observability / Analytics

- `Metabase`: 지표 조회와 데이터 분석
- `Scouter`: 애플리케이션 모니터링
- `OpenLens`: 쿠버네티스 클러스터 관찰

### Infrastructure / Delivery

- `Docker`: 컨테이너 기반 실행 환경
- `Kubernetes (k3s)`: 경량 쿠버네티스 운영 환경
- `Nginx Ingress`: 인그레스 및 트래픽 라우팅
- `GitHub Actions`: CI/CD 파이프라인 자동화

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

---

# 설정 파일 구조

Spring 설정 파일은 공통 설정과 프로필별 설정으로 분리한다.

- `src/main/resources/application.yml`
  - 모든 환경에서 공통으로 사용하는 기본 설정
- `src/main/resources/application-local.yml`
  - 로컬 개발 환경 전용 설정
- `src/main/resources/application-dev.yml`
  - 개발 서버 환경 전용 설정

정리 원칙:

- 공통으로 유지되는 값은 `application.yml`에 둔다.
- DB, Redis, 외부 연동 키처럼 환경마다 달라지는 값은 프로필 파일로 분리한다.
- `dev` 환경에서는 가능한 한 환경변수로 값을 주입한다.

실행 예시:

```bash
./gradlew bootRun
```

기본 실행은 `local` 프로필을 사용한다.

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

`dev` 프로필 실행 시 필요한 대표 환경변수:

```bash
DB_URL=jdbc:postgresql://localhost:5432/lessonring
DB_USERNAME=devyn
DB_PASSWORD=
REDIS_HOST=localhost
REDIS_PORT=6379
PG_TOSS_SECRET_KEY=test_sk_xxxxx
PG_TOSS_BASE_URL=https://api.tosspayments.com
JWT_SECRET=your-secret-key-your-secret-key-your-secret-key
JWT_ACCESS_TOKEN_EXPIRATION=3600000
JWT_REFRESH_TOKEN_EXPIRATION=1209600000
```
