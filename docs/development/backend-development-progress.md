# Backend Development Progress

LessOnRing Backend 개발 진행 현황

---

# 1️⃣ Core

- [x] 핵심 도메인 식별
- [x] Studio / Instructor / Member / Membership / Schedule / Booking / Attendance / Payment / Notification 관계 정의
- [x] 엔티티 초안 정의
- [x] 상태값(Enum) 정의
- [x] core-domain.md 작성

---

# 2️⃣ DB

- [x] PostgreSQL 연결
- [x] HikariCP 연결
- [x] Flyway 설정
- [x] JPA 설정
- [x] QueryDSL 설정
- [x] Base Migration 작성
- [x] V1 ~ V10 Migration 적용

### Migration List

- [x] V1 create_studio
- [x] V2 create_instructor
- [x] V3 create_member
- [x] V4 create_membership
- [x] V5 create_schedule
- [x] V6 create_booking
- [x] V7 create_attendance
- [x] V8 create_payment
- [x] V9 create_notification
- [x] V10 create_refresh_token

### Runtime

- [x] JPA EntityManager 생성
- [x] Tomcat 8080 기동
- [x] Spring Boot 실행

---

# 3️⃣ Common

- [x] BaseEntity
- [x] JpaAuditingConfig
- [x] AuditorAwareConfig
- [x] QueryDslConfig
- [x] SecurityConfig 기본 구조
- [x] ApiResponse
- [x] ErrorCode
- [x] BusinessException
- [x] GlobalExceptionHandler
- [x] 공통 응답 규칙 정리
- [x] 공통 예외 처리 규칙 정리

---

# 4️⃣ Auth

### Token

- [x] RefreshToken Entity
- [x] RefreshToken Repository
- [x] RefreshToken DB 연동

### JWT

- [x] JwtTokenProvider interface
- [x] JwtTokenProviderImpl
- [x] JwtAuthenticationFilter

### Auth API

- [x] AuthService
- [x] AuthController
- [x] 로그인 API
- [x] 토큰 재발급 API
- [x] 로그아웃 API

### Security

