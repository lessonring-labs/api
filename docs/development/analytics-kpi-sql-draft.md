# Analytics KPI SQL Draft

이 문서는 LessonRing Backend의 현재 스키마를 기준으로, 운영 및 정합성 KPI를 산출하기 위한 초기 SQL 초안이다.

기준:

- DB: PostgreSQL
- timezone: `Asia/Seoul`
- 기준 테이블: `member`, `membership`, `schedule`, `booking`, `attendance`, `payment`, `notification`, `payment_operation`, `payment_webhook_log`

주의:

- 아래 SQL은 운영 스키마 기준 초안이다.
- 실제 배치/대시보드 연결 전 실행 계획과 인덱스를 검토해야 한다.
- 날짜 기준은 `created_at`, `booked_at`, `checked_at`, `paid_at`, `canceled_at` 중 KPI 의미에 맞는 컬럼을 사용한다.

---

# 1. 운영 KPI SQL

## 1.1 일별 신규 회원 수

```sql
SELECT DATE_TRUNC('day', joined_at) AS day, COUNT(*) AS new_members
FROM member
WHERE joined_at >= :from
  AND joined_at < :to
GROUP BY 1
ORDER BY 1;
```

## 1.2 현재 활성 회원 수

```sql
SELECT COUNT(*) AS active_members
FROM member
WHERE status = 'ACTIVE';
```

## 1.3 현재 활성 회원권 수

```sql
SELECT COUNT(*) AS active_memberships
FROM membership
WHERE status = 'ACTIVE'
  AND CURRENT_DATE BETWEEN start_date AND end_date;
```

## 1.4 일별 예약 생성 수

```sql
SELECT DATE_TRUNC('day', booked_at) AS day, COUNT(*) AS bookings_created
FROM booking
WHERE booked_at >= :from
  AND booked_at < :to
GROUP BY 1
ORDER BY 1;
```

## 1.5 일별 예약 취소 수

```sql
SELECT DATE_TRUNC('day', canceled_at) AS day, COUNT(*) AS bookings_canceled
FROM booking
WHERE canceled_at IS NOT NULL
  AND canceled_at >= :from
  AND canceled_at < :to
GROUP BY 1
ORDER BY 1;
```

## 1.6 일별 no-show 수

```sql
SELECT DATE_TRUNC('day', updated_at) AS day, COUNT(*) AS no_show_count
FROM booking
WHERE status = 'NO_SHOW'
  AND updated_at >= :from
  AND updated_at < :to
GROUP BY 1
ORDER BY 1;
```

## 1.7 일별 출석 수

```sql
SELECT DATE_TRUNC('day', checked_at) AS day, COUNT(*) AS attendance_count
FROM attendance
WHERE status = 'ATTENDED'
  AND checked_at >= :from
  AND checked_at < :to
GROUP BY 1
ORDER BY 1;
```

## 1.8 결제 성공률

```sql
SELECT
    COUNT(*) AS total_payments,
    COUNT(*) FILTER (WHERE status = 'COMPLETED') AS completed_payments,
    COUNT(*) FILTER (WHERE status = 'FAILED') AS failed_payments,
    ROUND(
        COUNT(*) FILTER (WHERE status = 'COMPLETED')::numeric
        / NULLIF(COUNT(*), 0) * 100,
        2
    ) AS payment_success_rate
FROM payment
WHERE created_at >= :from
  AND created_at < :to;
```

## 1.9 일별 결제 승인 수와 금액

```sql
SELECT
    DATE_TRUNC('day', paid_at) AS day,
    COUNT(*) AS completed_payments,
    COALESCE(SUM(amount), 0) AS completed_amount
FROM payment
WHERE status = 'COMPLETED'
  AND paid_at IS NOT NULL
  AND paid_at >= :from
  AND paid_at < :to
GROUP BY 1
ORDER BY 1;
```

## 1.10 일별 환불 수와 금액

```sql
SELECT
    DATE_TRUNC('day', canceled_at) AS day,
    COUNT(*) AS refunded_payments,
    COALESCE(SUM(amount), 0) AS refunded_amount
FROM payment
WHERE status = 'CANCELED'
  AND canceled_at IS NOT NULL
  AND canceled_at >= :from
  AND canceled_at < :to
GROUP BY 1
ORDER BY 1;
```

---

# 2. 정합성 KPI SQL

## 2.1 결제 완료 후 회원권 미생성 건수

```sql
SELECT COUNT(*) AS payments_without_membership
FROM payment
WHERE status = 'COMPLETED'
  AND membership_id IS NULL;
```

## 2.2 결제 완료 후 연결된 회원권이 실제로 없는 건수

