#!/usr/bin/env bash
set -euo pipefail

DB_USER="${DB_USER:-devyn}"
DB_NAME="${DB_NAME:-lessonring}"
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_PASSWORD="${DB_PASSWORD:-}"

export PGPASSWORD="$DB_PASSWORD"

psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" <<'SQL'
BEGIN;

DELETE FROM attendance;
DELETE FROM booking;
DELETE FROM payment;
DELETE FROM schedule;
DELETE FROM membership;
DELETE FROM member;
DELETE FROM instructor;
DELETE FROM studio;

DO $$
DECLARE
    cols text[];
    col text;
    insert_cols text := 'id';
    insert_vals text := '1';
BEGIN
    SELECT array_agg(column_name::text ORDER BY ordinal_position)
    INTO cols
    FROM information_schema.columns
    WHERE table_name = 'studio'
      AND table_schema = 'public';

    FOREACH col IN ARRAY cols LOOP
        IF col = 'id' THEN
            CONTINUE;
        ELSIF col = 'name' THEN
            insert_cols := insert_cols || ', name';
            insert_vals := insert_vals || ', ''LessonRing Main Studio''';
        ELSIF col = 'phone' THEN
            insert_cols := insert_cols || ', phone';
            insert_vals := insert_vals || ', ''01000000000''';
        ELSIF col = 'email' THEN
            insert_cols := insert_cols || ', email';
            insert_vals := insert_vals || ', ''studio@lessonring.local''';
        ELSIF col = 'address' THEN
            insert_cols := insert_cols || ', address';
            insert_vals := insert_vals || ', ''Busan''';
        ELSIF col = 'detail_address' THEN
            insert_cols := insert_cols || ', detail_address';
            insert_vals := insert_vals || ', ''Centum''';
        ELSIF col = 'zipcode' THEN
            insert_cols := insert_cols || ', zipcode';
            insert_vals := insert_vals || ', ''48000''';
        ELSIF col = 'timezone' THEN
            insert_cols := insert_cols || ', timezone';
            insert_vals := insert_vals || ', ''Asia/Seoul''';
        ELSIF col = 'description' THEN
            insert_cols := insert_cols || ', description';
            insert_vals := insert_vals || ', ''Main studio''';
        ELSIF col = 'status' THEN
            insert_cols := insert_cols || ', status';
            insert_vals := insert_vals || ', ''ACTIVE''';
        ELSIF col = 'created_at' THEN
            insert_cols := insert_cols || ', created_at';
            insert_vals := insert_vals || ', NOW()';
        ELSIF col = 'created_by' THEN
            insert_cols := insert_cols || ', created_by';
            insert_vals := insert_vals || ', 0';
        ELSIF col = 'updated_at' THEN
            insert_cols := insert_cols || ', updated_at';
            insert_vals := insert_vals || ', NOW()';
        ELSIF col = 'updated_by' THEN
            insert_cols := insert_cols || ', updated_by';
            insert_vals := insert_vals || ', 0';
        END IF;
    END LOOP;

    EXECUTE format('INSERT INTO studio (%s) VALUES (%s)', insert_cols, insert_vals);
END $$;

DO $$
DECLARE
    cols text[];
    col text;
    insert_cols text := 'id';
    insert_vals text := '1';
BEGIN
    SELECT array_agg(column_name::text ORDER BY ordinal_position)
    INTO cols
    FROM information_schema.columns
    WHERE table_name = 'instructor'
      AND table_schema = 'public';

    FOREACH col IN ARRAY cols LOOP
        IF col = 'id' THEN
            CONTINUE;
        ELSIF col = 'studio_id' THEN
            insert_cols := insert_cols || ', studio_id';
            insert_vals := insert_vals || ', 1';
        ELSIF col = 'name' THEN
            insert_cols := insert_cols || ', name';
            insert_vals := insert_vals || ', ''Instructor A''';
        ELSIF col = 'phone' THEN
            insert_cols := insert_cols || ', phone';
            insert_vals := insert_vals || ', ''01011111111''';
        ELSIF col = 'email' THEN
            insert_cols := insert_cols || ', email';
            insert_vals := insert_vals || ', ''instructor@lessonring.local''';
        ELSIF col = 'gender' THEN
            insert_cols := insert_cols || ', gender';
            insert_vals := insert_vals || ', ''MALE''';
        ELSIF col = 'specialty' THEN
            insert_cols := insert_cols || ', specialty';
            insert_vals := insert_vals || ', ''PILATES''';
        ELSIF col = 'status' THEN
            insert_cols := insert_cols || ', status';
            insert_vals := insert_vals || ', ''ACTIVE''';
        ELSIF col = 'created_at' THEN
            insert_cols := insert_cols || ', created_at';
            insert_vals := insert_vals || ', NOW()';
        ELSIF col = 'created_by' THEN
            insert_cols := insert_cols || ', created_by';
            insert_vals := insert_vals || ', 0';
        ELSIF col = 'updated_at' THEN
            insert_cols := insert_cols || ', updated_at';
            insert_vals := insert_vals || ', NOW()';
        ELSIF col = 'updated_by' THEN
            insert_cols := insert_cols || ', updated_by';
            insert_vals := insert_vals || ', 0';
        END IF;
    END LOOP;

    EXECUTE format('INSERT INTO instructor (%s) VALUES (%s)', insert_cols, insert_vals);
