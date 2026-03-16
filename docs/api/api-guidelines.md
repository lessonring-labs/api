# API Guidelines

LessonRing Backend API 설계 및 구현 시 따라야 하는 규칙을 정의한다.

이 문서는 다음 목적을 가진다.

- API 설계 일관성 유지
- 클라이언트 연동 안정성 확보
- 유지보수성 향상
- 확장 가능한 API 구조 설계

---

# 1. API 기본 규칙

## Base URL

모든 API는 버전 prefix를 사용한다.

예시

/api/v1

예시 API

/api/v1/members  
/api/v1/schedules  
/api/v1/bookings  
/api/v1/memberships

---

# 2. HTTP Method 규칙

GET  
조회

POST  
생성

PUT  
전체 수정

PATCH  
부분 수정

DELETE  
삭제

예시

GET /api/v1/members  
POST /api/v1/members  
GET /api/v1/members/{memberId}  
PATCH /api/v1/members/{memberId}  
DELETE /api/v1/members/{memberId}

---

# 3. Resource Naming 규칙

모든 Resource는 **복수형**을 사용한다.

/members  
/schedules  
/bookings  
/memberships

---

# 4. Path Parameter 규칙

Resource 식별자는 Path Parameter로 전달한다.

예시

/api/v1/members/{memberId}  
/api/v1/schedules/{scheduleId}  
/api/v1/bookings/{bookingId}

예시

GET /api/v1/members/1  
GET /api/v1/schedules/10

---

# 5. Query Parameter 규칙

목록 조회 및 검색 시 Query Parameter를 사용한다.

예시

GET /api/v1/schedules?date=2026-03-20  
GET /api/v1/bookings?memberId=1  
GET /api/v1/members?page=0&size=20

---

# 6. API 응답 구조

모든 API는 공통 응답 구조를 사용한다.

{
"success": true,
"data": {},
"error": null
}

---

# 7. 성공 응답 예시

{
"success": true,
"data": {
"id": 1,
"studioId": 1,
"title": "Pilates Group",
"capacity": 10
},
"error": null
}

---

# 8. 에러 응답 구조

{
"success": false,
"data": null,
"error": {
"code": "BOOKING_FULL",
"message": "예약 가능한 정원이 없습니다"
}
}

---

# 9. ErrorCode 규칙

모든 비즈니스 에러는 ErrorCode Enum을 사용한다.

예시

MEMBER_NOT_FOUND  
SCHEDULE_NOT_FOUND  
BOOKING_FULL  
MEMBERSHIP_EXPIRED

예시 코드

throw new BusinessException(ErrorCode.BOOKING_FULL);

---

# 10. 인증 방식

LessonRing API는 JWT 인증을 사용한다.

요청 헤더

Authorization: Bearer {accessToken}

예시

Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...

---

# 11. 인증이 필요한 API

다음 API는 인증이 필요하다.

members  
schedules  
bookings  
memberships  
attendance  
payment

로그인 API만 인증이 필요 없다.

/api/v1/auth/login

---

# 12. Pagination 규칙

목록 조회 시 Pagination을 지원한다.

Query Parameter

page  
size  
sort

예시

GET /api/v1/members?page=0&size=20  
GET /api/v1/schedules?page=0&size=10

응답 예시

{
"success": true,
"data": {
"content": [],
"page": 0,
"size": 20,
"totalElements": 100
},
"error": null
}

---

# 13. 예약 API 설계 규칙

예약 시스템 특성상 다음 규칙을 따른다.

예약 생성

POST /api/v1/bookings

예약 취소

PATCH /api/v1/bookings/{bookingId}/cancel

예시

PATCH /api/v1/bookings/1/cancel

---

# 14. 상태값 규칙

예약 상태

RESERVED  
CANCELED  
ATTENDED  
NO_SHOW

스케줄 상태

OPEN  
CLOSED  
CANCELED

이용권 상태

ACTIVE  
EXPIRED  
USED

---

# 15. 날짜 및 시간 규칙

모든 날짜는 ISO8601 형식을 사용한다.

예시

2026-03-20T19:00:00

Timezone

UTC 기반 또는 서버 timezone 사용

---

# 16. API 버전 전략

API는 version prefix를 사용한다.

현재 버전

/api/v1

향후 버전

/api/v2

기존 API는 최대한 유지하고 새 버전에서 변경한다.

---

# 17. 로그 전략

다음 상황에서 로그를 남긴다.

예약 생성  
예약 취소  
결제 완료  
로그인 실패

로그 레벨

INFO  
WARN  
ERROR

---

# 18. 향후 확장 고려

다음 기능을 고려하여 API 설계를 유지한다.

대기열 예약  
자동 예약 취소  
결제 연동  
알림 시스템  
멀티 스튜디오 지원