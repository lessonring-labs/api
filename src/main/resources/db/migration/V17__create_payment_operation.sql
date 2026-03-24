CREATE TABLE payment_operation (
       id BIGSERIAL PRIMARY KEY,

       payment_id BIGINT NOT NULL,
       operation_type VARCHAR(20) NOT NULL,

       idempotency_key VARCHAR(100) NOT NULL,
       request_hash VARCHAR(64) NOT NULL,

       status VARCHAR(20) NOT NULL,

       provider_reference VARCHAR(200),

       response_payload TEXT,

       error_code VARCHAR(100),
       error_message VARCHAR(500),

       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
       created_by BIGINT,
       updated_at TIMESTAMP,
       updated_by BIGINT
);

CREATE UNIQUE INDEX uq_payment_operation_key
    ON payment_operation (payment_id, operation_type, idempotency_key);