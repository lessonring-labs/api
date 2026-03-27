# 개발 컨벤션 및 가이드 (Dev Guide)

이 문서는 LessonRing Backend 개발 시 팀이 공통으로 따르는 기술적 약속을 정리한 문서다.

목적:

- 코드 스타일과 구조 기준을 통일한다.
- 협업 시 충돌을 줄인다.
- 리뷰 기준을 명확히 한다.
- 신규 팀원이 빠르게 프로젝트 방식에 적응할 수 있게 한다.

이 문서는 다음 기존 문서의 공통 핵심을 하나로 묶은 통합 가이드다.

- `coding-rules.md`
- `development-guide.md`
- `development-process.md`
- `module-dependency-rules.md`
- `logging-standard.md`

---

# 1. 개발 원칙

LessonRing Backend는 다음 원칙을 우선한다.

- 도메인 중심 설계
- 계층 책임 분리
- 운영 정합성 우선
- 읽기 쉬운 코드
- 테스트 가능한 구조
- 문서와 코드의 동기화

이 프로젝트는 단순 CRUD보다 예약, 출석, 회원권, 결제의 상태 정합성이 더 중요하다. 따라서 "짧은 구현"보다 "명확한 책임과 안전한 처리"를 우선한다.

---

# 2. 패키지 및 계층 구조

기본 구조:

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

각 도메인은 기본적으로 다음 계층을 따른다.

```text
api
application
domain
infrastructure
```

계층 책임:

- `api`: Controller, Request DTO, Response DTO
- `application`: 유스케이스 조합, 트랜잭션 경계, 도메인 간 orchestration
- `domain`: 엔티티, enum, 상태 전이, 도메인 규칙, repository interface
- `infrastructure`: DB 접근, repository 구현, 외부 시스템 연동

---

# 3. 계층별 기술적 약속

## 3.1 Controller

Controller는 얇게 유지한다.

Controller의 책임:

- HTTP 요청 수신
- DTO 검증 진입점
- Service 호출
- `ApiResponse` 반환

Controller에서 하지 말아야 할 것:

- 비즈니스 로직 처리
- Repository 직접 호출
- 트랜잭션 관리
- 복잡한 예외 분기 처리

## 3.2 Application Service

Application Service는 유스케이스를 조합한다.

Service의 책임:

- 여러 도메인 조회 및 조합
- 트랜잭션 관리
- 순서 제어
- 도메인 규칙 호출
- 예외 발생 지점 통제

## 3.3 Domain

Domain은 상태와 상태 전이를 책임진다.

예:

- `Booking.cancel()`
- `Booking.attend()`
- `Membership.useOnce()`
- `Membership.refund()`

Domain에서 하지 말아야 할 것:

- HTTP 처리
- DTO 처리
- 외부 API 호출
- Repository 구현 의존

## 3.4 Repository

Repository는 데이터 접근만 담당한다.

규칙:

- Repository interface는 `domain`
- 구현체는 `infrastructure`
- 비즈니스 로직은 넣지 않는다

---

# 4. 모듈 의존 규칙

다음 규칙을 따른다.

- 도메인 간 결합은 최소화한다.
- 순환 의존은 금지한다.
- `common`은 공통 규칙만 담는다.
- 외부 연동은 `integration`으로 모은다.
- 다른 도메인을 직접 엔티티 참조하기보다 `id` 기반 참조를 우선한다.

예:

- `booking`은 `memberId`, `scheduleId`, `membershipId`를 사용한다.
- `domain`은 `controller`, `api dto`, `repository implementation`에 의존하지 않는다.

---

# 5. 네이밍 규칙

## 5.1 클래스명

- `PascalCase`

예:

- `BookingService`
- `PaymentWebhookService`
- `JwtAuthenticationFilter`

## 5.2 메서드명 / 변수명

- `camelCase`

예:

- `createBooking`
- `cancelBooking`
- `memberId`
- `remainingCount`

짧은 축약어는 피한다.

피해야 할 예:

- `repo`
- `svc`
- `cnt`

## 5.3 상수명

- `UPPER_SNAKE_CASE`

예:

- `WEBHOOK_LOCK_TIMEOUT_SECONDS`
- `DEFAULT_PAGE_SIZE`

## 5.4 패키지명

- 모두 소문자

---

# 6. DTO 규칙

Request / Response DTO는 `api` 계층 아래 둔다.

예:

```text
booking/api/request/BookingCreateRequest
booking/api/response/BookingResponse
```

규칙:

- Request DTO는 입력 검증용
- Response DTO는 출력 계약용
- DTO에 비즈니스 로직을 넣지 않는다
- DTO와 Entity를 직접 섞지 않는다

네이밍:

- `CreateRequest`
- `UpdateRequest`
- `SearchRequest`
- `Response`

---

# 7. Entity 및 Domain Model 규칙

규칙:

- public setter 남발 금지
- 상태 변경은 의도가 드러나는 메서드로 수행
- static factory method 사용 가능
- 상태값은 문자열 직접 비교 대신 enum 사용

좋은 예:

- `booking.cancel(reason)`
- `membership.useOnce()`
- `payment.linkMembership(id)`

피해야 할 예:

- `booking.setStatus(...)`
- `membership.setRemainingCount(...)`

---

# 8. Validation 규칙

형식 검증은 Request DTO에서 처리한다.

예:

- `@NotNull`
- `@NotBlank`
- `@Positive`
- `@Future`

비즈니스 검증은 Service 또는 Domain에서 처리한다.

예:

- 중복 예약 여부
- 정원 초과 여부
- 회원권 사용 가능 여부
- 결제 상태 전이 가능 여부

정리:

- 형식 검증: DTO
- 비즈니스 검증: Service / Domain

