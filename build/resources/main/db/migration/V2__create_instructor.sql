CREATE TABLE instructor (
    id BIGSERIAL PRIMARY KEY,
    studio_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    email VARCHAR(100),
    profile_image_url VARCHAR(255),
    status VARCHAR(30) NOT NULL,
    memo VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    updated_at TIMESTAMP NOT NULL,
    updated_by BIGINT
);
