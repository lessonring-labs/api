# Coding Rules

LessonRing Backend 프로젝트의 코딩 규칙을 정의한다.

이 문서는 다음 목적을 가진다.

- 코드 스타일 통일
- 가독성 향상
- 유지보수성 향상
- 협업 효율 증가
- 도메인 중심 구조 유지

---

# 1. 기본 원칙

LessonRing Backend는 다음 원칙을 따른다.

- 읽기 쉬운 코드를 우선한다.
- 의도를 드러내는 이름을 사용한다.
- 하나의 클래스는 하나의 책임을 가진다.
- Controller에는 비즈니스 로직을 두지 않는다.
- Application Service에서 흐름을 제어한다.
- Domain은 핵심 비즈니스 의미를 표현해야 한다.
- 공통 규칙은 예외 없이 일관되게 적용한다.

---

# 2. 네이밍 규칙

## 2.1 클래스명

클래스명은 PascalCase를 사용한다.

예시

Member  
MemberService  
BookingController  
ScheduleRepositoryImpl  
JwtAuthenticationFilter

---

## 2.2 메서드명

메서드명은 camelCase를 사용한다.

예시

createMember()  
getSchedule()  
cancelBooking()  
findById()

---

## 2.3 변수명

변수명은 camelCase를 사용한다.

예시

memberId  
scheduleId  
bookingStatus  
remainingCount

짧은 축약어는 지양한다.

좋은 예

membershipRepository  
notificationService

나쁜 예

repo  
svc  
cnt

---

## 2.4 상수명

상수명은 UPPER_SNAKE_CASE를 사용한다.

예시

MAX_BOOKING_COUNT  
DEFAULT_PAGE_SIZE  
TOKEN_PREFIX

---

## 2.5 패키지명

패키지명은 모두 소문자를 사용한다.

예시

com.lessonring.api.member  
com.lessonring.api.booking  
com.lessonring.api.common.error

---

# 3. 패키지 구조 규칙

도메인 모듈은 다음 구조를 기본으로 한다.

api  
application  
domain  
infrastructure

예시

booking  
├─ api  
├─ application  
├─ domain  
└─ infrastructure

각 계층 역할

api
- Controller
- Request DTO
- Response DTO

application
- Service
- Use case orchestration
- Transaction boundary

domain
- Entity
- Enum
- Domain rule
- Repository interface

infrastructure
- JPA Repository
- Repository implementation
- 외부 시스템 연동

---

# 4. 클래스별 역할 규칙

## 4.1 Controller

Controller는 다음 역할만 수행한다.

- HTTP 요청 수신
- PathVariable / RequestBody 처리
- Request DTO 수신
- Service 호출
- ApiResponse 반환

Controller에서 하지 않는 것

- 비즈니스 로직 처리
- Repository 직접 호출
- 트랜잭션 처리
- 복잡한 예외 분기 처리

좋은 예

Controller  
→ Request 수신  
→ Service 호출  
→ Response 반환

나쁜 예

Controller  
→ 비즈니스 검증  
→ DB 접근  
→ 상태 변경  
→ 예외 직접 처리

---

## 4.2 Application Service

Application Service는 다음 역할을 가진다.

- 비즈니스 흐름 제어
- 여러 도메인 조합
- 트랜잭션 경계 설정
- 정책 적용 순서 제어

예시

BookingService

- Member 확인
- Schedule 확인
- Membership 확인
- 중복 예약 확인
- Booking 생성

---

## 4.3 Domain

Domain은 핵심 비즈니스 의미를 표현해야 한다.

예시

Booking.cancel()  
Membership.useOnce()  
Booking.attend()

Domain에 둘 수 있는 것

- 상태 변경 로직
- 상태 검증 로직
- 도메인 의미 있는 행위

Domain에 두지 않는 것

- HTTP 처리
- Request/Response 처리
- 외부 API 호출
- 직접적인 화면/UI 관심사

---

## 4.4 Repository

Repository는 데이터 접근만 담당한다.

Repository 인터페이스는 domain에 둔다.  
구현체는 infrastructure에 둔다.

예시

MemberRepository  
MemberJpaRepository  
MemberRepositoryImpl

