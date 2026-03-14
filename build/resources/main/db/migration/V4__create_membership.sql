CREATE TABLE membership (
    id BIGSERIAL PRIMARY KEY,
    studio_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(30) NOT NULL,
    total_count INTEGER,
    remaining_count INTEGER,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    updated_at TIMESTAMP NOT NULL,
    updated_by BIGINT
);
