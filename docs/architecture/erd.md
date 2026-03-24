# ERD

이 문서는 `src/main/java` 기준 JPA 엔티티를 분석해 정리한 논리 ERD다.

- 기준일: 2026-03-24
- 기준 소스: `Studio`, `Member`, `Instructor`, `Schedule`, `Membership`, `Booking`, `Attendance`, `Payment`, `PaymentWebhookLog`, `Notification`, `RefreshToken`
- 주의: 현재 코드는 대부분 `@ManyToOne` 대신 `Long ...Id` 필드로 관계를 표현한다. 따라서 아래 관계는 "코드에서 사용 중인 논리 FK" 기준이다.

## Mermaid ERD

```mermaid
erDiagram
    STUDIO ||--o{ MEMBER : has
    STUDIO ||--o{ INSTRUCTOR : has
    STUDIO ||--o{ SCHEDULE : has
    STUDIO ||--o{ MEMBERSHIP : has
    STUDIO ||--o{ PAYMENT : has
    STUDIO ||--o{ NOTIFICATION : has

    MEMBER ||--o{ MEMBERSHIP : owns
    MEMBER ||--o{ BOOKING : makes
    MEMBER ||--o{ ATTENDANCE : checks_in
    MEMBER ||--o{ PAYMENT : pays
    MEMBER ||--o{ NOTIFICATION : receives
    MEMBER ||--o| REFRESH_TOKEN : authenticates

    INSTRUCTOR ||--o{ SCHEDULE : teaches

    SCHEDULE ||--o{ BOOKING : reserves
    SCHEDULE ||--o{ ATTENDANCE : records

    MEMBERSHIP ||--o{ BOOKING : used_for
    PAYMENT o|--|| MEMBERSHIP : creates_on_complete
    PAYMENT ||--o{ PAYMENT_WEBHOOK_LOG : tracked_by_order_id
    BOOKING ||--o| ATTENDANCE : results_in

    STUDIO {
        bigint id PK
        varchar name
        varchar phone
        varchar address
        varchar detail_address
        varchar timezone
        varchar business_number
        varchar status
        datetime created_at
        bigint created_by
        datetime updated_at
        bigint updated_by
    }

    MEMBER {
        bigint id PK
        bigint studio_id FK
        varchar name
        varchar phone
        varchar email
        varchar gender
        date birth_date
        varchar status
        datetime joined_at
        text memo
        datetime created_at
        bigint created_by
        datetime updated_at
        bigint updated_by
    }

    INSTRUCTOR {
        bigint id PK
        bigint studio_id FK
        varchar name
        varchar phone
        varchar email
        varchar profile_image_url
        varchar status
        text memo
        datetime created_at
        bigint created_by
        datetime updated_at
        bigint updated_by
    }

    SCHEDULE {
        bigint id PK
        bigint studio_id FK
        bigint instructor_id FK
        varchar title
        varchar type
        datetime start_at
        datetime end_at
        int capacity
        int booked_count
        varchar status
        datetime created_at
        bigint created_by
        datetime updated_at
        bigint updated_by
    }

    MEMBERSHIP {
        bigint id PK
        bigint studio_id FK
        bigint member_id FK
        varchar name
        varchar type
        int total_count
        int remaining_count
        date start_date
        date end_date
        varchar status
        datetime created_at
        bigint created_by
        datetime updated_at
        bigint updated_by
    }

    BOOKING {
        bigint id PK
        bigint studio_id FK
        bigint member_id FK
        bigint schedule_id FK
        bigint membership_id FK
        varchar status
        datetime booked_at
        datetime canceled_at
        varchar cancel_reason
        datetime created_at
        bigint created_by
        datetime updated_at
        bigint updated_by
    }

    ATTENDANCE {
        bigint id PK
        bigint booking_id FK
        bigint member_id FK
        bigint schedule_id FK
        varchar status
        datetime checked_at
        text note
        datetime created_at
        bigint created_by
        datetime updated_at
        bigint updated_by
    }

    PAYMENT {
        bigint id PK
        bigint studio_id FK
        bigint member_id FK
        bigint membership_id FK_nullable
        varchar order_name
        varchar method
        varchar status
        bigint amount
        datetime paid_at
        datetime canceled_at
        varchar membership_name
        varchar membership_type
        int membership_total_count
        date membership_start_date
        date membership_end_date
        varchar pg_provider
        varchar pg_order_id
        varchar pg_payment_key
        text pg_raw_response
        varchar failed_reason
        varchar idempotency_key
        datetime created_at
        bigint created_by
        datetime updated_at
        bigint updated_by
    }

    PAYMENT_WEBHOOK_LOG {
        bigint id PK
        varchar provider
        varchar transmission_id
        varchar event_type
        varchar order_id
        varchar payment_key
        text payload
        datetime created_at
        bigint created_by
        datetime updated_at
        bigint updated_by
    }

    NOTIFICATION {
        bigint id PK
        bigint studio_id FK
        bigint member_id FK
        varchar title
        text content
        varchar type
        datetime read_at
        datetime created_at
        bigint created_by
        datetime updated_at
        bigint updated_by
    }

    REFRESH_TOKEN {
        bigint id PK
        bigint user_id FK
        varchar token
        datetime expires_at
        datetime created_at
        bigint created_by
        datetime updated_at
        bigint updated_by
    }
```

