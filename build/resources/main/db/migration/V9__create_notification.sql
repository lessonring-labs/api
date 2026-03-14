CREATE TABLE notification (
    id BIGSERIAL PRIMARY KEY,
    studio_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content VARCHAR(1000),
    type VARCHAR(50),
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    updated_at TIMESTAMP NOT NULL,
    updated_by BIGINT
);