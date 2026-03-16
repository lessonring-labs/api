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

CREATE INDEX IF NOT EXISTS idx_schedule_studio_id
    ON schedule (studio_id);

CREATE INDEX IF NOT EXISTS idx_schedule_instructor_id
    ON schedule (instructor_id);

CREATE INDEX IF NOT EXISTS idx_schedule_start_at
    ON schedule (start_at);

CREATE INDEX IF NOT EXISTS idx_schedule_status_start_at
    ON schedule (status, start_at);

CREATE INDEX IF NOT EXISTS idx_membership_member_id
    ON membership (member_id);

CREATE INDEX IF NOT EXISTS idx_membership_member_status
    ON membership (member_id, status);

CREATE INDEX IF NOT EXISTS idx_membership_end_date
    ON membership (end_date);

CREATE INDEX IF NOT EXISTS idx_payment_member_id
    ON payment (member_id);

CREATE INDEX IF NOT EXISTS idx_payment_status
    ON payment (status);

CREATE INDEX IF NOT EXISTS idx_payment_member_status
    ON payment (member_id, status);

CREATE INDEX IF NOT EXISTS idx_payment_paid_at
    ON payment (paid_at DESC);

CREATE INDEX IF NOT EXISTS idx_notification_member_id
    ON notification (member_id);

CREATE INDEX IF NOT EXISTS idx_notification_member_read_at
    ON notification (member_id, read_at);

CREATE INDEX IF NOT EXISTS idx_notification_created_at
    ON notification (created_at DESC);

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

CREATE UNIQUE INDEX IF NOT EXISTS uq_refresh_token_user_id
    ON refresh_token (user_id);

CREATE INDEX IF NOT EXISTS idx_refresh_token_expires_at
    ON refresh_token (expires_at);