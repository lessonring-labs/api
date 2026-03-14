CREATE TABLE booking (
    id BIGSERIAL PRIMARY KEY,
    studio_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    schedule_id BIGINT NOT NULL,
    membership_id BIGINT,
    status VARCHAR(30) NOT NULL,
    booked_at TIMESTAMP NOT NULL,
    canceled_at TIMESTAMP,
    cancel_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    updated_at TIMESTAMP NOT NULL,
    updated_by BIGINT
);