- [x] SecurityConfig JWT 연결
- [x] /auth/** permitAll 적용
- [x] 보호 API 인증 처리
- [x] /me 인증 테스트

### Kakao OAuth (미구현)

- [ ] Kakao OAuth 진입 구조
- [ ] Kakao OAuth Client
- [ ] Kakao 사용자 정보 조회
- [ ] 내부 Member 연계
- [ ] Kakao 로그인 후 JWT 발급

---

# 5️⃣ Member

### Domain

- [x] Gender enum
- [x] MemberStatus enum
- [x] Member Entity

### Repository

- [x] MemberRepository interface
- [x] MemberJpaRepository
- [x] MemberRepositoryImpl

### DTO

- [x] MemberCreateRequest
- [x] MemberResponse

### Service / API

- [x] MemberService
- [x] MemberController
- [x] 회원 등록 API
- [x] 회원 단건 조회 API
- [x] 회원 목록 조회 API

### TODO

- [ ] 회원 검색 / 페이징 조회
- [ ] 회원 상태 변경
- [ ] 전화번호 중복 검사
- [ ] soft delete 정책 검토

### Document

- [x] member_domain.md 작성

---

# 6️⃣ Schedule

### Domain

- [ ] ScheduleStatus enum
- [ ] Schedule Entity

### Repository

- [ ] ScheduleRepository interface
- [ ] ScheduleJpaRepository
- [ ] ScheduleRepositoryImpl

### DTO

- [ ] ScheduleCreateRequest
- [ ] ScheduleResponse

### Service / API

- [ ] ScheduleService
- [ ] ScheduleController
- [ ] 수업 생성 API
- [ ] 수업 단건 조회 API
- [ ] 수업 목록 조회 API
- [ ] 기간별 스케줄 조회
- [ ] 강사별 스케줄 조회
- [ ] 정원(capacity) 관리
- [ ] 예약 가능 여부 검증

### Document

- [x] schedule_domain.md 작성

---

# 7️⃣ Booking

### Domain

- [ ] BookingStatus enum
- [ ] Booking Entity

### Repository

- [ ] BookingRepository interface
- [ ] BookingJpaRepository
- [ ] BookingRepositoryImpl

### DTO

- [ ] BookingCreateRequest
- [ ] BookingResponse

### Service / API

- [ ] BookingService
- [ ] BookingController
- [ ] 예약 생성 API
- [ ] 예약 단건 조회 API
- [ ] 예약 목록 조회 API
- [ ] 예약 취소 API

### Validation

- [ ] 회원 존재 여부
- [ ] 스케줄 존재 여부
- [ ] 정원 초과 여부
- [ ] 중복 예약 여부

### Concurrency

- [ ] Redis Distributed Lock 적용

### Event

- [ ] BookingCreatedEvent
- [ ] BookingCanceledEvent
- [ ] 예약 이벤트 발행

### Document

- [x] booking_domain.md 작성

---

# 8️⃣ Membership

### Domain

- [ ] MembershipStatus enum
- [ ] Membership Entity

### Repository

- [ ] MembershipRepository interface
- [ ] MembershipJpaRepository
- [ ] MembershipRepositoryImpl

### DTO

- [ ] MembershipCreateRequest
- [ ] MembershipResponse

### Service / API

- [ ] MembershipService
- [ ] MembershipController
- [ ] 이용권 생성 API
- [ ] 이용권 단건 조회 API
- [ ] 회원별 이용권 목록 조회 API

### Status

- [ ] ACTIVE
- [ ] EXPIRED
- [ ] SUSPENDED
- [ ] USED_UP

### Logic

- [ ] 이용권 사용 처리
- [ ] 이용권 잔여 횟수 차감
- [ ] 이용권 만료 처리
- [ ] 이용권 유효기간 검증

### Document

- [x] membership_domain.md 작성

---

# 9️⃣ Attendance

### Domain

- [ ] AttendanceStatus enum
- [ ] Attendance Entity

### Repository

- [ ] AttendanceRepository interface
- [ ] AttendanceJpaRepository
- [ ] AttendanceRepositoryImpl

### DTO

- [ ] AttendanceCreateRequest
- [ ] AttendanceResponse

### Service / API

- [ ] AttendanceService
- [ ] AttendanceController
- [ ] 출석 기록 생성 API
- [ ] 출석 단건 조회 API
- [ ] 출석 목록 조회 API

### Logic

- [ ] Booking → Attendance 연결
- [ ] 출석 처리 시 Booking 상태 변경
- [ ] 출석 처리 시 Membership 차감 연결
- [ ] no-show 정책 정의

### Document

- [x] attendance_domain.md 작성

---

# 🔟 Payment

### Domain

- [ ] PaymentMethod enum
- [ ] PaymentStatus enum
- [ ] Payment Entity

### Repository

- [ ] PaymentRepository interface
- [ ] PaymentJpaRepository
- [ ] PaymentRepositoryImpl

### DTO

- [ ] PaymentCreateRequest
- [ ] PaymentResponse

### Service / API

- [ ] PaymentService
- [ ] PaymentController
- [ ] 결제 생성 API
- [ ] 결제 단건 조회 API
- [ ] 결제 목록 조회 API

### Logic

- [ ] 결제 완료 처리
- [ ] 결제 취소 / 환불 처리

### Event

- [ ] PaymentCompletedEvent
- [ ] 결제 완료 후 Membership 생성 연결

### Analytics

- [ ] 결제 통계 기반 구조

### Document

- [x] payment_domain.md 작성

---

# 1️⃣1️⃣ Notification

### Domain

- [x] Notification Entity

### Repository

- [ ] NotificationRepository interface
- [ ] NotificationJpaRepository
- [ ] NotificationRepositoryImpl

### DTO

- [ ] NotificationResponse

### Service / API

- [ ] NotificationService
- [ ] NotificationController
- [ ] 알림 목록 조회 API
- [ ] 알림 읽음 처리 API

### Event Trigger

- [ ] 예약 생성 알림
- [ ] 예약 취소 알림
- [ ] 수업 시작 알림
- [ ] 결제 완료 알림

### Event System

- [ ] 이벤트 기반 알림 발행 구조

### Document

- [x] notification_domain.md 작성