---

# 5. DTO 규칙

## 5.1 Request DTO

입력용 DTO는 목적이 드러나게 네이밍한다.

규칙

CreateRequest  
UpdateRequest  
SearchRequest

예시

MemberCreateRequest  
ScheduleCreateRequest  
BookingCreateRequest

---

## 5.2 Response DTO

출력용 DTO는 Response suffix를 사용한다.

예시

MemberResponse  
ScheduleResponse  
BookingResponse

---

## 5.3 DTO 위치

Request / Response DTO는 api 하위 패키지에 둔다.

예시

booking/api/request/BookingCreateRequest  
booking/api/response/BookingResponse

---

## 5.4 DTO 규칙

DTO는 다음 원칙을 따른다.

- 비즈니스 로직을 가지지 않는다.
- Validation 용도만 가진다.
- Controller와 Service 경계를 명확히 한다.

---

# 6. Entity 규칙

## 6.1 Entity 책임

Entity는 상태와 상태 변화 로직을 가진다.

예시

Booking.create()  
Booking.cancel()  
Booking.attend()

Membership.create()  
Membership.useOnce()

---

## 6.2 Setter 사용 금지

Entity에는 무분별한 public setter를 두지 않는다.

권장 방식

- 정적 팩토리 메서드
- 의도가 드러나는 상태 변경 메서드

좋은 예

Membership.create(...)  
booking.cancel("user canceled")

나쁜 예

booking.setStatus(...)  
membership.setRemainingCount(...)

---

## 6.3 컬럼명 명시

DB 컬럼명이 snake_case인 경우 가능하면 `@Column(name = "...")` 으로 명시한다.

예시

@Column(name = "member_id")  
@Column(name = "booked_at")

이유

- DB와 코드의 대응 관계 명확화
- naming strategy 의존 최소화

---

# 7. Enum 규칙

상태값은 문자열 상수 대신 Enum을 사용한다.

예시

ScheduleStatus  
BookingStatus  
MembershipStatus  
AttendanceStatus

Enum은 도메인 의미가 드러나야 한다.

좋은 예

OPEN  
CLOSED  
CANCELED

RESERVED  
ATTENDED  
NO_SHOW

---

# 8. 메서드 설계 규칙

## 8.1 메서드는 의도가 드러나야 한다

좋은 예

createBooking()  
cancelBooking()  
getMembership()  
findAllByMemberId()

나쁜 예

process()  
handle()  
doIt()

---

## 8.2 메서드는 가능한 짧게 유지한다

권장

- 하나의 메서드는 하나의 역할
- 복잡한 조건은 private method로 분리

---

## 8.3 검증 로직은 의미별로 분리한다

예시

validateScheduleOpen(schedule)  
validateMembershipAvailable(membership)  
validateDuplicateBooking(memberId, scheduleId)

이렇게 하면 가독성이 올라간다.

---

# 9. 예외 처리 규칙

## 9.1 공통 예외 구조 사용

LessonRing은 다음 구조를 사용한다.

BusinessException  
ErrorCode  
GlobalExceptionHandler

예시

throw new BusinessException(ErrorCode.INVALID_REQUEST);

---

## 9.2 Controller에서 try-catch 남용 금지

예외는 GlobalExceptionHandler에서 일괄 처리한다.

Controller는 정상 흐름만 집중한다.

---

## 9.3 ErrorCode 우선 사용

문자열 메시지 직접 작성보다 ErrorCode를 우선 사용한다.

좋은 예

ErrorCode.ENTITY_NOT_FOUND  
ErrorCode.INVALID_REQUEST

나쁜 예

throw new RuntimeException("예약 실패");

---

# 10. 응답 규칙

모든 API는 ApiResponse 구조를 사용한다.

예시

{
"success": true,
"data": {},
"error": null
}

Controller 예시

return ApiResponse.success(response);

일관된 응답 형식을 유지한다.

---

# 11. Validation 규칙

입력 검증은 Request DTO에서 수행한다.

사용 예시

@NotNull  
@NotBlank  
@Positive  
@Future

복잡한 비즈니스 검증은 Service 또는 Domain에서 수행한다.

