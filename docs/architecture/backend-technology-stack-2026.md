# LessonRing Backend Technology Stack 2026

이 문서는 LessonRing Backend의 2026 기준 표준 기술 스택과 기술 선택 원칙을 정의한다.

LessonRing Backend는 레슨 스튜디오 운영을 위한 API 서버이며, 다음 도메인을 중심으로 동작한다.

- 인증
- 스튜디오
- 강사
- 회원
- 이용권
- 수업 일정
- 예약
- 출석
- 결제
- 알림
- 분석

이 문서의 목적은 단순히 현재 의존성을 나열하는 것이 아니라, LessonRing 백엔드가 어떤 업무를 처리하기 위해 어떤 기술 조합을 채택했는지 설명하는 데 있다.

---

# 1. LessonRing Backend의 시스템 특성

LessonRing Backend는 일반 CRUD 서버보다 운영 정합성이 중요한 도메인을 다룬다.

## 1.1 운영형 SaaS 특성

주요 업무는 다음과 같다.

- 회원 등록 및 상태 관리
- 이용권 생성 및 사용 관리
- 수업 일정 운영
- 예약 생성 및 취소
- 출석 및 no-show 처리
- 결제 승인 및 환불
- 알림 발송
- 운영 지표 집계

각 기능은 독립적인 것처럼 보여도 실제로는 서로 강하게 연결된다.

예시:

- 결제가 완료되면 이용권이 생성된다.
- 예약이 생성되면 이용권 검증이 필요하다.
- 예약 취소 또는 no-show 처리는 이용권 사용 상태에 영향을 준다.
- 출석 데이터는 운영 지표 집계와 연결된다.

## 1.2 정합성 우선 시스템

LessonRing은 다음 이유로 정합성이 최우선이다.

- 중복 예약은 운영 사고로 이어진다.
- 잘못된 이용권 차감은 고객 CS로 이어진다.
- 결제 승인/환불 오류는 직접 금전 손실로 이어진다.

따라서 기술 스택은 빠른 개발보다 다음을 우선한다.

- 트랜잭션 안정성
- 타입과 도메인 모델의 명확성
- 데이터베이스 제약과 인덱스 관리
- 동시성 제어
- 운영 추적 가능성

---

# 2. 아키텍처 목표

LessonRing Backend의 기술 선택 목표는 다음과 같다.

- 운영형 비즈니스 도메인을 안정적으로 모델링할 수 있을 것
- 단일 서비스 안에서 빠르게 기능 확장할 수 있을 것
- PostgreSQL 기반의 강한 트랜잭션 정합성을 활용할 수 있을 것
- 결제, 예약 같은 민감 도메인에서 동시성 제어가 가능할 것
- 문서화, 테스트, 운영 모니터링이 가능한 구조일 것

---

# 3. 최종 표준 기술 스택

## 3.1 Core Platform

| 영역 | 표준 기술 | LessonRing 내 역할 |
|-----|-----|-----|
| Language | Java 21 | 백엔드 주 개발 언어 |
| Framework | Spring Boot 3.5.9 | API 서버, 설정, DI, 실행 환경 |
| Build Tool | Gradle Wrapper | 빌드, 테스트, 의존성 관리 |

## 3.2 API & Web Layer

| 영역 | 표준 기술 | LessonRing 내 역할 |
|-----|-----|-----|
| HTTP API | Spring Web | REST API, JSON 요청/응답 처리 |
| Validation | Spring Validation | 요청 DTO 검증 |
| API Docs | Springdoc OpenAPI / Swagger UI | API 명세 노출 및 테스트 지원 |

## 3.3 Security

| 영역 | 표준 기술 | LessonRing 내 역할 |
|-----|-----|-----|
| Security Framework | Spring Security | 인증/인가 및 보안 필터 체인 |
| Token | JWT | Access Token / Refresh Token 기반 인증 |

## 3.4 Data Layer

| 영역 | 표준 기술 | LessonRing 내 역할 |
|-----|-----|-----|
| Primary Database | PostgreSQL | 주 데이터 저장소 |
| ORM | Spring Data JPA / Hibernate | 엔터티 매핑 및 데이터 접근 |
| Query Layer | Querydsl | 복잡한 조회 쿼리 및 검색 조건 처리 |
| Migration | Flyway | DB 스키마 변경 이력 관리 |

## 3.5 Cache / Lock

