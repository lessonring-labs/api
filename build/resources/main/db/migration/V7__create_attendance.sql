CREATE TABLE attendance (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    schedule_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    checked_at TIMESTAMP NOT NULL,
    note VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    updated_at TIMESTAMP NOT NULL,
    updated_by BIGINT
);
