CREATE TABLE payment_webhook_event (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(100) NOT NULL,
    payment_key VARCHAR(100),
    event_type VARCHAR(50) NOT NULL,
    payload_hash VARCHAR(64) NOT NULL,
    status VARCHAR(20) NOT NULL,
    raw_payload TEXT,
    error_code VARCHAR(100),
    error_message VARCHAR(500),
    received_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,
    CONSTRAINT uq_payment_webhook_event_event_id UNIQUE (event_id)
);