END $$;

DO $$
DECLARE
    cols text[];
    col text;
    insert_cols text := 'id';
    insert_vals text := '1';
BEGIN
    SELECT array_agg(column_name::text ORDER BY ordinal_position)
    INTO cols
    FROM information_schema.columns
    WHERE table_name = 'member'
      AND table_schema = 'public';

    FOREACH col IN ARRAY cols LOOP
        IF col = 'id' THEN
            CONTINUE;
        ELSIF col = 'studio_id' THEN
            insert_cols := insert_cols || ', studio_id';
            insert_vals := insert_vals || ', 1';
        ELSIF col = 'name' THEN
            insert_cols := insert_cols || ', name';
            insert_vals := insert_vals || ', ''Member A''';
        ELSIF col = 'gender' THEN
            insert_cols := insert_cols || ', gender';
            insert_vals := insert_vals || ', ''MALE''';
        ELSIF col = 'phone' THEN
            insert_cols := insert_cols || ', phone';
            insert_vals := insert_vals || ', ''01012345678''';
        ELSIF col = 'email' THEN
            insert_cols := insert_cols || ', email';
            insert_vals := insert_vals || ', ''member@lessonring.local''';
        ELSIF col = 'joined_at' THEN
            insert_cols := insert_cols || ', joined_at';
            insert_vals := insert_vals || ', NOW()';
        ELSIF col = 'birth_date' THEN
            insert_cols := insert_cols || ', birth_date';
            insert_vals := insert_vals || ', DATE ''1990-01-01''';
        ELSIF col = 'status' THEN
            insert_cols := insert_cols || ', status';
            insert_vals := insert_vals || ', ''ACTIVE''';
        ELSIF col = 'created_at' THEN
            insert_cols := insert_cols || ', created_at';
            insert_vals := insert_vals || ', NOW()';
        ELSIF col = 'created_by' THEN
            insert_cols := insert_cols || ', created_by';
            insert_vals := insert_vals || ', 0';
        ELSIF col = 'updated_at' THEN
            insert_cols := insert_cols || ', updated_at';
            insert_vals := insert_vals || ', NOW()';
        ELSIF col = 'updated_by' THEN
            insert_cols := insert_cols || ', updated_by';
            insert_vals := insert_vals || ', 0';
        END IF;
    END LOOP;

    EXECUTE format('INSERT INTO member (%s) VALUES (%s)', insert_cols, insert_vals);
END $$;

INSERT INTO membership (
    id,
    studio_id,
    member_id,
    name,
    type,
    total_count,
    remaining_count,
    start_date,
    end_date,
    status,
    created_at,
    created_by,
    updated_at,
    updated_by
) VALUES
(
    1,
    1,
    1,
    '10 Session Pass',
    'COUNT',
    10,
    9,
    DATE '2026-03-14',
    DATE '2026-04-30',
    'ACTIVE',
    NOW(),
    0,
    NOW(),
    0
),
(
    2,
    1,
    1,
    '20 Session Pass',
    'COUNT',
    20,
    20,
    DATE '2026-03-15',
    DATE '2026-05-31',
    'ACTIVE',
    NOW(),
    0,
    NOW(),
    0
);

