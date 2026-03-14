CREATE TABLE schedule (
    id BIGSERIAL PRIMARY KEY,
    studio_id BIGINT NOT NULL,
    instructor_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    type VARCHAR(30) NOT NULL,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    capacity INTEGER NOT NULL,
    booked_count INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    updated_at TIMESTAMP NOT NULL,
    updated_by BIGINT
);
