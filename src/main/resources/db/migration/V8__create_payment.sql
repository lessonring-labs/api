CREATE TABLE payment (
    id BIGSERIAL PRIMARY KEY,
    studio_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    membership_id BIGINT,
    amount BIGINT NOT NULL,
    method VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    paid_at TIMESTAMP NOT NULL,
    canceled_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    updated_at TIMESTAMP NOT NULL,
    updated_by BIGINT
);