| 영역 | 표준 기술 | LessonRing 내 역할 |
|-----|-----|-----|
| Cache / Store | Redis | 캐시 및 분산 제어용 저장소 |
| Distributed Lock | Redisson | 예약/결제 동시성 제어 |

## 3.6 External Integration

| 영역 | 표준 기술 | LessonRing 내 역할 |
|-----|-----|-----|
| Payment Gateway | Toss Payments | 결제 승인, 취소, 환불 연동 |
| Integration Style | REST API / Webhook | 외부 시스템 연동 및 이벤트 수신 |

## 3.7 Quality & Test

| 영역 | 표준 기술 | LessonRing 내 역할 |
|-----|-----|-----|
| Unit / Integration Test | Spring Boot Test | 서비스, API, 통합 테스트 |
| Assertion | AssertJ | 테스트 가독성 향상 |
| Security Test | Spring Security Test | 인증/인가 테스트 |
| Productivity | Lombok | 보일러플레이트 감소 |

---

# 4. 현재 적용 스택 상세

## 4.1 Java 21

LessonRing Backend는 Java 21을 표준 언어 버전으로 사용한다.

선정 이유:

- 장기 지원 버전 기반 안정성
- Spring Boot 3 계열과의 호환성
- 최신 언어 기능을 활용한 코드 가독성 향상

적합성:

- 도메인 모델과 서비스 계층을 명확하게 표현하기 좋다.
- 대규모 팀 개발에서 예측 가능한 코드 구조를 유지하기 쉽다.

## 4.2 Spring Boot 3.5.9

Spring Boot는 LessonRing 백엔드의 실행 표준이다.

선정 이유:

- 웹, 보안, 데이터 접근, 검증, 설정 관리가 통합되어 있다.
- 운영형 API 서버에 필요한 표준 구성이 이미 잘 정리되어 있다.
- 팀 생산성과 유지보수성 측면에서 검증된 선택지다.

LessonRing에서의 활용:

- REST API 제공
- 보안 설정
- 트랜잭션 관리
- 환경별 설정 분리
- 테스트 실행 기반 제공

## 4.3 Spring Web

LessonRing은 REST API 중심 구조를 사용한다.

주요 역할:

- 회원, 예약, 결제, 출석, 이용권 API 제공
- JSON 요청/응답 직렬화
- 예외 응답 변환

적합한 이유:

- 운영 화면과 모바일 앱 모두 REST API와 잘 맞는다.
- 현재 제품 규모에서 GraphQL보다 구조가 단순하고 명확하다.

## 4.4 Spring Validation

요청 DTO는 API 진입점에서 우선 검증한다.

적용 이유:

- 잘못된 요청을 서비스 로직 전에 차단한다.
- 도메인 제약 이전에 형식 오류를 걸러낸다.

예시:

- 필수값 누락
- 길이 제한 위반
- 형식 오류

## 4.5 Spring Security + JWT

LessonRing 인증 구조는 JWT 기반이다.

역할:

- 인증 필터 체인 관리
- Access Token 검증
- Refresh Token 기반 재발급 흐름 지원

적합한 이유:

- 웹과 모바일 클라이언트 모두에 적용하기 쉽다.
- 세션 서버 의존도를 낮춘다.
- 운영형 API 구조에서 stateless 인증 모델을 유지할 수 있다.

주의점:

- Refresh Token 저장 및 만료 관리가 중요하다.
- 권한 모델이 확장될 경우 role/authority 전략을 명확히 해야 한다.

## 4.6 PostgreSQL

PostgreSQL은 LessonRing의 주 데이터 저장소다.

선정 이유:

- 강한 트랜잭션 보장
- 관계형 모델링 적합성
- 인덱스, 제약조건, 쿼리 최적화 기능이 우수하다.

LessonRing 적합성:

- 회원, 예약, 결제, 이용권처럼 관계가 명확한 도메인에 적합하다.
- 정합성이 중요한 업무 데이터를 다루기에 적절하다.

## 4.7 Spring Data JPA / Hibernate

도메인 모델 중심 개발을 위해 JPA를 사용한다.

적합한 이유:

- 엔터티와 비즈니스 규칙을 코드로 함께 유지하기 쉽다.
- 리포지토리 패턴과 잘 맞는다.
- Spring 생태계와의 통합이 강하다.

LessonRing에서의 역할:

