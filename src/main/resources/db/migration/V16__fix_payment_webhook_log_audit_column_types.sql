UPDATE payment_webhook_log
SET created_by = NULL
WHERE created_by IS NOT NULL
  AND created_by !~ '^[0-9]+$';

UPDATE payment_webhook_log
SET updated_by = NULL
WHERE updated_by IS NOT NULL
  AND updated_by !~ '^[0-9]+$';

ALTER TABLE payment_webhook_log
ALTER COLUMN created_by TYPE bigint USING created_by::bigint;

ALTER TABLE payment_webhook_log
ALTER COLUMN updated_by TYPE bigint USING updated_by::bigint;