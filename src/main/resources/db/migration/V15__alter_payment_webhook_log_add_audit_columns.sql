ALTER TABLE payment_webhook_log
    ADD COLUMN created_by VARCHAR(100),
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN updated_by VARCHAR(100);