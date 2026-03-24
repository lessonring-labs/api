# 기술 스택

이 문서는 현재 LessonRing Backend 프로젝트에 실제 적용된 기술 스택을 코드와 설정 기준으로 정리한다.

기준:

- `build.gradle`에 선언된 의존성
- `src/main/java`의 실제 사용 코드
- `src/main/resources` 설정 파일

---

# 개요

현재 프로젝트는 다음 조합으로 구성되어 있다.

- Java 21
- Spring Boot 3.5.9
- Gradle 9 계열 Wrapper
- PostgreSQL
- Spring Data JPA / Hibernate
- Querydsl
- Flyway
- Spring Security + JWT
- Redis + Redisson
- Springdoc OpenAPI

---

# 백엔드 프레임워크

## Java

- 버전: `21`
- 근거: [`build.gradle`](/C:/wms/api/build.gradle#L8)

## Spring Boot

- 버전: `3.5.9`
- 근거: [`build.gradle`](/C:/wms/api/build.gradle#L1)

## 빌드 도구

- Gradle Wrapper 사용
- Wrapper 버전: `9.0.0`
- 근거: [`gradle-wrapper.properties`](/C:/wms/api/gradle/wrapper/gradle-wrapper.properties#L1)

---

# 웹 / API 계층

## Spring Web

- 의존성: `spring-boot-starter-web`
- 역할: REST API, MVC, JSON 요청/응답 처리
- 근거: [`build.gradle`](/C:/wms/api/build.gradle#L24)

## Validation

- 의존성: `spring-boot-starter-validation`
- 역할: 요청 DTO 검증
- 근거: [`build.gradle`](/C:/wms/api/build.gradle#L23)

## OpenAPI / Swagger UI

- 의존성: `springdoc-openapi-starter-webmvc-ui:2.8.15`
- 역할: API 문서 노출, Swagger UI 제공
- 관련 코드:
  - [`OpenApiConfig.java`](/C:/wms/api/src/main/java/com/lessonring/api/common/config/OpenApiConfig.java#L1)
  - [`SwaggerConfig.java`](/C:/wms/api/src/main/java/com/lessonring/api/common/swagger/SwaggerConfig.java#L1)
- 설정:
  - [`application.yml`](/C:/wms/api/src/main/resources/application.yml#L1)

---

# 보안

## Spring Security

- 의존성: `spring-boot-starter-security`
- 역할: 인증/인가, 보안 필터 체인
- 관련 코드:
  - [`SecurityConfig.java`](/C:/wms/api/src/main/java/com/lessonring/api/common/config/SecurityConfig.java#L1)
  - [`JwtAuthenticationFilter.java`](/C:/wms/api/src/main/java/com/lessonring/api/common/security/JwtAuthenticationFilter.java#L1)

## JWT

- 의존성:
  - `io.jsonwebtoken:jjwt-api:0.12.6`
  - `jjwt-impl`
  - `jjwt-jackson`
- 역할: access token / refresh token 생성 및 검증
- 관련 코드:
  - [`JwtTokenProviderImpl.java`](/C:/wms/api/src/main/java/com/lessonring/api/common/security/JwtTokenProviderImpl.java#L1)
  - [`AuthService.java`](/C:/wms/api/src/main/java/com/lessonring/api/auth/application/AuthService.java#L1)
- 설정:
  - [`application.yml`](/C:/wms/api/src/main/resources/application.yml#L1)

---

# 데이터베이스

## PostgreSQL

- 의존성: `org.postgresql:postgresql:42.7.10`
- 역할: 주 데이터베이스
- 관련 설정:
  - [`application-local.yml`](/C:/wms/api/src/main/resources/application-local.yml#L1)
  - [`application-dev.yml`](/C:/wms/api/src/main/resources/application-dev.yml#L1)

## Spring Data JPA / Hibernate

- 의존성: `spring-boot-starter-data-jpa`
- 역할: ORM, 엔티티 매핑, 리포지토리 기반 데이터 접근
- 설정:
  - `ddl-auto: validate`
  - `open-in-view: false`
- 근거:
  - [`build.gradle`](/C:/wms/api/build.gradle#L20)
  - [`application.yml`](/C:/wms/api/src/main/resources/application.yml#L1)

## Querydsl

- 의존성:
  - `com.querydsl:querydsl-jpa:5.1.0:jakarta`
  - `querydsl-apt`
- 역할: 타입 세이프 쿼리 작성
- 관련 코드:
  - [`QueryDslConfig.java`](/C:/wms/api/src/main/java/com/lessonring/api/common/config/QueryDslConfig.java#L1)
  - [`BookingQueryRepository.java`](/C:/wms/api/src/main/java/com/lessonring/api/booking/infrastructure/query/BookingQueryRepository.java#L1)
  - [`StudioQueryRepository.java`](/C:/wms/api/src/main/java/com/lessonring/api/studio/infrastructure/query/StudioQueryRepository.java#L1)
  - [`AnalyticsQueryRepository.java`](/C:/wms/api/src/main/java/com/lessonring/api/analytics/infrastructure/query/AnalyticsQueryRepository.java#L1)
  - [`InstructorQueryRepository.java`](/C:/wms/api/src/main/java/com/lessonring/api/instructor/infrastructure/query/InstructorQueryRepository.java#L1)

## Flyway

- 의존성:
  - `flyway-core`
  - `flyway-database-postgresql`
- 역할: DB schema migration 관리
- 관련 설정:
  - [`application.yml`](/C:/wms/api/src/main/resources/application.yml#L1)
- 관련 경로:
  - [`db/migration`](/C:/wms/api/src/main/resources/db/migration/V1__create_studio.sql#L1)
  - [`db/baseline/V1__init_schema.sql`](/C:/wms/api/src/main/resources/db/baseline/V1__init_schema.sql#L1)

---

# 캐시 / 분산 락

## Redis

- 의존성: `spring-boot-starter-data-redis`
- 역할: Redis 연결 설정
- 관련 설정:
  - [`application-local.yml`](/C:/wms/api/src/main/resources/application-local.yml#L1)
  - [`application-dev.yml`](/C:/wms/api/src/main/resources/application-dev.yml#L1)

## Redisson

- 의존성: `redisson-spring-boot-starter:3.52.0`
- 역할: 분산 락 구현
- 관련 코드:
  - [`RedissonConfig.java`](/C:/wms/api/src/main/java/com/lessonring/api/common/config/RedissonConfig.java#L1)
  - [`RedisLockManager.java`](/C:/wms/api/src/main/java/com/lessonring/api/common/lock/RedisLockManager.java#L1)
  - [`BookingRedisLockManager.java`](/C:/wms/api/src/main/java/com/lessonring/api/booking/infrastructure/lock/BookingRedisLockManager.java#L1)
  - [`PaymentRedisLockManager.java`](/C:/wms/api/src/main/java/com/lessonring/api/payment/infrastructure/lock/PaymentRedisLockManager.java#L1)

---

# 개발 생산성

## Lombok

- 의존성: `org.projectlombok:lombok`
- 역할: 반복 boilerplate 코드 감소
- 근거: [`build.gradle`](/C:/wms/api/build.gradle#L35)

## AssertJ / Spring Boot Test / Spring Security Test

- 역할: 테스트 작성 지원
- 의존성:
  - `org.assertj:assertj-core:3.27.7`
  - `spring-boot-starter-test`
  - `spring-security-test`
- 근거: [`build.gradle`](/C:/wms/api/build.gradle#L43)

---

# 외부 결제 연동

## Toss Payments

- 역할: PG 승인/취소 연동
- 관련 코드:
  - [`TossPaymentsClient.java`](/C:/wms/api/src/main/java/com/lessonring/api/payment/infrastructure/pg/TossPaymentsClient.java#L1)
  - [`PaymentPgService.java`](/C:/wms/api/src/main/java/com/lessonring/api/payment/application/PaymentPgService.java#L1)
- 관련 설정:
  - `pg.toss.base-url`
  - `pg.toss.secret-key`
  - [`application-local.yml`](/C:/wms/api/src/main/resources/application-local.yml#L1)
  - [`application-dev.yml`](/C:/wms/api/src/main/resources/application-dev.yml#L1)

---

# 현재 코드에 있으나 아직 실사용으로 보기 어려운 항목

아래는 클래스나 TODO 흔적은 있으나, 현재 의존성이나 실제 동작 구성이 완성되지 않은 항목이다.

## Kafka

- 관련 코드:
  - [`KafkaConfig.java`](/C:/wms/api/src/main/java/com/lessonring/api/common/infrastructure/kafka/KafkaConfig.java#L1)
  - [`EventKafkaProducer.java`](/C:/wms/api/src/main/java/com/lessonring/api/integration/kafka/EventKafkaProducer.java#L1)
  - [`BookingKafkaProducer.java`](/C:/wms/api/src/main/java/com/lessonring/api/booking/infrastructure/messaging/BookingKafkaProducer.java#L1)
- 상태:
  - `spring-kafka` 의존성 없음
  - TODO 수준의 플레이스홀더 코드

## Feign

- 관련 코드:
  - [`FeignConfig.java`](/C:/wms/api/src/main/java/com/lessonring/api/common/infrastructure/feign/FeignConfig.java#L1)
  - [`ExternalApiClient.java`](/C:/wms/api/src/main/java/com/lessonring/api/integration/feign/ExternalApiClient.java#L1)
  - [`KakaoFeignClient.java`](/C:/wms/api/src/main/java/com/lessonring/api/auth/infrastructure/oauth/KakaoFeignClient.java#L1)
- 상태:
  - OpenFeign 의존성 없음
  - TODO 수준의 플레이스홀더 코드

## MapStruct

- 상태:
  - README에는 언급되어 있으나 현재 `build.gradle` 기준 의존성 없음
  - 실제 사용 코드도 확인되지 않음

---

# 요약

현재 프로젝트의 실사용 기술 스택은 다음으로 정리할 수 있다.

- Java 21
- Spring Boot 3.5.9
- Spring Web
- Spring Validation
- Spring Security
- JWT
- PostgreSQL
- Spring Data JPA / Hibernate
- Querydsl
- Flyway
- Redis
- Redisson
- Springdoc OpenAPI / Swagger UI
- Gradle

Kafka, Feign, MapStruct는 현재 코드베이스에 흔적은 있으나, 실사용 스택으로 확정하기에는 아직 이르다.