- `Member`
- `Membership`
- `Schedule`
- `Booking`
- `Attendance`
- `Payment`
- `Notification`

위 도메인 엔터티의 영속성 관리

운영 원칙:

- `open-in-view: false`
- `ddl-auto: validate`

## 4.8 Querydsl

LessonRing은 조회성 화면 요구가 많다.

예시:

- 회원 검색
- 예약 목록 필터링
- 강사별 스케줄 조회
- 운영 통계 집계

이런 쿼리는 단순 JPA 메서드 네이밍으로는 유지보수가 어렵다. Querydsl은 타입 안정성을 유지하면서 복잡한 조건 조회를 구현하는 데 적합하다.

## 4.9 Flyway

DB 스키마는 코드와 함께 버전 관리해야 한다.

선정 이유:

- 운영 DB 변경 이력을 명시적으로 남길 수 있다.
- 배포 과정에서 스키마 변경을 관리하기 쉽다.
- 신규 환경 구성과 기존 환경 업그레이드 기준을 통일할 수 있다.

LessonRing 적합성:

- 예약, 결제, 이용권처럼 데이터 구조 변경이 민감한 시스템에서 필수적이다.

## 4.10 Redis + Redisson

LessonRing은 단순 캐시보다 동시성 제어 목적이 더 중요하다.

주요 적용 포인트:

- 예약 동시성 제어
- 결제 상태 변경 동시성 제어
- 분산 환경에서의 잠금 처리

왜 필요한가:

- 같은 수업에 대한 동시 예약 충돌을 줄여야 한다.
- 결제 완료/환불 처리 중복 실행을 방지해야 한다.

Redisson은 Redis 기반 분산 락을 비교적 안정적으로 적용할 수 있는 선택지다.

## 4.11 Springdoc OpenAPI / Swagger UI

LessonRing은 Admin Web, Mobile, 외부 연동까지 API 소비 주체가 많다.

따라서 API 문서화는 선택이 아니라 필수다.

역할:

- API 명세 자동 노출
- 클라이언트와의 계약 확인
- 개발 및 테스트 편의성 향상

## 4.12 Toss Payments

LessonRing 결제 도메인의 외부 PG 연동 표준이다.

역할:

- 결제 승인
- 결제 취소
- 환불 연동
- 외부 응답 기반 상태 동기화

주의점:

- 외부 응답 신뢰성에 의존하므로 멱등성과 로깅이 중요하다.
- 내부 Payment 상태 전이와 PG 상태 전이를 분리해서 봐야 한다.

---

# 5. LessonRing 도메인과 기술 스택의 연결

## 5.1 Member / Auth

핵심 기술:

- Spring Web
- Spring Security
- JWT
- JPA

주요 이유:

- 인증과 회원 도메인은 API 안정성과 보안 경계가 중요하다.

## 5.2 Membership

핵심 기술:

- JPA
- PostgreSQL
- Flyway

주요 이유:

- 잔여 횟수, 기간, 상태 같은 비즈니스 규칙을 명확히 관리해야 한다.

## 5.3 Schedule / Booking / Attendance

핵심 기술:

- JPA
- Querydsl
- Redis
- Redisson

주요 이유:

- 조회 요구와 동시성 제어 요구가 동시에 존재한다.
- 예약 가능 상태와 출석 처리 정합성이 중요하다.

## 5.4 Payment

핵심 기술:

- PostgreSQL
- JPA
- Redis / Redisson
- Toss Payments
- Webhook

주요 이유:

- 결제는 트랜잭션 안정성과 외부 연동 안정성이 동시에 필요하다.
- 멱등성, 상태 추적, 감사 로그 성격의 데이터 관리가 중요하다.

## 5.5 Analytics

핵심 기술:

- Querydsl
- PostgreSQL

주요 이유:

- 운영 지표 집계를 위한 조회 최적화가 필요하다.

---

# 6. 권장 백엔드 아키텍처 스타일

LessonRing은 현재 다음 구조를 기준으로 한다.

- Modular Monolith
- Domain-Oriented Architecture
- REST API 중심 구조

패키지 표준:

```text
com.lessonring.api
├─ auth
├─ member
├─ membership
├─ schedule
├─ booking
├─ attendance
├─ payment
├─ notification
├─ studio
├─ instructor
├─ analytics
├─ integration
└─ common
```

각 도메인 모듈은 다음 계층을 기본으로 한다.

