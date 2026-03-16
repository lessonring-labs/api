# Redis Lock Guide

LessonRing Backend에서 Redis Distributed Lock을 사용하는 목적과 적용 방식을 정리한다.

이 문서는 다음 내용을 다룬다.

- Redis 사용 목적
- Docker 기반 Redis 실행 방법
- Spring Boot Redis 연결 설정
- Redisson 구성 방식
- Distributed Lock 적용 구조
- Booking 예약 동시성 제어 방식
- 테스트 방법
- 운영 시 고려사항

---

# 1. Overview

LessonRing Backend는 예약 시스템의 동시성 제어를 위해 Redis Distributed Lock을 사용한다.

예약 시스템에서는 같은 수업(schedule)에 대해 여러 사용자가 동시에 예약 요청을 보낼 수 있다.  
이때 단순한 DB 조회/저장만으로는 정원(capacity) 초과, 중복 처리, 상태 불일치가 발생할 수 있다.

이를 방지하기 위해 다음 구조를 사용한다.

```text
Redis Lock
→ Schedule 단위 직렬화
→ Capacity 검증
→ bookedCount 상태 변경
→ Booking 저장
```

---

# 2. Why Redis Lock

Redis Lock을 사용하는 이유는 다음과 같다.

- 동일한 schedule에 대한 동시 예약 요청 직렬화
- 예약 생성과 취소 시 bookedCount 정합성 보장
- 정원 초과 방지
- 멀티 인스턴스 환경 대응 가능
- DB 제약만으로 해결하기 어려운 타이밍 이슈 보완

Redis Lock은 DB 트랜잭션을 대체하는 것이 아니라,  
트랜잭션과 함께 사용하여 정합성을 강화하는 역할을 한다.

---

# 3. Where Redis Lock Is Used

현재 Redis Lock은 Booking 생성 시점에 적용한다.

적용 대상

- Booking 생성
- Booking 취소

락 기준 자원

```text
scheduleId
```

락 키 예시

```text
booking:schedule:41
booking:schedule:42
```

즉 같은 수업에 대한 예약/취소는 같은 락을 사용한다.

---

# 4. Redis Docker Run Guide

로컬 개발 환경에서는 Docker 기반 Redis 실행을 권장한다.

## 4.1 단건 실행

```bash
docker run -d \
  --name lessonring-redis \
  -p 6379:6379 \
  redis:7
```

## 4.2 실행 확인

```bash
docker ps
```

## 4.3 Redis 응답 확인

```bash
docker exec -it lessonring-redis redis-cli ping
```

정상 응답

```text
PONG
```

---

# 5. Docker Compose Guide

프로젝트 루트에 `docker-compose.yml` 파일을 두고 관리하는 방식을 권장한다.

예시

```yaml
version: "3.8"

services:
  redis:
    image: redis:7
    container_name: lessonring-redis
    ports:
      - "6379:6379"
    restart: always
    command: redis-server --appendonly yes
    volumes:
      - redis-data:/data

volumes:
  redis-data:
```

실행

```bash
docker compose up -d
```

---

# 6. Spring Boot Redis Configuration

Spring Boot는 Redis 연결 정보를 application 설정으로 관리한다.

예시

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

Docker에서 Redis를 로컬 포트로 노출했다면 `localhost:6379` 로 연결하면 된다.

---

# 7. Redisson Dependency

Redis Distributed Lock 구현에는 Redisson을 사용한다.

Gradle dependency 예시

```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-redis'
implementation 'org.redisson:redisson-spring-boot-starter:3.37.0'
```

---

# 8. RedissonConfig

RedissonClient Bean을 생성하여 DistributedLockService에서 사용할 수 있도록 구성한다.

예시

```java
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port);

        return Redisson.create(config);
    }
}
```

역할

- RedissonClient Bean 등록
- Redis 단일 서버 연결 구성
- Lock 획득 API 제공 기반 생성

---

# 9. DistributedLockService

서비스 레이어에서 Redis 구현 세부사항을 직접 다루지 않도록 공통 Lock 서비스로 추상화한다.

역할

- lock key 기반 분산락 획득
- waitTime / leaseTime 표준화
- tryLock / unlock 공통 처리
- 락 실패 시 예외 처리
- 콜백 기반 비즈니스 로직 실행

권장 구조

```text
BookingService
→ DistributedLockService.executeWithLock()
→ RedissonClient.getLock()
```

---

# 10. Lock Key Strategy

락 키는 경쟁 자원을 기준으로 설계해야 한다.

현재 예약 시스템의 기준 자원은 `scheduleId` 이다.

권장 키 규칙

```text
booking:schedule:{scheduleId}
```

예시

```text
booking:schedule:30
booking:schedule:41
```

장점

- 같은 schedule 예약끼리만 직렬화
- 다른 schedule 예약은 병렬 처리 가능
- 락 범위가 과도하게 넓어지지 않음

---

# 11. Lock Time Strategy

권장 시간 설정

```text
waitTime  = 3 seconds
leaseTime = 5 seconds
```

의미

- waitTime: 락 획득을 위해 대기하는 최대 시간
- leaseTime: 락 자동 해제 최대 시간

현재 Booking 트랜잭션은 짧은 편이므로 위 설정으로 충분하다.

권장 상수화 예시

```java
public final class LockConstants {

    private LockConstants() {
    }

    public static final long BOOKING_WAIT_TIME_SECONDS = 3L;
    public static final long BOOKING_LEASE_TIME_SECONDS = 5L;

    public static String bookingScheduleKey(Long scheduleId) {
        return "booking:schedule:" + scheduleId;
    }
}
```

