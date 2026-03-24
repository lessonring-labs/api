-- =========================================================
-- Core tables
-- =========================================================

-- studio
create table if not exists studio (
    id bigserial primary key,
    name varchar(100) not null,
    phone varchar(30) not null,
    address varchar(255) not null,
    detail_address varchar(255),
    timezone varchar(50) not null,
    business_number varchar(30),
    status varchar(30) not null,
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint
);

-- instructor
create table if not exists instructor (
    id bigserial primary key,
    studio_id bigint not null,
    name varchar(100) not null,
    phone varchar(30) not null,
    email varchar(100),
    profile_image_url varchar(255),
    status varchar(30) not null,
    memo varchar(500),
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint fk_instructor_studio foreign key (studio_id) references studio(id)
);

-- member
create table if not exists member (
    id bigserial primary key,
    studio_id bigint not null,
    name varchar(100) not null,
    phone varchar(30) not null,
    email varchar(100),
    gender varchar(20),
    birth_date date,
    status varchar(30) not null,
    joined_at timestamp not null,
    memo varchar(500),
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint fk_member_studio foreign key (studio_id) references studio(id)
);

-- membership
create table if not exists membership (
    id bigserial primary key,
    studio_id bigint not null,
    member_id bigint not null,
    name varchar(100) not null,
    type varchar(30) not null,
    total_count integer,
    remaining_count integer,
    start_date date not null,
    end_date date not null,
    status varchar(30) not null,
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint fk_membership_studio foreign key (studio_id) references studio(id),
    constraint fk_membership_member foreign key (member_id) references member(id)
);

-- schedule
create table if not exists schedule (
    id bigserial primary key,
    studio_id bigint not null,
    instructor_id bigint not null,
    title varchar(100) not null,
    type varchar(30) not null,
    start_at timestamp not null,
    end_at timestamp not null,
    capacity integer not null,
    booked_count integer not null,
    status varchar(30) not null,
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint fk_schedule_studio foreign key (studio_id) references studio(id),
    constraint fk_schedule_instructor foreign key (instructor_id) references instructor(id)
);

-- booking
create table if not exists booking (
    id bigserial primary key,
    studio_id bigint not null,
    member_id bigint not null,
    schedule_id bigint not null,
    membership_id bigint,
    status varchar(30) not null,
    booked_at timestamp not null,
    canceled_at timestamp,
    cancel_reason varchar(255),
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint fk_booking_studio foreign key (studio_id) references studio(id),
    constraint fk_booking_member foreign key (member_id) references member(id),
    constraint fk_booking_schedule foreign key (schedule_id) references schedule(id),
    constraint fk_booking_membership foreign key (membership_id) references membership(id)
);

-- attendance
create table if not exists attendance (
    id bigserial primary key,
    booking_id bigint not null,
    member_id bigint not null,
    schedule_id bigint not null,
    status varchar(30) not null,
    checked_at timestamp not null,
    note varchar(500),
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint fk_attendance_booking foreign key (booking_id) references booking(id),
    constraint fk_attendance_member foreign key (member_id) references member(id),
    constraint fk_attendance_schedule foreign key (schedule_id) references schedule(id),
    constraint uq_attendance_booking unique (booking_id)
);

-- payment
create table if not exists payment (
    id bigserial primary key,
    studio_id bigint not null,
    member_id bigint not null,
    membership_id bigint,
    order_name varchar(255) not null,
    amount bigint not null,
    method varchar(30) not null,
    status varchar(30) not null,
    membership_name varchar(255) not null,
    membership_type varchar(50) not null,
    membership_total_count integer not null,
    membership_start_date date not null,
    membership_end_date date not null,
    paid_at timestamp,
    canceled_at timestamp,
    pg_provider varchar(50),
    pg_order_id varchar(100),
    pg_payment_key varchar(200),
    pg_raw_response text,
    failed_reason varchar(500),
    idempotency_key varchar(100),
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint fk_payment_studio foreign key (studio_id) references studio(id),
    constraint fk_payment_member foreign key (member_id) references member(id),
    constraint fk_payment_membership foreign key (membership_id) references membership(id)
);