INSERT INTO schedule (
    id,
    studio_id,
    instructor_id,
    title,
    type,
    start_at,
    end_at,
    capacity,
    booked_count,
    status,
    created_at,
    created_by,
    updated_at,
    updated_by
) VALUES
(
    1,
    1,
    1,
    'Pilates Group A',
    'GROUP',
    TIMESTAMP '2026-03-20 19:00:00',
    TIMESTAMP '2026-03-20 20:00:00',
    10,
    1,
    'OPEN',
    NOW(),
    0,
    NOW(),
    0
),
(
    2,
    1,
    1,
    'Pilates Group B',
    'GROUP',
    TIMESTAMP '2026-03-21 19:00:00',
    TIMESTAMP '2026-03-21 20:00:00',
    10,
    1,
    'OPEN',
    NOW(),
    0,
    NOW(),
    0
);

INSERT INTO booking (
    id,
    studio_id,
    member_id,
    schedule_id,
    membership_id,
    status,
    booked_at,
    canceled_at,
    cancel_reason,
    created_at,
    created_by,
    updated_at,
    updated_by
) VALUES
(
    1,
    1,
    1,
    1,
    1,
    'CANCELED',
    TIMESTAMP '2026-03-14 14:36:34',
    TIMESTAMP '2026-03-14 14:36:59',
    'user canceled',
    NOW(),
    0,
    NOW(),
    0
),
(
    2,
    1,
    1,
    1,
    1,
    'ATTENDED',
    TIMESTAMP '2026-03-15 13:40:29',
    NULL,
    NULL,
    NOW(),
    0,
    NOW(),
    0
),
(
    3,
    1,
    1,
    2,
    1,
    'RESERVED',
    TIMESTAMP '2026-03-15 15:00:00',
    NULL,
    NULL,
    NOW(),
    0,
    NOW(),
    0
);

INSERT INTO attendance (
    id,
    booking_id,
    member_id,
    schedule_id,
    status,
    checked_at,
    note,
    created_at,
    created_by,
    updated_at,
    updated_by
) VALUES
(
    1,
    2,
    1,
    1,
    'ATTENDED',
    TIMESTAMP '2026-03-15 13:46:11',
    'checked in',
    NOW(),
    0,
    NOW(),
    0
);

INSERT INTO payment (
    id,
    studio_id,
    member_id,
    membership_id,
    order_name,
    amount,
    method,
    status,
    membership_name,
    membership_type,
    membership_total_count,
    membership_start_date,
    membership_end_date,
    paid_at,
    canceled_at,
    created_at,
    created_by,
    updated_at,
    updated_by
) VALUES
(
    1,
    1,
    1,
    NULL,
    '10 Session Pass Payment',
    300000,
    'CARD',
    'READY',
    '10 Session Pass',
    'COUNT',
    10,
    DATE '2026-03-15',
    DATE '2026-04-30',
    NULL,
    NULL,
    NOW(),
    0,
    NOW(),
    0
),
(
    2,
    1,
    1,
    2,
    '20 Session Pass Payment',
    550000,
    'CARD',
    'COMPLETED',
    '20 Session Pass',
    'COUNT',
    20,
    DATE '2026-03-15',
    DATE '2026-05-31',
    TIMESTAMP '2026-03-15 16:00:00',
    NULL,
    NOW(),
    0,
    NOW(),
    0
);

SELECT setval('studio_id_seq', COALESCE((SELECT MAX(id) FROM studio), 1), true);
SELECT setval('instructor_id_seq', COALESCE((SELECT MAX(id) FROM instructor), 1), true);
SELECT setval('member_id_seq', COALESCE((SELECT MAX(id) FROM member), 1), true);
SELECT setval('membership_id_seq', COALESCE((SELECT MAX(id) FROM membership), 1), true);
SELECT setval('schedule_id_seq', COALESCE((SELECT MAX(id) FROM schedule), 1), true);
SELECT setval('booking_id_seq', COALESCE((SELECT MAX(id) FROM booking), 1), true);
SELECT setval('attendance_id_seq', COALESCE((SELECT MAX(id) FROM attendance), 1), true);
SELECT setval('payment_id_seq', COALESCE((SELECT MAX(id) FROM payment), 1), true);

COMMIT;

SELECT 'studio' AS table_name, COUNT(*) AS cnt FROM studio
UNION ALL
SELECT 'instructor', COUNT(*) FROM instructor
UNION ALL
SELECT 'member', COUNT(*) FROM member
UNION ALL
SELECT 'membership', COUNT(*) FROM membership
UNION ALL
SELECT 'schedule', COUNT(*) FROM schedule
UNION ALL
SELECT 'booking', COUNT(*) FROM booking
UNION ALL
SELECT 'attendance', COUNT(*) FROM attendance
UNION ALL
SELECT 'payment', COUNT(*) FROM payment
ORDER BY table_name;
SQL
