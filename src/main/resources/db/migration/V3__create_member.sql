CREATE TABLE member (
    id BIGSERIAL PRIMARY KEY,
    studio_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(100),
    gender VARCHAR(20),
    birth_date DATE,
    status VARCHAR(30) NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    memo VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    updated_at TIMESTAMP NOT NULL,
    updated_by BIGINT
);