```text
api
application
domain
infrastructure
```

이 구조를 채택하는 이유:

- 도메인 경계를 코드에서 명확히 유지할 수 있다.
- 기능 확장 시 패키지 응집도를 유지하기 쉽다.
- 완전한 마이크로서비스 이전에도 충분히 큰 제품을 감당할 수 있다.

---

# 7. 테스트 표준

LessonRing Backend에서 반드시 검증해야 하는 영역은 다음과 같다.

- 인증 토큰 발급 및 검증
- 예약 생성/취소/중복 방지
- 결제 승인/환불/멱등성
- Webhook 처리
- 출석 및 no-show 처리
- API 권한 제어

권장 테스트 계층:

- 단위 테스트
- 서비스 통합 테스트
- 컨트롤러 테스트
- 외부 연동 mock 기반 테스트

현재 표준 도구:

- `spring-boot-starter-test`
- `AssertJ`
- `spring-security-test`

---

# 8. 운영 및 품질 원칙

## 8.1 예외 처리

예외는 공통 응답 형식으로 변환해야 한다.

목표:

- 클라이언트가 예측 가능한 에러 구조를 받도록 한다.
- 운영 시 원인 추적이 가능하도록 한다.

## 8.2 DB 제약 우선

애플리케이션 검증만으로 정합성을 보장하지 않는다.

원칙:

- unique 제약
- foreign key
- index
- 상태 전이 규칙

은 가능한 범위에서 DB와 코드 양쪽에서 관리한다.

## 8.3 동시성 제어

다음 영역은 반드시 동시성 사고를 전제로 설계한다.

- 예약
- 결제 승인
- 환불
- 이용권 차감

## 8.4 문서화

백엔드 기술 스택 문서는 다음 문서와 함께 유지한다.

- `docs/architecture/system-architecture.md`
- `docs/architecture/domain-architecture.md`
- `docs/architecture/technology-stack.md`
- `docs/api/api-guidelines.md`
- `docs/domain/*`

---

# 9. 향후 확장 후보 기술

현재 코드베이스에는 흔적이 있거나, 향후 도입 후보로 고려할 수 있는 기술이 있다.

## 9.1 Kafka

적용 가능 영역:

- 알림 이벤트 비동기 처리
- 분석 이벤트 적재
- 외부 시스템 연동

도입 시점:

- 이벤트량 증가
- 비동기 후처리 분리 필요성 증가

## 9.2 Feign 또는 HTTP Client 표준화

적용 가능 영역:

- OAuth 공급자 연동
- 외부 서비스 호출 표준화

도입 이유:

- 외부 API 연동 코드 일관성 확보

## 9.3 Observability Stack

후보:

- Prometheus
- Grafana
- Scouter
- ELK / Loki
- Sentry

도입 목적:

- 애플리케이션 메트릭 수집
- 에러 추적
- 운영 대시보드 구성

---

# 10. LessonRing Backend 최종 권고안

LessonRing Backend의 표준 기술 조합은 다음과 같이 정리한다.

- `Java 21`
- `Spring Boot 3.5.9`
- `Gradle`
- `Spring Web`
- `Spring Validation`
- `Spring Security`
- `JWT`
- `PostgreSQL`
- `Spring Data JPA / Hibernate`
- `Querydsl`
- `Flyway`
- `Redis`
- `Redisson`
- `Springdoc OpenAPI / Swagger UI`
- `Toss Payments`
- `Spring Boot Test`
- `AssertJ`
- `Spring Security Test`

이 조합은 LessonRing의 실제 운영 요구를 만족한다.

- 관계형 도메인 중심 모델링
- 높은 정합성 요구
- 예약/결제 동시성 제어
- 운영형 API 서버 구조
- 클라이언트와의 안정적인 계약 유지

---

# 11. 결론

LessonRing Backend의 기술 스택은 최신 트렌드 중심 조합이 아니라, 스튜디오 운영 도메인을 안전하게 처리하기 위한 실용적 조합이다.

따라서 LessonRing Backend 표준은 다음과 같이 정의한다.

`Java 21 + Spring Boot`를 중심으로, `PostgreSQL`, `JPA`, `Querydsl`, `Flyway`, `Redis`, `Redisson`, `Spring Security`, `JWT`, `OpenAPI`, `Toss Payments`를 결합한 정합성 중심 운영형 백엔드 아키텍처를 표준으로 채택한다.