```sql
SELECT COUNT(*) AS missing_linked_memberships
FROM payment p
LEFT JOIN membership m ON m.id = p.membership_id
WHERE p.status = 'COMPLETED'
  AND p.membership_id IS NOT NULL
  AND m.id IS NULL;
```

## 2.3 환불 완료 후 회원권 미환불 건수

```sql
SELECT COUNT(*) AS refunded_payment_but_membership_not_refunded
FROM payment p
JOIN membership m ON m.id = p.membership_id
WHERE p.status = 'CANCELED'
  AND m.status <> 'REFUNDED';
```

## 2.4 환불 완료 후 미래 예약 미취소 건수

```sql
SELECT COUNT(*) AS refunded_but_future_bookings_alive
FROM payment p
JOIN booking b ON b.membership_id = p.membership_id
JOIN schedule s ON s.id = b.schedule_id
WHERE p.status = 'CANCELED'
  AND s.start_at > NOW()
  AND b.status <> 'CANCELED';
```

## 2.5 출석 완료 후 회원권 이상 상태 건수

```sql
SELECT COUNT(*) AS attendance_membership_mismatch
FROM attendance a
JOIN booking b ON b.id = a.booking_id
LEFT JOIN membership m ON m.id = b.membership_id
WHERE a.status = 'ATTENDED'
  AND b.membership_id IS NOT NULL
  AND (m.id IS NULL OR m.status = 'REFUNDED');
```

## 2.6 결제 작업 실패 건수

```sql
SELECT
    operation_type,
    error_code,
    COUNT(*) AS failed_count
FROM payment_operation
WHERE status = 'FAILED'
  AND created_at >= :from
  AND created_at < :to
GROUP BY operation_type, error_code
ORDER BY failed_count DESC, operation_type;
```

## 2.7 webhook 수신 건수

```sql
SELECT
    event_type,
    COUNT(*) AS webhook_count
FROM payment_webhook_log
WHERE created_at >= :from
  AND created_at < :to
GROUP BY event_type
ORDER BY webhook_count DESC;
```

---

# 3. 사용자 행동 KPI SQL

## 3.1 회원권 구매 후 첫 예약까지 걸린 평균 시간

```sql
SELECT
    AVG(first_booking.booked_at - p.paid_at) AS avg_time_to_first_booking
FROM payment p
JOIN LATERAL (
    SELECT MIN(b.booked_at) AS booked_at
    FROM booking b
    WHERE b.membership_id = p.membership_id
) first_booking ON TRUE
WHERE p.status = 'COMPLETED'
  AND p.paid_at IS NOT NULL
  AND p.membership_id IS NOT NULL
  AND first_booking.booked_at IS NOT NULL
  AND p.paid_at >= :from
  AND p.paid_at < :to;
```

## 3.2 시간대별 예약 집중도

```sql
SELECT
    EXTRACT(HOUR FROM s.start_at) AS hour_of_day,
    COUNT(*) AS booking_count
FROM booking b
JOIN schedule s ON s.id = b.schedule_id
WHERE b.booked_at >= :from
  AND b.booked_at < :to
GROUP BY 1
ORDER BY 1;
```

## 3.3 강사별 예약 수

```sql
SELECT
    s.instructor_id,
    COUNT(*) AS booking_count
FROM booking b
JOIN schedule s ON s.id = b.schedule_id
WHERE b.booked_at >= :from
  AND b.booked_at < :to
GROUP BY s.instructor_id
ORDER BY booking_count DESC;
```

## 3.4 강사별 출석 수

```sql
SELECT
    s.instructor_id,
    COUNT(*) AS attendance_count
FROM attendance a
JOIN schedule s ON s.id = a.schedule_id
WHERE a.status = 'ATTENDED'
  AND a.checked_at >= :from
  AND a.checked_at < :to
GROUP BY s.instructor_id
ORDER BY attendance_count DESC;
```

## 3.5 알림 유형별 읽음률

```sql
SELECT
    type,
    COUNT(*) AS total_notifications,
    COUNT(*) FILTER (WHERE read_at IS NOT NULL) AS read_notifications,
    ROUND(
        COUNT(*) FILTER (WHERE read_at IS NOT NULL)::numeric
        / NULLIF(COUNT(*), 0) * 100,
        2
    ) AS read_rate
FROM notification
WHERE created_at >= :from
  AND created_at < :to
GROUP BY type
ORDER BY total_notifications DESC;
```

---

# 4. 우선 구현 추천 SQL

1. 결제 완료 후 회원권 미생성 건수
2. 환불 완료 후 회원권 미환불 건수
3. 환불 완료 후 미래 예약 미취소 건수
4. 결제 작업 실패 건수
5. 일별 결제 승인 수
6. 일별 환불 수
7. 일별 예약 생성 수
8. 일별 출석 수
9. no-show 비율
10. 알림 유형별 읽음률
