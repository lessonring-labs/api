# 시스템 아키텍처

LessonRing Backend의 현재 시스템 아키텍처를 코드 기준으로 정리한 문서다.  
기존 계획성 설명보다 실제 구현 상태를 우선하며, 2026-03 기준 코드베이스를 반영한다.

---

## 1. 개요

LessonRing Backend는 레슨/스튜디오 운영을 위한 API 서버이며 다음 기능을 제공한다.

- 인증 및 JWT 토큰 발급
- 회원 관리
- 이용권 관리
- 수업 일정 관리
- 예약 관리
- 출석 관리
- 결제 승인/환불/웹훅 처리
- 알림 조회 및 읽음 처리
- 분석 API 확장 기반

현재 시스템은 **Spring Boot 기반 모듈형 모놀리식(Modular Monolith)** 구조다.

---

## 2. 상위 구조

현재 운영 관점의 상위 구조는 다음과 같다.

```text
Client
  -> HTTP/JSON API
Spring Boot Backend
  -> PostgreSQL
  -> Redis
  -> Toss Payments
```

구성 요소별 역할은 다음과 같다.

- Client: Web 또는 추후 Mobile 클라이언트
- Spring Boot Backend: 인증, 도메인 로직, 트랜잭션, 외부 연동 처리
- PostgreSQL: 주요 업무 데이터 저장소
- Redis: 분산 락 및 상태 충돌 방지
- Toss Payments: 결제 승인/취소/상태 검증

Nginx, Kubernetes, Kafka, 외부 메시징 파이프라인은 현재 코드에 핵심 런타임으로 포함되어 있지 않으며 일부는 향후 확장 대상으로 보는 것이 정확하다.

---

## 3. 애플리케이션 구조

백엔드는 도메인 중심 계층 구조를 따른다.

```text
api
application
domain
infrastructure
common
```

각 계층의 책임은 다음과 같다.

- `api`: Controller, Request/Response DTO, HTTP 진입점
- `application`: 유스케이스 조합, 트랜잭션, 도메인 간 오케스트레이션
- `domain`: 엔티티, enum, 도메인 상태 전이, 저장소 인터페이스
- `infrastructure`: DB 조회 구현, PG/Webhook 등 외부 연동
- `common`: 보안, 예외, 응답 포맷, 이벤트, 락, 공통 설정

현재 패키지 기준 주요 모듈은 다음과 같다.

- `auth`
- `studio`
- `instructor`
- `member`
- `membership`
- `schedule`
- `booking`
- `attendance`
- `payment`
- `notification`
- `analytics`
- `integration`
- `common`

---

## 4. API 아키텍처

모든 주요 API는 REST 스타일과 `/api/v1` 버전 prefix를 사용한다.

대표 엔드포인트:

- `/api/v1/auth`
- `/api/v1/members`
- `/api/v1/memberships`
- `/api/v1/schedules`
- `/api/v1/bookings`
- `/api/v1/attendances`
- `/api/v1/payments`
- `/api/v1/payments/webhook`
- `/api/v1/notifications`
- `/api/v1/analytics`

공통 응답 포맷은 `ApiResponse`를 사용한다.

Swagger/OpenAPI 노출 경로:

- `/api-docs`
- `/swagger-ui`

---

## 5. 인증 및 보안 구조

인증은 **JWT 기반 Stateless 인증**이다.

구성 요소:

- Access Token
- Refresh Token
- `JwtAuthenticationFilter`
- `SecurityConfig`

보안 정책 요약:

- 인증이 필요한 API는 Bearer Token 사용
- 로그인/토큰 재발급은 비인증 허용
- Swagger 경로는 비인증 허용
- 세션은 사용하지 않고 `STATELESS` 정책 적용
- `Spring Security` 기반 필터 체인 사용

현재 공개 허용 경로:

- `/api/v1/auth/login`
- `/api/v1/auth/refresh`
- `/swagger-ui/**`
- `/api-docs/**`
- `/v3/api-docs/**`
- `/actuator/health`

---

## 6. 데이터 저장 구조

### 6.1 주 데이터 저장소

- PostgreSQL
- Spring Data JPA
- Hibernate
- Querydsl
- Flyway

구현 특징:

- `ddl-auto=validate`로 스키마 검증만 수행
- Flyway로 스키마 버전 관리
- JPA `open-in-view=false`
- Hibernate JDBC timezone은 `Asia/Seoul`

주요 마이그레이션 범위:

- 스튜디오, 강사, 회원, 이용권
- 수업 일정, 예약, 출석
- 결제, 알림, 리프레시 토큰
- 결제 웹훅 로그, 결제 작업 이력
- 제약조건 및 인덱스

### 6.2 캐시/락 저장소

- Redis
- Redisson

현재 Redis의 핵심 용도는 캐시보다 **분산 락**이다.

실제 구현 예:

- 예약/결제 상태 충돌 방지
- 동일 결제에 대한 승인/환불/웹훅 동시 처리 제어

---

## 7. 결제 아키텍처

결제 모듈은 현재 시스템에서 가장 명확한 외부 연동 구조를 가진다.

핵심 구성:

- `PaymentService`: 결제 생성, 완료, 취소, 환불
- `PaymentPgService`: PG 승인 처리
- `PaymentWebhookService`: PG 웹훅 수신 처리
- `PgClient` / `TossPaymentsClient`: PG 연동
- `PaymentOperationService`: 승인/환불 멱등 처리
- `PaymentStateLockManager`: 결제 단위 분산 락

결제 처리 방식:

1. 내부 결제 엔티티를 `READY` 상태로 생성한다.
2. 승인 API 호출 시 PG 승인 요청을 보낸다.
3. 승인 성공 시 이용권을 생성하고 결제를 `COMPLETED`로 전환한다.
4. 환불 시 PG 취소 요청과 내부 상태 전이를 함께 처리한다.
5. 웹훅 수신 시 PG 상태 검증 후 결제 상태를 동기화한다.

보강 포인트:

- `PaymentOperation`으로 승인/환불 멱등성 보장
- `PaymentWebhookEvent`, `PaymentWebhookLog`로 웹훅 중복/처리 추적
- `RedisLockManager` 기반 결제 상태 동시성 제어

---

## 8. 이벤트 아키텍처

현재는 내부 애플리케이션 이벤트 기반 구조다.

구성 요소:

- `DomainEvent`
- `DomainEventPublisher`
- `@EventListener` 기반 핸들러

현재 사용 중인 주요 도메인 이벤트:

- `BookingCreatedEvent`
- `BookingCanceledEvent`
- `MembershipUsedEvent`
- `PaymentCompletedEvent`
- `PaymentCanceledEvent`

대표 소비자:

- `NotificationEventHandler`

현재 이벤트는 주로 다음 용도로 사용된다.

- 예약/결제/이용권 사용 시 알림 엔티티 생성
- 도메인 간 직접 결합 완화

Kafka producer 패키지가 일부 존재하지만, 현재 `build.gradle` 기준으로 Kafka 런타임 의존성은 핵심 구성에 포함되어 있지 않다. 따라서 현재 문서에서는 내부 이벤트 중심 구조로 보는 것이 맞다.

---

## 9. 도메인 간 상호작용 구조

주요 업무 흐름은 다음과 같이 연결된다.

### 9.1 예약 흐름

```text
Member
  -> Membership 검증
  -> Schedule 검증
  -> Booking 생성
  -> BookingCreatedEvent 발행
  -> Notification 생성
```

### 9.2 출석 흐름

```text
Booking 확인
  -> Membership 차감
  -> Booking 상태 ATTENDED 변경
  -> Attendance 생성
  -> MembershipUsedEvent 발행
  -> Notification 생성
```

### 9.3 결제 흐름

```text
Payment 생성(READY)
  -> PG 승인
  -> Membership 생성
  -> Payment COMPLETED
  -> PaymentCompletedEvent 발행
  -> Notification 생성
```

### 9.4 환불 흐름

```text
Payment COMPLETED 확인
  -> PG 취소
  -> 미래 예약 일괄 취소
  -> Membership REFUNDED
  -> Payment 상태 변경
  -> PaymentCanceledEvent 발행
```

---

## 10. 운영/관찰 가능성

현재 코드 기준 운영 지원 요소는 다음과 같다.

- Swagger/OpenAPI 문서화
- 전역 예외 처리(`GlobalExceptionHandler`)
- 공통 에러 코드(`ErrorCode`)
- 웹훅/결제 작업 이력 저장
- `actuator/health` 헬스 체크 허용

반면 다음 항목은 문서화된 계획 또는 패키지 흔적은 있으나, 현재 핵심 런타임으로 완성된 상태라고 보긴 어렵다.

- Prometheus/Scouter 기반 모니터링
- ELK/Loki 기반 로그 파이프라인
- Kafka 기반 비동기 메시징
- Kubernetes 기반 배포 오케스트레이션

---

## 11. 배포 관점 정리

현재 코드에서 강하게 전제되는 실행 환경은 다음과 같다.

- Java 21
- Spring Boot 3.5.9
- PostgreSQL
- Redis
- 환경변수 기반 설정 주입

프로파일 구조:

- `application.yml`: 공통 설정
- `application-local.yml`: 로컬 개발
- `application-dev.yml`: 개발 서버

기본 프로파일은 `local`이다.

---

## 12. 현재 아키텍처 판단

현재 LessonRing Backend는 다음과 같이 요약할 수 있다.

- 구조: 도메인 중심 모듈형 모놀리식
- 인터페이스: REST API + JWT 인증
- 저장소: PostgreSQL + Redis
- 외부 연동: Toss Payments, Webhook
- 내부 통신: Spring 애플리케이션 이벤트
- 데이터 일관성 보강: 분산 락 + 멱등 키 + 작업 이력 테이블

즉, 이 시스템은 단순 CRUD 백엔드가 아니라 **예약/출석/결제 상태 전이와 동시성 제어를 포함한 운영형 백엔드**로 보는 것이 맞다.

---

## 13. 향후 확장 방향

현재 구조를 기준으로 자연스러운 확장 방향은 다음과 같다.

- 외부 알림 채널 연동(SMS, 카카오, 푸시)
- Analytics API 구체화
- Kafka 기반 비동기 이벤트 분리
- 스튜디오 기준 멀티 테넌시 강화
- 결제/예약 배치 및 운영성 지표 고도화
- 배포 파이프라인과 관측 도구 정식 도입
