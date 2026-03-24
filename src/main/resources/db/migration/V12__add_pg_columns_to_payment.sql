ALTER TABLE payment
    ADD COLUMN pg_provider VARCHAR(50),
    ADD COLUMN pg_order_id VARCHAR(100),
    ADD COLUMN pg_payment_key VARCHAR(200),
    ADD COLUMN pg_raw_response TEXT,
    ADD COLUMN failed_reason VARCHAR(500);

CREATE INDEX idx_payment_pg_order_id ON payment(pg_order_id);
CREATE INDEX idx_payment_pg_payment_key ON payment(pg_payment_key);