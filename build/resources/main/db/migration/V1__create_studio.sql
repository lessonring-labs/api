CREATE TABLE studio (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    address VARCHAR(255) NOT NULL,
    detail_address VARCHAR(255),
    timezone VARCHAR(50) NOT NULL,
    business_number VARCHAR(30),
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    updated_at TIMESTAMP NOT NULL,
    updated_by BIGINT
);