-- notification
create table if not exists notification (
    id bigserial primary key,
    studio_id bigint not null,
    member_id bigint not null,
    title varchar(255) not null,
    content varchar(1000),
    type varchar(50),
    read_at timestamp,
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint fk_notification_studio foreign key (studio_id) references studio(id),
    constraint fk_notification_member foreign key (member_id) references member(id)
);

-- refresh_token
create table if not exists refresh_token (
    id bigserial primary key,
    user_id bigint not null,
    token varchar(1000) not null,
    expires_at timestamp not null,
    created_at timestamp not null,
    created_by bigint,
    updated_at timestamp not null,
    updated_by bigint,
    constraint fk_refresh_token_user foreign key (user_id) references member(id),
    constraint uq_refresh_token_user unique (user_id)
);

-- =========================================================
-- Payment support tables
-- =========================================================

-- payment_webhook_log
create table if not exists payment_webhook_log (
    id bigserial primary key,
    provider varchar(50) not null,
    transmission_id varchar(100),
    event_type varchar(100) not null,
    order_id varchar(100),
    payment_key varchar(200),
    payload text,
    created_at timestamp not null default now(),
    created_by bigint,
    updated_at timestamp,
    updated_by bigint,
    constraint uq_payment_webhook_log_provider_transmission_id unique (provider, transmission_id)
);

-- payment_operation
create table if not exists payment_operation (
    id bigserial primary key,
    payment_id bigint not null,
    operation_type varchar(20) not null,
    idempotency_key varchar(100) not null,
    request_hash varchar(64) not null,
    status varchar(20) not null,
    provider_reference varchar(200),
    response_payload text,
    error_code varchar(100),
    error_message varchar(500),
    created_at timestamp not null default now(),
    created_by bigint,
    updated_at timestamp,
    updated_by bigint,
    constraint uq_payment_operation_key unique (payment_id, operation_type, idempotency_key)
);

-- =========================================================
-- Indexes and unique indexes
-- =========================================================

-- membership
create index if not exists idx_membership_member_id
    on membership (member_id);

create index if not exists idx_membership_member_status
    on membership (member_id, status);

create index if not exists idx_membership_end_date
    on membership (end_date);

-- schedule
create index if not exists idx_schedule_studio_id
    on schedule (studio_id);

create index if not exists idx_schedule_instructor_id
    on schedule (instructor_id);

create index if not exists idx_schedule_start_at
    on schedule (start_at);

create index if not exists idx_schedule_status_start_at
    on schedule (status, start_at);

-- booking
create unique index if not exists uq_booking_active_member_schedule
    on booking (member_id, schedule_id)
    where status <> 'CANCELED';

create index if not exists idx_booking_member_id
    on booking (member_id);

create index if not exists idx_booking_schedule_id
    on booking (schedule_id);

create index if not exists idx_booking_membership_id
    on booking (membership_id);

create index if not exists idx_booking_schedule_status
    on booking (schedule_id, status);

create index if not exists idx_booking_booked_at
    on booking (booked_at desc);

-- attendance
create index if not exists idx_attendance_member_id
    on attendance (member_id);

create index if not exists idx_attendance_schedule_id
    on attendance (schedule_id);

create index if not exists idx_attendance_checked_at
    on attendance (checked_at desc);

-- payment
create index if not exists idx_payment_member_id
    on payment (member_id);

create index if not exists idx_payment_status
    on payment (status);

create index if not exists idx_payment_member_status
    on payment (member_id, status);

create index if not exists idx_payment_paid_at
    on payment (paid_at desc);

create unique index if not exists uq_payment_pg_order_id
    on payment (pg_order_id);

create unique index if not exists uq_payment_idempotency_key
    on payment (idempotency_key);

create index if not exists idx_payment_pg_payment_key
    on payment (pg_payment_key);

-- notification
create index if not exists idx_notification_member_id
    on notification (member_id);

create index if not exists idx_notification_member_read_at
    on notification (member_id, read_at);

create index if not exists idx_notification_created_at
    on notification (created_at desc);

-- refresh_token
create index if not exists idx_refresh_token_expires_at
    on refresh_token (expires_at);
