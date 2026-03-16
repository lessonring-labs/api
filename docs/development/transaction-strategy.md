# Transaction Strategy

LessonRing Backend에서 사용하는 트랜잭션 전략과 동시성 제어 구조를 정리한다.

이 문서는 다음 내용을 다룬다.

- Spring Transaction 기본 전략
- 도메인 서비스에서의 트랜잭션 경계
- 예약 시스템 동시성 제어 방식
- Redis Distributed Lock 전략
- 이벤트 발행과 트랜잭션 관계
- DB 정합성 보장 전략

---

# 1. Overview

LessonRing Backend는 다음 세 가지 계층을 기준으로 데이터 정합성을 보장한다.

```text
1. Database Transaction
2. Redis Distributed Lock
3. Domain Event
```

각 계층의 역할은 다음과 같다.

| Layer | 역할 |
|-----|-----|
| Transaction | 데이터 일관성 보장 |
| Redis Lock | 동시 요청 직렬화 |
| Domain Event | 후처리 분리 |

---

# 2. Transaction Boundary

트랜잭션은 **Service Layer**에서 시작한다.

예시

```java
@Transactional
public Booking create(BookingCreateRequest request)
```

원칙

```text
Controller → Transaction 없음
Service → Transaction 시작
Repository → Transaction 참여
```

Service 단위에서 트랜잭션을 관리하는 이유

- 도메인 로직의 원자성 보장
- 여러 Repository 작업을 하나의 단위로 묶기 위함
- 예외 발생 시 전체 롤백

---

# 3. Transaction Rules

LessonRing Backend의 기본 규칙은 다음과 같다.

### Rule 1

조회 전용 로직

```java
@Transactional(readOnly = true)
```

### Rule 2

쓰기 로직

```java
@Transactional
```

### Rule 3

도메인 이벤트는 트랜잭션 안에서 발행

```text
Entity 상태 변경
→ Repository save
→ Domain Event publish
```

이렇게 하면 이벤트 리스너가 트랜잭션 완료 후 실행될 수 있다.

---

# 4. Reservation Concurrency Problem

예약 시스템에서는 다음 문제가 발생할 수 있다.

예시

```text
Schedule capacity = 1
```

동시에 두 요청 발생

```text
User A 예약
User B 예약
```

단순한 DB 조회/저장만 사용하면 다음 문제가 생긴다.

```text
A 조회 → capacity OK
B 조회 → capacity OK
A 저장
B 저장
```

결과

```text
예약 2개 생성
정원 초과
```

이 문제를 해결하기 위해 Redis Distributed Lock을 사용한다.

---

# 5. Redis Distributed Lock Strategy

예약 시스템에서는 **Schedule 단위 락**을 사용한다.

락 키

```text
booking:schedule:{scheduleId}
```

예시

```text
booking:schedule:41
booking:schedule:42
```

같은 schedule 예약 요청은 같은 락을 사용한다.

---

# 6. Lock Flow

예약 생성 흐름

```text
1. Redis Lock 획득
2. Schedule 조회
3. Capacity 검증
4. 중복 예약 검증
5. bookedCount 증가
6. Booking 저장
7. Domain Event 발행
8. Lock 해제
```

핵심 원칙

```text
검증 + 상태 변경은 반드시 Lock 안에서 수행
```

---

# 7. Cancel Flow

예약 취소도 동일한 schedule 락을 사용한다.

이유

예약 생성과 취소는 동일한 자원(schedule)에 영향을 준다.

예시 문제

```text
A 예약 취소
B 예약 생성
```

동시에 발생하면 순서 보장이 필요하다.

따라서 취소도 같은 락을 사용한다.

취소 흐름

```text
1. booking 조회
2. schedule 락 획득
3. booking 재조회
4. 취소 가능 여부 검증
5. booking 상태 변경
6. bookedCount 감소
7. Domain Event 발행
8. Lock 해제
```

---

# 8. bookedCount Management

Schedule 엔티티는 현재 예약 수를 관리한다.

```java
public void increaseBookedCount() {
    if (this.bookedCount >= this.capacity) {
        throw new BusinessException(ErrorCode.INVALID_REQUEST);
    }
    this.bookedCount++;
}

public void decreaseBookedCount() {
    if (this.bookedCount > 0) {
        this.bookedCount--;
    }
}
```

이 필드는 다음 목적을 가진다.

```text
빠른 정원 체크
동시성 제어
조회 성능 개선
```

---

# 9. Redis Lock + Transaction

Redis Lock과 DB Transaction은 서로 다른 역할을 한다.

| Component | 역할 |
|------|------|
| Redis Lock | 동시 요청 직렬화 |
| Transaction | 데이터 일관성 보장 |

구조

```text
Redis Lock
→ Transaction 시작
→ DB 상태 변경
→ Commit
→ Lock 해제
```

---

# 10. Why Lock Alone Is Not Enough

Redis Lock만으로는 완전한 정합성을 보장할 수 없다.

가능한 문제

```text
락 만료
네트워크 지연
멀티 인스턴스 타이밍 문제
코드 실수
```

따라서 다음 구조를 함께 사용한다.

```text
Redis Lock
+ Transaction
+ DB 제약
```

---

# 11. Recommended Concurrency Pattern

예약 시스템 권장 구조

```text
Pattern 1. 단일 자원 락
→ booking:schedule:{scheduleId}
```

추가 보호

```text
Pattern 3. Redis Lock + DB Constraint
```

즉 최종 구조

```text
Redis Lock
→ Capacity 검증
→ bookedCount 변경
→ Booking 저장
→ DB 제약 보호
```

---

# 12. Common Mistakes

### 락 없이 예약 처리

동시 요청 시 정원 초과 발생

### 락 범위가 너무 넓음

예

```text
booking:global
```

전체 예약이 직렬화됨

### bookedCount 관리 누락

락이 있어도 정원 초과 가능

### Redis만 믿고 DB 보호 장치 없음

운영 환경에서 정합성 문제 발생 가능

---

# 13. Future Improvements

향후 확장 방향

```text
Kafka 이벤트 처리
Outbox Pattern
Membership Lock
Rate Limit
Observability
```

---

# 14. Summary

LessonRing Backend의 트랜잭션 전략은 다음 세 가지 계층을 기반으로 한다.

```text
Transaction
Redis Distributed Lock
Domain Event
```

예약 시스템에서는 Schedule 단위 Redis Lock을 사용하여  
동시 예약 요청을 직렬화하고 데이터 정합성을 보장한다.