---

# 12. Booking Create Lock Flow

Booking 생성은 반드시 락 안에서 다음 순서로 처리해야 한다.

```text
1. schedule 락 획득
2. member 조회
3. schedule 조회
4. membership 조회
5. schedule 상태 검증
6. capacity 검증
7. 중복 예약 검증
8. bookedCount 증가
9. booking 저장
10. 이벤트 발행
11. 락 해제
```

핵심 원칙

- 검증과 상태 변경을 락 안에서 함께 수행
- bookedCount 증가를 실제 저장 전에 처리
- 락 밖에서 schedule 상태를 먼저 읽지 않음

---

# 13. Booking Cancel Lock Flow

취소도 같은 schedule 락 안에서 처리하는 것을 권장한다.

이유

- 예약 생성과 예약 취소가 같은 자원(schedule)에 영향을 줌
- bookedCount 감소와 신규 예약이 동시에 들어오는 경우 순서 보장이 필요함

권장 흐름

```text
1. booking 조회
2. schedule 락 획득
3. booking 재조회
4. 이미 취소된 예약인지 검증
5. booking 상태 변경
6. bookedCount 감소
7. 이벤트 발행
8. 락 해제
```

즉 생성과 취소 모두 동일한 락 키를 사용해야 한다.

---

# 14. bookedCount Management

Redis Lock만으로는 충분하지 않다.  
실제로 수업 정원 상태를 나타내는 `bookedCount` 가 함께 관리되어야 한다.

Schedule 엔티티 권장 메서드

```java
public void increaseBookedCount() {
    if (this.bookedCount >= this.capacity) {
        throw new BusinessException(ErrorCode.INVALID_REQUEST);
    }
    this.bookedCount += 1;
}

public void decreaseBookedCount() {
    if (this.bookedCount > 0) {
        this.bookedCount -= 1;
    }
}
```

핵심

- 정원 초과 시 예외 발생
- 취소 시 음수 방지
- create/cancel 모두 schedule 상태를 변경해야 함

---

# 15. Why Lock Alone Is Not Enough

Redis Lock을 걸어도 아래가 빠지면 의미가 없다.

예시 문제

- bookedCount 증가 누락
- bookedCount 감소 누락
- 락 밖에서 capacity 검증
- DB 최종 정합성 보호 장치 없음

즉 안전한 구조는 다음 조합이다.

```text
Redis Lock
+ Schedule bookedCount 관리
+ Transaction
+ DB 제약
```

---

# 16. Recommended Reservation Concurrency Pattern

현재 LessonRing에 가장 적합한 패턴은 다음과 같다.

```text
Pattern 1. 단일 자원 락
→ booking:schedule:{scheduleId}
```

그리고 운영 안정성 강화를 위해 아래를 함께 사용한다.

```text
Pattern 3. Redis Lock + DB 제약 조합
```

즉 현재 추천 최종 방향은 아래와 같다.

```text
Redis Lock
→ schedule 단위 직렬화
→ capacity 검증
→ bookedCount 변경
→ booking 저장
→ DB 제약으로 최종 보호
```

---

# 17. Test Guide

## 17.1 단건 테스트

capacity = 1 인 schedule 생성 후 1건 예약 요청

기대 결과

```text
예약 성공
bookedCount = 1
```

## 17.2 동시성 테스트

서로 다른 회원 2명이 같은 schedule(capacity=1)에 동시에 예약 요청

기대 결과

```text
1명 성공
1명 실패
bookedCount = 1
booking 1건만 생성
```

## 17.3 Redis 확인

Redis CLI 접속

```bash
docker exec -it lessonring-redis redis-cli
```

키 확인

```text
KEYS *
```

락 키 예시

```text
booking:schedule:41
```

※ 락 키는 짧게 생성되었다가 해제되므로 항상 보이지는 않을 수 있다.

---

# 18. Common Mistakes

다음은 예약 시스템에서 자주 하는 실수다.

## 18.1 락만 걸고 상태 변경 안 함

예

```text
lock 사용
but bookedCount 증가 없음
→ 동시 요청 모두 성공 가능
```

## 18.2 락 범위를 너무 넓게 설정

예

```text
booking:global
```

문제

- 모든 예약이 직렬화됨
- 병목 증가

## 18.3 락 없이 DB만 믿음

문제

- 초당 동시 요청이 늘면 타이밍 이슈 빈번

## 18.4 Redis만 믿고 DB 보호 장치 없음

문제

- 예외 상황에서 정합성 보장 약함

---

# 19. Future Enhancements

향후 확장 방향

- Membership 단위 락 검토
- Outbox 패턴 도입
- Kafka 이벤트 연동
- 예약 대기열 정책
- API Rate Limit
- Lock 실패 전용 ErrorCode 분리
- 관리자용 모니터링 지표 추가

---

# 20. Summary

LessonRing Backend는 예약 시스템 동시성 제어를 위해 Redis Distributed Lock을 사용한다.

핵심 원칙

- schedule 단위 락 사용
- 락 키 표준화
- 검증과 상태 변경을 락 안에서 함께 처리
- bookedCount를 실제로 관리
- Redis Lock + Transaction + DB 보호 장치를 함께 사용

현재 구조는 예약 시스템의 기본적인 동시성 제어를 위한 실무형 설계로 볼 수 있다.