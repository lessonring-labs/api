# ERD DBML

이 문서는 `dbdiagram.io`에서 바로 사용할 수 있도록 현재 프로젝트 기준의 DBML을 정리한 문서다.

기준:

- `src/main/resources/db/migration`
- `src/main/resources/db/baseline/V1__init_schema.sql`
- 현재 엔티티 및 서비스 로직

주의:

- 아래 DBML은 현재 정리된 migration 결과 기준이다.
- `payment_webhook_log`는 `payment`와 물리 FK로 직접 연결되지 않고, `order_id`, `payment_key` 등 논리 키로 추적된다.

## DBML

```dbml
Project lessonring_api {
  database_type: "PostgreSQL"
  Note: "Current schema based on Flyway migrations and baseline init schema"
}

Table studio {
  id bigint [pk, increment]
  name varchar(100) [not null]
  phone varchar(30) [not null]
  address varchar(255) [not null]
  detail_address varchar(255)
  timezone varchar(50) [not null]
  business_number varchar(30)
  status varchar(30) [not null]
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint
}

Table instructor {
  id bigint [pk, increment]
  studio_id bigint [not null, ref: > studio.id]
  name varchar(100) [not null]
  phone varchar(30) [not null]
  email varchar(100)
  profile_image_url varchar(255)
  status varchar(30) [not null]
  memo varchar(500)
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint
}

Table member {
  id bigint [pk, increment]
  studio_id bigint [not null, ref: > studio.id]
  name varchar(100) [not null]
  phone varchar(30) [not null]
  email varchar(100)
  gender varchar(20)
  birth_date date
  status varchar(30) [not null]
  joined_at timestamp [not null]
  memo varchar(500)
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint
}

Table membership {
  id bigint [pk, increment]
  studio_id bigint [not null, ref: > studio.id]
  member_id bigint [not null, ref: > member.id]
  name varchar(100) [not null]
  type varchar(30) [not null]
  total_count integer
  remaining_count integer
  start_date date [not null]
  end_date date [not null]
  status varchar(30) [not null]
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint

  indexes {
    member_id
    (member_id, status)
    end_date
  }
}

Table schedule {
  id bigint [pk, increment]
  studio_id bigint [not null, ref: > studio.id]
  instructor_id bigint [not null, ref: > instructor.id]
  title varchar(100) [not null]
  type varchar(30) [not null]
  start_at timestamp [not null]
  end_at timestamp [not null]
  capacity integer [not null]
  booked_count integer [not null]
  status varchar(30) [not null]
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint

  indexes {
    studio_id
    instructor_id
    start_at
    (status, start_at)
  }
}

Table booking {
  id bigint [pk, increment]
  studio_id bigint [not null, ref: > studio.id]
  member_id bigint [not null, ref: > member.id]
  schedule_id bigint [not null, ref: > schedule.id]
  membership_id bigint [ref: > membership.id]
  status varchar(30) [not null]
  booked_at timestamp [not null]
  canceled_at timestamp
  cancel_reason varchar(255)
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint

  indexes {
    member_id
    schedule_id
    membership_id
    (schedule_id, status)
    booked_at
    (member_id, schedule_id) [name: "uq_booking_active_member_schedule", unique, note: "Partial unique index where status <> CANCELED"]
  }
}

Table attendance {
  id bigint [pk, increment]
  booking_id bigint [not null, ref: > booking.id, unique]
  member_id bigint [not null, ref: > member.id]
  schedule_id bigint [not null, ref: > schedule.id]
  status varchar(30) [not null]
  checked_at timestamp [not null]
  note varchar(500)
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint

  indexes {
    member_id
    schedule_id
    checked_at
  }
}

Table payment {
  id bigint [pk, increment]
  studio_id bigint [not null, ref: > studio.id]
  member_id bigint [not null, ref: > member.id]
  membership_id bigint [ref: > membership.id]
  order_name varchar(255) [not null]
  amount bigint [not null]
  method varchar(30) [not null]
  status varchar(30) [not null]
  membership_name varchar(255) [not null]
  membership_type varchar(50) [not null]
  membership_total_count integer [not null]
  membership_start_date date [not null]
  membership_end_date date [not null]
  paid_at timestamp
  canceled_at timestamp
  pg_provider varchar(50)
  pg_order_id varchar(100) [unique]
  pg_payment_key varchar(200)
  pg_raw_response text
  failed_reason varchar(500)
  idempotency_key varchar(100) [unique]
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint

  indexes {
    member_id
    status
    (member_id, status)
    paid_at
    pg_payment_key
  }
}

Table notification {
  id bigint [pk, increment]
  studio_id bigint [not null, ref: > studio.id]
  member_id bigint [not null, ref: > member.id]
  title varchar(255) [not null]
  content varchar(1000)
  type varchar(50)
  read_at timestamp
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint

  indexes {
    member_id
    (member_id, read_at)
    created_at
  }
}

Table refresh_token {
  id bigint [pk, increment]
  user_id bigint [not null, ref: > member.id, unique]
  token varchar(1000) [not null]
  expires_at timestamp [not null]
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint

  indexes {
    expires_at
  }
}

Table payment_webhook_log {
  id bigint [pk, increment]
  provider varchar(50) [not null]
  transmission_id varchar(100)
  event_type varchar(100) [not null]
  order_id varchar(100)
  payment_key varchar(200)
  payload text
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp
  updated_by bigint

  indexes {
    (provider, transmission_id) [unique]
  }
}

Table payment_operation {
  id bigint [pk, increment]
  payment_id bigint [not null, ref: > payment.id]
  operation_type varchar(20) [not null]
  idempotency_key varchar(100) [not null]
  request_hash varchar(64) [not null]
  status varchar(20) [not null]
  provider_reference varchar(200)
  response_payload text
  error_code varchar(100)
  error_message varchar(500)
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp
  updated_by bigint

  indexes {
    (payment_id, operation_type, idempotency_key) [unique]
  }
}
```

## 사용 방법

1. `dbdiagram.io`에서 새 다이어그램을 생성한다.
2. 위 `DBML` 블록 전체를 복사해 붙여 넣는다.
3. 필요하면 테이블 색상, 그룹, 노트를 추가해 시각적으로 보강한다.

## 참고

- `payment_webhook_log`는 논리 연결 테이블이므로 `payment.order_id`, `payment.payment_key`와의 매칭은 문서 해석으로 본다.
- `booking`의 `(member_id, schedule_id)` unique는 실제로는 partial unique index다.
- `payment_operation`은 엔티티와 migration 모두 존재하므로 최신 DBML에 포함한다.
