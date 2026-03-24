# ERD DBML

이 문서는 dbdiagram.io 등에서 바로 사용할 수 있도록 DBML 형식으로 정리한 문서다.

- 기준 문서: `docs/architecture/erd.md`
- 목적: 시각화 도구 import, 협업용 ERD 공유
- 주의: `payment_webhook_log`는 현재 코드상 `payment`와 문자열 키로 연결되므로 Ref를 강제하지 않았다.

## DBML

```dbml
Project lessonring_api {
  database_type: "PostgreSQL"
  Note: "Code-first logical ERD extracted from Spring Boot/JPA entities"
}

Table studio {
  id bigint [pk, increment]
  name varchar
  phone varchar
  address varchar
  detail_address varchar
  timezone varchar
  business_number varchar
  status varchar
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint
}

Table member {
  id bigint [pk, increment]
  studio_id bigint [not null, ref: > studio.id]
  name varchar
  phone varchar
  email varchar
  gender varchar
  birth_date date
  status varchar
  joined_at timestamp
  memo text
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint
}

Table instructor {
  id bigint [pk, increment]
  studio_id bigint [not null, ref: > studio.id]
  name varchar [not null]
  phone varchar
  email varchar
  profile_image_url varchar
  status varchar [not null]
  memo text
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint
}

Table schedule {
  id bigint [pk, increment]
  studio_id bigint [not null, ref: > studio.id]
  instructor_id bigint [not null, ref: > instructor.id]
  title varchar [not null]
  type varchar [not null]
  start_at timestamp [not null]
  end_at timestamp [not null]
  capacity int [not null]
  booked_count int [not null, default: 0]
  status varchar [not null]
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint
}

Table membership {
  id bigint [pk, increment]
  studio_id bigint [not null, ref: > studio.id]
  member_id bigint [not null, ref: > member.id]
  name varchar [not null]
  type varchar [not null]
  total_count int [not null]
  remaining_count int [not null]
  start_date date [not null]
  end_date date [not null]
  status varchar [not null]
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint
}

Table booking {
  id bigint [pk, increment]
  studio_id bigint [not null, ref: > studio.id]
  member_id bigint [not null, ref: > member.id]
  schedule_id bigint [not null, ref: > schedule.id]
  membership_id bigint [not null, ref: > membership.id]
  status varchar [not null]
  booked_at timestamp [not null]
  canceled_at timestamp
  cancel_reason varchar
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint
}

Table attendance {
  id bigint [pk, increment]
  booking_id bigint [not null, ref: > booking.id, unique]
  member_id bigint [not null, ref: > member.id]
  schedule_id bigint [not null, ref: > schedule.id]
  status varchar [not null]
  checked_at timestamp [not null]
  note text
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint
}

Table payment {
  id bigint [pk, increment]
  studio_id bigint [not null, ref: > studio.id]
  member_id bigint [not null, ref: > member.id]
  membership_id bigint [ref: > membership.id]
  order_name varchar [not null]
  method varchar [not null]
  status varchar [not null]
  amount bigint [not null]
  paid_at timestamp
  canceled_at timestamp
  membership_name varchar [not null]
  membership_type varchar [not null]
  membership_total_count int [not null]
  membership_start_date date [not null]
  membership_end_date date [not null]
  pg_provider varchar
  pg_order_id varchar [unique]
  pg_payment_key varchar
  pg_raw_response text
  failed_reason varchar
  idempotency_key varchar [unique]
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint
}

Table payment_webhook_log {
  id bigint [pk, increment]
  provider varchar [not null]
  transmission_id varchar
  event_type varchar [not null]
  order_id varchar
  payment_key varchar
  payload text
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint

  indexes {
    (provider, transmission_id) [unique]
  }
}

Table notification {
  id bigint [pk, increment]
  studio_id bigint [not null, ref: > studio.id]
  member_id bigint [not null, ref: > member.id]
  title varchar [not null]
  content text
  type varchar
  read_at timestamp
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint
}

Table refresh_token {
  id bigint [pk, increment]
  user_id bigint [not null, ref: > member.id, unique]
  token varchar [not null]
  expires_at timestamp [not null]
  created_at timestamp [not null]
  created_by bigint
  updated_at timestamp [not null]
  updated_by bigint
}
```

## 사용 방법

1. `dbdiagram.io`에서 새 다이어그램 생성
2. 위 `DBML` 블록 전체 복붙
3. 필요하면 도구 내에서 추가 색상/그룹만 보강