## 핵심 관계

- `studio -> member/instructor/schedule/membership/payment/notification`
  - 거의 모든 운영 데이터가 `studio_id` 기준으로 귀속된다.
- `member -> membership`
  - 한 회원은 여러 이용권을 가질 수 있다.
- `member + schedule + membership -> booking`
  - 예약은 회원, 수업, 이용권을 함께 묶는 허브 테이블이다.
- `booking -> attendance`
  - 출석 생성 전에 `existsByBookingId` 검증을 하므로 현재 로직상 예약 1건당 출석은 최대 1건이다.
- `payment -> membership`
  - 결제 완료 시 이용권이 생성되고, 생성된 `membership.id`가 `payment.membership_id`에 저장된다.
- `payment_webhook_log -> payment`
  - 물리 FK가 아니라 `pg_order_id = order_id` 또는 `pg_payment_key = payment_key`로 추적한다.
- `refresh_token -> member`
  - 인증 서비스에서 `userId`를 `member.id`로 사용한다.

## 물리 FK 권장안

현재 엔티티에는 DB FK 제약조건이 드러나지 않는다. 운영 안정성을 위해 아래 FK를 우선 고려할 수 있다.

- `member.studio_id -> studio.id`
- `instructor.studio_id -> studio.id`
- `schedule.studio_id -> studio.id`
- `schedule.instructor_id -> instructor.id`
- `membership.studio_id -> studio.id`
- `membership.member_id -> member.id`
- `booking.studio_id -> studio.id`
- `booking.member_id -> member.id`
- `booking.schedule_id -> schedule.id`
- `booking.membership_id -> membership.id`
- `attendance.booking_id -> booking.id`
- `attendance.member_id -> member.id`
- `attendance.schedule_id -> schedule.id`
- `payment.studio_id -> studio.id`
- `payment.member_id -> member.id`
- `payment.membership_id -> membership.id`
- `notification.studio_id -> studio.id`
- `notification.member_id -> member.id`
- `refresh_token.user_id -> member.id`

## 제약조건 권장안

- `refresh_token.user_id` unique
  - 로그인 로직상 사용자당 refresh token 1건을 전제로 동작한다.
- `attendance.booking_id` unique
  - 출석은 예약당 최대 1건이라는 서비스 규칙과 맞다.
- `payment.idempotency_key` unique nullable
  - 멱등 결제 생성 의도와 맞다.
- `payment.pg_order_id` unique nullable
  - PG 주문 번호 중복을 막기 좋다.
- `payment_webhook_log(provider, transmission_id)` unique
  - webhook 중복 수신 방지 로직과 맞다.

## 분석 메모

- `Booking`, `Attendance`, `PaymentWebhookLog`는 강한 업무 규칙을 갖지만 엔티티 연관관계 대신 ID 참조로 구현되어 있다.
- `Payment`는 결제 시점의 이용권 정보를 스냅샷으로 저장한다. 따라서 `membership_name`, `membership_type`, `membership_total_count`, `membership_start_date`, `membership_end_date`는 정규화보다 이력 보존을 우선한 설계다.
- `Attendance`가 `member_id`, `schedule_id`를 별도로 저장하는 것은 조회 최적화와 이력 보존 목적의 중복 저장으로 보인다.
