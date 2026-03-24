# ERD DDL

이 문서는 현재 코드 기반 ERD를 PostgreSQL DDL 형태로 정리한 초안이다.

- 기준 문서: `docs/architecture/erd.md`
- 목적: 신규 스키마 생성 또는 리뷰용 초안
- 주의: 현재 애플리케이션은 엔티티에 FK를 명시하지 않으므로, 아래 DDL의 FK/UNIQUE는 운영 안정성을 위해 제안된 값이다.

## PostgreSQL DDL

```sql
create table studio (
    id bigserial primary key,
    name varchar(255),
    phone varchar(255),
    address varchar(255),
    detail_address varchar(255),
    timezone varchar(100),
    business_number varchar(100),
    status varchar(50),
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint
);

create table member (
    id bigserial primary key,
    studio_id bigint not null,
    name varchar(255),
    phone varchar(255),
    email varchar(255),
    gender varchar(50),
    birth_date date,
    status varchar(50),
    joined_at timestamp,
    memo text,
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint fk_member_studio foreign key (studio_id) references studio(id)
);

create table instructor (
    id bigserial primary key,
    studio_id bigint not null,
    name varchar(255) not null,
    phone varchar(255),
    email varchar(255),
    profile_image_url varchar(1000),
    status varchar(50) not null,
    memo text,
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint fk_instructor_studio foreign key (studio_id) references studio(id)
);

create table schedule (
    id bigserial primary key,
    studio_id bigint not null,
    instructor_id bigint not null,
    title varchar(255) not null,
    type varchar(50) not null,
    start_at timestamp not null,
    end_at timestamp not null,
    capacity integer not null,
    booked_count integer not null default 0,
    status varchar(50) not null,
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint fk_schedule_studio foreign key (studio_id) references studio(id),
    constraint fk_schedule_instructor foreign key (instructor_id) references instructor(id),
    constraint chk_schedule_capacity_non_negative check (capacity >= 0),
    constraint chk_schedule_booked_count_non_negative check (booked_count >= 0),
    constraint chk_schedule_booked_count_capacity check (booked_count <= capacity),
    constraint chk_schedule_time_range check (start_at < end_at)
);

create table membership (
    id bigserial primary key,
    studio_id bigint not null,
    member_id bigint not null,
    name varchar(255) not null,
    type varchar(50) not null,
    total_count integer not null,
    remaining_count integer not null,
    start_date date not null,
    end_date date not null,
    status varchar(50) not null,
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint fk_membership_studio foreign key (studio_id) references studio(id),
    constraint fk_membership_member foreign key (member_id) references member(id),
    constraint chk_membership_date_range check (start_date <= end_date),
    constraint chk_membership_total_count_non_negative check (total_count >= 0),
    constraint chk_membership_remaining_count_non_negative check (remaining_count >= 0),
    constraint chk_membership_remaining_total check (remaining_count <= total_count)
);

create table booking (
    id bigserial primary key,
    studio_id bigint not null,
    member_id bigint not null,
    schedule_id bigint not null,
    membership_id bigint not null,
    status varchar(50) not null,
    booked_at timestamp not null,
    canceled_at timestamp,
    cancel_reason varchar(1000),
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint fk_booking_studio foreign key (studio_id) references studio(id),
    constraint fk_booking_member foreign key (member_id) references member(id),
    constraint fk_booking_schedule foreign key (schedule_id) references schedule(id),
    constraint fk_booking_membership foreign key (membership_id) references membership(id)
);

create table attendance (
    id bigserial primary key,
    booking_id bigint not null,
    member_id bigint not null,
    schedule_id bigint not null,
    status varchar(50) not null,
    checked_at timestamp not null,
    note text,
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint fk_attendance_booking foreign key (booking_id) references booking(id),
    constraint fk_attendance_member foreign key (member_id) references member(id),
    constraint fk_attendance_schedule foreign key (schedule_id) references schedule(id),
    constraint uk_attendance_booking unique (booking_id)
);

create table payment (
    id bigserial primary key,
    studio_id bigint not null,
    member_id bigint not null,
    membership_id bigint,
    order_name varchar(255) not null,
    method varchar(50) not null,
    status varchar(50) not null,
    amount bigint not null,
    paid_at timestamp,
    canceled_at timestamp,
    membership_name varchar(255) not null,
    membership_type varchar(50) not null,
    membership_total_count integer not null,
    membership_start_date date not null,
    membership_end_date date not null,
    pg_provider varchar(100),
    pg_order_id varchar(255),
    pg_payment_key varchar(255),
    pg_raw_response text,
    failed_reason varchar(1000),
    idempotency_key varchar(255),
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint fk_payment_studio foreign key (studio_id) references studio(id),
    constraint fk_payment_member foreign key (member_id) references member(id),
    constraint fk_payment_membership foreign key (membership_id) references membership(id),
    constraint uk_payment_idempotency_key unique (idempotency_key),
    constraint uk_payment_pg_order_id unique (pg_order_id),
    constraint chk_payment_amount_positive check (amount >= 0),
    constraint chk_payment_membership_date_range check (membership_start_date <= membership_end_date),
    constraint chk_payment_membership_total_count_non_negative check (membership_total_count >= 0)
);

create table payment_webhook_log (
    id bigserial primary key,
    provider varchar(100) not null,
    transmission_id varchar(255),
    event_type varchar(100) not null,
    order_id varchar(255),
    payment_key varchar(255),
    payload text,
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint uk_payment_webhook_provider_transmission unique (provider, transmission_id)
);

create table notification (
    id bigserial primary key,
    studio_id bigint not null,
    member_id bigint not null,
    title varchar(255) not null,
    content text,
    type varchar(100),
    read_at timestamp,
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint fk_notification_studio foreign key (studio_id) references studio(id),
    constraint fk_notification_member foreign key (member_id) references member(id)
);

create table refresh_token (
    id bigserial primary key,
    user_id bigint not null,
    token varchar(1000) not null,
    expires_at timestamp not null,
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint fk_refresh_token_user foreign key (user_id) references member(id),
    constraint uk_refresh_token_user unique (user_id)
);

create index idx_member_studio_id on member (studio_id);
create index idx_instructor_studio_id on instructor (studio_id);
create index idx_schedule_studio_id on schedule (studio_id);
create index idx_schedule_instructor_id on schedule (instructor_id);
create index idx_schedule_start_at on schedule (start_at);
create index idx_membership_studio_id on membership (studio_id);
create index idx_membership_member_id on membership (member_id);
create index idx_booking_studio_id on booking (studio_id);
create index idx_booking_member_id on booking (member_id);
create index idx_booking_schedule_id on booking (schedule_id);
create index idx_booking_membership_id on booking (membership_id);
create index idx_booking_status on booking (status);
create index idx_attendance_member_id on attendance (member_id);
create index idx_attendance_schedule_id on attendance (schedule_id);
create index idx_payment_studio_id on payment (studio_id);
create index idx_payment_member_id on payment (member_id);
create index idx_payment_membership_id on payment (membership_id);
create index idx_payment_status on payment (status);
create index idx_payment_pg_payment_key on payment (pg_payment_key);
create index idx_notification_member_id on notification (member_id);
create index idx_notification_studio_id on notification (studio_id);
create index idx_refresh_token_expires_at on refresh_token (expires_at);
```

## 적용 전 확인 필요

- `payment.membership_id`는 결제 완료 전에는 `null`이므로 nullable 유지가 맞다.
- `payment_webhook_log`는 `payment`와 직접 FK를 두지 않았다.
- 현재 로직은 webhook `order_id`와 `payment.pg_order_id`를 기준으로 연결된다.
