ALTER TABLE payment
    ADD COLUMN idempotency_key VARCHAR(100);

CREATE UNIQUE INDEX uq_payment_pg_order_id ON payment(pg_order_id);
CREATE UNIQUE INDEX uq_payment_idempotency_key ON payment(idempotency_key);