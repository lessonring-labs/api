-- V1: studio
-- No additional indexes or constraints are currently required.

-- V2: instructor
-- No additional indexes or constraints are currently required.

-- V3: member
-- No additional indexes or constraints are currently required.

-- V4: membership
CREATE INDEX IF NOT EXISTS idx_membership_member_id
    ON membership (member_id);

CREATE INDEX IF NOT EXISTS idx_membership_member_status
    ON membership (member_id, status);

CREATE INDEX IF NOT EXISTS idx_membership_end_date
    ON membership (end_date);

-- V5: schedule
CREATE INDEX IF NOT EXISTS idx_schedule_studio_id
    ON schedule (studio_id);

CREATE INDEX IF NOT EXISTS idx_schedule_instructor_id
    ON schedule (instructor_id);

CREATE INDEX IF NOT EXISTS idx_schedule_start_at
    ON schedule (start_at);

CREATE INDEX IF NOT EXISTS idx_schedule_status_start_at
    ON schedule (status, start_at);

-- V6: booking
CREATE UNIQUE INDEX IF NOT EXISTS uq_booking_active_member_schedule
    ON booking (member_id, schedule_id)
    WHERE status <> 'CANCELED';

CREATE INDEX IF NOT EXISTS idx_booking_member_id
    ON booking (member_id);

CREATE INDEX IF NOT EXISTS idx_booking_schedule_id
    ON booking (schedule_id);

CREATE INDEX IF NOT EXISTS idx_booking_membership_id
    ON booking (membership_id);

CREATE INDEX IF NOT EXISTS idx_booking_schedule_status
    ON booking (schedule_id, status);

CREATE INDEX IF NOT EXISTS idx_booking_booked_at
    ON booking (booked_at DESC);

-- V7: attendance
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_attendance_booking'
    ) THEN
        ALTER TABLE attendance
            ADD CONSTRAINT uq_attendance_booking UNIQUE (booking_id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_attendance_member_id
    ON attendance (member_id);

CREATE INDEX IF NOT EXISTS idx_attendance_schedule_id
    ON attendance (schedule_id);

CREATE INDEX IF NOT EXISTS idx_attendance_checked_at
    ON attendance (checked_at DESC);

-- V8: payment
CREATE INDEX IF NOT EXISTS idx_payment_member_id
    ON payment (member_id);

CREATE INDEX IF NOT EXISTS idx_payment_status
    ON payment (status);

CREATE INDEX IF NOT EXISTS idx_payment_member_status
    ON payment (member_id, status);

CREATE INDEX IF NOT EXISTS idx_payment_paid_at
    ON payment (paid_at DESC);

CREATE UNIQUE INDEX IF NOT EXISTS uq_payment_pg_order_id
    ON payment (pg_order_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_payment_idempotency_key
    ON payment (idempotency_key);

CREATE INDEX IF NOT EXISTS idx_payment_pg_payment_key
    ON payment (pg_payment_key);

-- V9: notification
CREATE INDEX IF NOT EXISTS idx_notification_member_id
    ON notification (member_id);

CREATE INDEX IF NOT EXISTS idx_notification_member_read_at
    ON notification (member_id, read_at);

CREATE INDEX IF NOT EXISTS idx_notification_created_at
    ON notification (created_at DESC);

-- V10: refresh_token
CREATE UNIQUE INDEX IF NOT EXISTS uq_refresh_token_user_id
    ON refresh_token (user_id);

CREATE INDEX IF NOT EXISTS idx_refresh_token_expires_at
    ON refresh_token (expires_at);

-- V11: payment_webhook_log
-- Unique index is created in V11__create_payment_webhook_log.sql.

-- V12: payment_operation
-- Unique index is created in V12__create_payment_operation.sql.

-- V13: payment_webhook_event
CREATE INDEX IF NOT EXISTS idx_payment_webhook_event_payment_key
    ON payment_webhook_event (payment_key);

CREATE INDEX IF NOT EXISTS idx_payment_webhook_event_event_type
    ON payment_webhook_event (event_type);

CREATE INDEX IF NOT EXISTS idx_payment_webhook_event_status
    ON payment_webhook_event (status);
