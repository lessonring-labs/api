# Analytics API Specification

이 문서는 LessonRing Backend의 Analytics API 초안 명세서다.

기준:

- Base URL: `/api/v1`
- 공통 응답 규격: `ApiResponse`

주의:

- 현재 프로젝트에는 관리자 권한 모델이 명시적으로 구현되어 있지 않다.
- 따라서 본 문서는 우선 API 계약 기준을 정의하는 문서다.

---

# 1. 공통 규칙

## 1.1 Base Path

```text
/api/v1/analytics
```

## 1.2 공통 Query Parameter

- `from`: 조회 시작 시각, ISO8601
- `to`: 조회 종료 시각, ISO8601
- `studioId`: 스튜디오 기준 필터

예시:

```text
GET /api/v1/analytics/overview?from=2026-03-01T00:00:00&to=2026-04-01T00:00:00&studioId=1
```

---

# 2. Overview API

## 2.1 운영 개요 조회

```text
GET /api/v1/analytics/overview
```

응답 예시:

```json
{
  "success": true,
  "data": {
    "newMembers": 24,
    "activeMembers": 180,
    "activeMemberships": 152,
    "bookingsCreated": 96,
    "attendances": 71,
    "noShows": 8,
    "completedPayments": 32,
    "refundPayments": 3
  },
  "error": null
}
```

---

# 3. Booking Analytics API

## 3.1 예약 지표 조회

```text
GET /api/v1/analytics/bookings
```

응답 예시:

```json
{
  "success": true,
  "data": {
    "summary": {
      "bookingsCreated": 96,
      "bookingsCanceled": 18,
      "attendances": 71,
      "noShows": 8,
      "attendanceConversionRate": 73.96,
      "noShowRate": 8.33
    },
    "daily": [
      {
        "date": "2026-03-20",
        "bookingsCreated": 18,
        "bookingsCanceled": 2,
        "attendances": 14,
        "noShows": 1
      }
    ]
  },
  "error": null
}
```

---

# 4. Payment Analytics API

## 4.1 결제 지표 조회

```text
GET /api/v1/analytics/payments
```

응답 예시:

```json
{
  "success": true,
  "data": {
    "summary": {
      "totalPayments": 40,
      "completedPayments": 32,
      "failedPayments": 5,
      "refundPayments": 3,
      "paymentSuccessRate": 80.0,
      "completedAmount": 960000,
      "refundAmount": 90000
    },
    "failureReasons": [
      {
        "reason": "PG_TIMEOUT",
        "count": 2
      }
    ]
  },
  "error": null
}
```

---

# 5. Integrity Analytics API

## 5.1 정합성 이상 징후 조회

```text
GET /api/v1/analytics/integrity
```

응답 예시:

```json
{
  "success": true,
  "data": {
    "paymentsWithoutMembership": 0,
    "missingLinkedMemberships": 0,
    "refundedPaymentButMembershipNotRefunded": 0,
    "refundedButFutureBookingsAlive": 1,
    "attendanceMembershipMismatch": 0,
    "paymentOperationFailedCount": 2,
    "webhookFailureCount": 1,
    "lockAcquisitionFailedCount": 0
  },
  "error": null
}
```

---

# 6. Notification Analytics API

## 6.1 알림 지표 조회

```text
GET /api/v1/analytics/notifications
```

응답 예시:

```json
{
  "success": true,
  "data": {
    "summary": {
      "totalNotifications": 210,
      "readNotifications": 124,
      "overallReadRate": 59.05
    },
    "byType": [
      {
        "type": "BOOKING_CREATED",
        "totalNotifications": 80,
        "readNotifications": 48,
        "readRate": 60.0
      }
    ]
  },
  "error": null
}
```

---

# 7. Timeseries API

## 7.1 시계열 KPI 조회

```text
GET /api/v1/analytics/timeseries
```

Query Parameter:

- `metric`: `bookings|attendances|payments|refunds|members`
- `interval`: `day|week|month`
- `from`
- `to`
- `studioId`

---

# 8. Breakdown API

## 8.1 강사별/시간대별 분포 조회

```text
GET /api/v1/analytics/breakdown
```

Query Parameter:

- `target`: `instructor|hour|notificationType|paymentFailureReason`
- `metric`: `bookings|attendances|readRate|failedPayments`
- `from`
- `to`
- `studioId`

---

# 9. 구현 우선순위

1. `GET /api/v1/analytics/overview`
2. `GET /api/v1/analytics/integrity`
3. `GET /api/v1/analytics/payments`
4. `GET /api/v1/analytics/bookings`
5. `GET /api/v1/analytics/notifications`
6. `GET /api/v1/analytics/timeseries`
7. `GET /api/v1/analytics/breakdown`