예시

- 예약 중복 여부
- 정원 초과 여부
- 이용권 사용 가능 여부
- 시작 시간/종료 시간 관계

즉

형식 검증  
→ DTO

비즈니스 검증  
→ Service / Domain

---

# 12. 트랜잭션 규칙

트랜잭션은 Service Layer에서 관리한다.

조회

@Transactional(readOnly = true)

쓰기

@Transactional

Controller / Repository에 트랜잭션을 남용하지 않는다.

---

# 13. 로그 규칙

로그는 필요한 곳에만 남긴다.

권장 상황

- 로그인 실패
- 예약 생성/취소
- 결제 완료/실패
- 외부 연동 실패
- 예외 발생

로그 레벨

INFO  
WARN  
ERROR

민감 정보는 로그에 남기지 않는다.

예시

- 비밀번호
- 전체 JWT 토큰
- 개인식별 민감정보

---

# 14. 주석 규칙

주석은 "무엇을 하는지"보다 "왜 이렇게 하는지"를 설명할 때 사용한다.

좋은 주석

- 비즈니스 정책 이유
- 예외적인 처리 이유
- 향후 개선 필요성

나쁜 주석

- 코드만 보면 알 수 있는 내용
- 불필요한 번역형 설명

좋은 예

// 예약 취소 시점에는 이용권 복구 정책이 확정되지 않아 상태만 변경한다.

나쁜 예

// booking을 취소한다.

---

# 15. 코드 중복 규칙

중복 코드는 최대한 제거한다.

공통 로직은 다음 위치를 고려한다.

- common util
- common response
- common error
- domain helper
- private method 분리

단, 무리한 공통화는 지양한다.

원칙

"비슷해 보인다"가 아니라  
"같은 책임을 가진다"일 때만 공통화한다.

---

# 16. 테스트 친화적 코드 규칙

코드는 테스트 가능하게 작성한다.

권장

- 의존성 주입 사용
- 정적 상태 최소화
- 비즈니스 로직을 Service / Domain에 배치
- 외부 연동 추상화

테스트가 어려운 구조는 보통 책임 분리가 잘못된 경우가 많다.

---

# 17. Git Commit 메시지 규칙

LessonRing은 한글 기반 커밋 타입을 사용한다.

예시 타입

기능  
수정  
리팩토링  
문서  
테스트  
설정

예시

기능(booking): 예약 생성 API 구현  
수정(auth): JWT 검증 오류 수정  
문서(api): API 가이드 문서 정리

원칙

- 한 커밋은 하나의 목적
- 제목은 간결하게
- 필요 시 본문에 변경 내용 정리

---

# 18. 리뷰 체크리스트

코드 리뷰 시 다음을 확인한다.

- 네이밍이 의도를 드러내는가
- Controller가 비대하지 않은가
- Service가 트랜잭션 경계를 잘 가지는가
- Domain이 상태 변화를 책임지는가
- Repository가 데이터 접근만 담당하는가
- 예외 처리가 일관적인가
- 응답 구조가 ApiResponse를 따르는가
- Validation 위치가 적절한가
- 중복 코드가 과하지 않은가

---

# 19. 금지 사항

다음 코딩 방식은 지양한다.

- Controller에서 비즈니스 로직 처리
- Entity에 무분별한 setter 추가
- RuntimeException 직접 남발
- 문자열 상태값 직접 비교
- Service 없이 Controller에서 Repository 호출
- 비즈니스 규칙을 util class에 몰아넣기
- 의미 없는 축약어 사용
- 민감 정보 로그 출력

---

# 20. Summary

LessonRing Backend는 도메인 중심 구조와 계층 분리를 유지하는 것을 가장 중요한 코딩 기준으로 삼는다.

핵심 원칙

- Controller는 얇게 유지한다.
- Service는 흐름과 트랜잭션을 책임진다.
- Domain은 핵심 상태와 행위를 가진다.
- Repository는 데이터 접근만 담당한다.
- 예외, 응답, 검증은 공통 규칙을 따른다.

이 문서는 Backend 전체 코드 스타일과 설계 일관성을 유지하기 위한 기준 문서로 사용한다.