CREATE TABLE payment_webhook_log (
     id BIGSERIAL PRIMARY KEY,
     provider VARCHAR(50) NOT NULL,
     transmission_id VARCHAR(100),
     event_type VARCHAR(100) NOT NULL,
     order_id VARCHAR(100),
     payment_key VARCHAR(200),
     payload TEXT,
     created_at TIMESTAMP NOT NULL DEFAULT now(),
     created_by BIGINT,
     updated_at TIMESTAMP,
     updated_by BIGINT
);

CREATE UNIQUE INDEX uq_payment_webhook_log_provider_transmission_id
    ON payment_webhook_log(provider, transmission_id);