---

# 9. 예외 처리 규칙

공통 예외 구조를 사용한다.

구성:

- `BusinessException`
- `ErrorCode`
- `GlobalExceptionHandler`

규칙:

- 비즈니스 예외는 `BusinessException`으로 던진다
- 문자열 예외 메시지를 직접 남발하지 않는다
- Controller에서 `try-catch`로 직접 처리하지 않는다
- 전역 예외 처리기로 응답 형식을 통일한다

좋은 예:

```java
throw new BusinessException(ErrorCode.INVALID_REQUEST);
```

피해야 할 예:

```java
throw new RuntimeException("예약 실패");
```

---

# 10. 응답 규칙

모든 API는 `ApiResponse` 구조를 따른다.

예:

```json
{
  "success": true,
  "data": {},
  "error": null
}
```

규칙:

- 성공/실패 구조를 통일한다
- Controller는 Response DTO를 `ApiResponse`로 감싸서 반환한다
- 임의의 응답 구조를 새로 만들지 않는다

---

# 11. 트랜잭션 규칙

트랜잭션은 Service Layer에서 관리한다.

규칙:

- 조회: `@Transactional(readOnly = true)`
- 쓰기: `@Transactional`
- Controller와 Repository에서 트랜잭션을 시작하지 않는다

예약/결제처럼 충돌 가능성이 높은 흐름은 다음 3가지를 함께 본다.

- DB Transaction
- Redis Distributed Lock
- DB 제약/정합성 검증

---

# 12. 로그 규칙

로그는 디버깅용이 아니라 운영 추적용으로 남긴다.

권장 로그 레벨:

- `INFO`: 정상 처리 흐름
- `WARN`: 비즈니스 예외, 검증 실패, 재시도 가능한 문제
- `ERROR`: 예상하지 못한 예외, 시스템 장애

공통 필드 권장:

- `module`
- `action`
- `result`
- `requestId`
- `errorCode`
- `durationMs`

규칙:

- 결제, webhook, 예약, 출석, 인증 실패는 로그가 있어야 한다
- 민감정보는 로그에 남기지 않는다
- 같은 이벤트는 같은 패턴으로 기록한다

예:

```text
payment approve requested. paymentId={}, orderId={}, idempotencyKey={}
payment approve succeeded. paymentId={}, membershipId={}, paymentKey={}, durationMs={}
```

---

# 13. 테스트 규칙

테스트는 기능 완료의 일부다.

우선 테스트 대상:

- 결제 승인/환불
- webhook 처리
- 예약 생성/취소/no-show
- 출석 처리
- 인증/보안

규칙:

- 핵심 도메인 흐름은 테스트 없이 머지하지 않는다
- 회귀 위험이 큰 버그는 재현 테스트를 먼저 만든다
- 외부 연동은 mock 기반 테스트를 우선한다

좋은 테스트 기준:

- happy path
- invalid input
- business rule violation
- concurrency / idempotency

---

# 14. 개발 프로세스

기능 개발 기본 순서:

1. 요구사항 정리
2. Domain 모델/규칙 정의
3. Entity 및 enum 설계
4. Repository interface 정의
5. Service 구현
6. Controller 및 DTO 구현
7. Validation 적용
8. 테스트 작성/실행
9. 문서 업데이트
10. 커밋

원칙:

- Domain first
- 코드와 문서를 함께 업데이트
- 새 기능은 최소 1개 이상의 검증 시나리오를 갖는다

---

# 15. Git 및 협업 규칙

브랜치 예시:

- `feature/booking-api`
- `feature/payment-refund`
- `docs/analytics-plan`

커밋 메시지 예시:

- `기능(booking): 예약 생성 API 구현`
- `수정(auth): JWT 검증 오류 처리 보완`
- `문서(api): analytics API 명세 추가`

원칙:

- 한 커밋은 한 목적
- 제목은 간결하게
- 문서 변경도 기능 변경과 함께 반영

---

# 16. 코드 리뷰 체크리스트

리뷰 시 다음을 확인한다.

- 계층 책임이 맞는가
- Controller가 비대하지 않은가
- Domain이 상태 전이를 책임지는가
- Repository가 데이터 접근만 하는가
- 예외 처리 방식이 일관적인가
- 로그가 필요한 지점에 있는가
- 민감정보를 노출하지 않는가
- 테스트가 핵심 시나리오를 커버하는가
- 문서 업데이트가 필요한데 빠지지 않았는가

---

# 17. 금지 사항

다음 방식은 지양한다.

- Controller에서 비즈니스 로직 처리
- Controller에서 Repository 직접 호출
- Domain에서 Infrastructure 의존
- 문자열로 상태값 직접 비교
- public setter 남발
- `RuntimeException` 직접 남발
- 민감정보 로그 출력
- 테스트 없이 핵심 로직 변경
- 문서와 코드가 어긋난 상태 방치

---

# 18. 신규 팀원 온보딩 순서

신규 개발자는 다음 순서로 문서를 읽는 것을 권장한다.

1. `docs/architecture/system-architecture.md`
2. `docs/architecture/domain-architecture.md`
3. `docs/architecture/adr.md`
4. `docs/development/package-structure.md`
5. `docs/development/dev-conventions-and-guide.md`
6. 관련 도메인 문서
7. 관련 테스트 코드

---

# 19. 요약

LessonRing Backend의 개발 컨벤션은 한 문장으로 요약하면 다음과 같다.

**도메인 중심으로 설계하고, 계층 책임을 지키며, 운영 정합성과 협업 일관성을 최우선으로 개발한다.**

이 문서는 팀의 기본 약속 문서이며, 실제 코드가 바뀌면 함께 갱신해야 한다.
