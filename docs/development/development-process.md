# Development Process

LessonRing Backend 개발 프로세스를 정의한다.

이 문서는 다음 목적을 가진다.

- Backend 개발 절차 표준화
- 기능 개발 순서 명확화
- 협업 효율 향상
- 코드 품질 유지
- 안정적인 서비스 운영

---

# 1. Development Process Overview

LessonRing Backend 개발은 다음 단계를 따른다.

1 요구사항 정의  
2 Domain 설계  
3 데이터 모델 설계  
4 API 설계  
5 Application Service 구현  
6 Controller 구현  
7 테스트 수행  
8 문서 업데이트  
9 Git Commit 및 Push

이 프로세스를 통해 개발 흐름을 일관되게 유지한다.

---

# 2. Feature Development Flow

새로운 기능 개발은 다음 순서로 진행한다.

1 기능 요구사항 정리  
2 Domain 모델 정의  
3 Entity 설계  
4 Repository 인터페이스 작성  
5 Service 구현  
6 Controller 구현  
7 DTO 작성  
8 Validation 적용  
9 테스트 수행  
10 문서 업데이트  
11 Git Commit

---

# 3. Domain First Development

LessonRing Backend는 **Domain 중심 개발 방식**을 따른다.

개발 순서

Domain  
→ Repository  
→ Service  
→ Controller

이 방식의 장점

- 비즈니스 규칙 명확화
- 테스트 용이성 증가
- 코드 구조 안정성 확보

---

# 4. Domain 설계 단계

Domain 설계 시 다음을 정의한다.

- Entity
- Domain 상태
- Domain 행위
- Domain 규칙

예시

Booking Domain

상태

RESERVED  
CANCELED  
ATTENDED

행위

create()  
cancel()  
attend()

---

# 5. Database 설계 단계

Entity 설계 시 다음을 고려한다.

- Primary Key
- Foreign Key
- 상태 컬럼
- 생성 시간
- 수정 시간

예시

Booking

id  
member_id  
schedule_id  
membership_id  
status  
booked_at  
canceled_at

---

# 6. Repository 설계 단계

Repository는 데이터 접근을 담당한다.

Repository 인터페이스는 domain 패키지에 위치한다.

예시

BookingRepository

주요 메서드

save()  
findById()  
findAll()  
findByMemberId()

구현체는 infrastructure 패키지에 위치한다.

---

# 7. Service 구현 단계

Application Service는 비즈니스 흐름을 제어한다.

Service의 주요 역할

- 도메인 조합
- 트랜잭션 관리
- 정책 적용
- 예외 처리

예시

BookingService

createBooking()  
cancelBooking()  
getBooking()

---

# 8. Controller 구현 단계

Controller는 HTTP 요청을 처리한다.

Controller 역할

- Request DTO 수신
- Service 호출
- Response DTO 반환

Controller에서는 비즈니스 로직을 작성하지 않는다.

예시

BookingController

POST /api/v1/bookings  
GET /api/v1/bookings  
PATCH /api/v1/bookings/{id}/cancel

---

# 9. DTO 작성 단계

DTO는 API 계층에서 사용된다.

Request DTO

BookingCreateRequest  
ScheduleCreateRequest

Response DTO

BookingResponse  
ScheduleResponse

DTO는 비즈니스 로직을 포함하지 않는다.

---

# 10. Validation 적용

입력값 검증은 Request DTO에서 수행한다.

사용 예시

@NotNull  
@NotBlank  
@Positive

예시

public class BookingCreateRequest {

    @NotNull
    private Long memberId;

    @NotNull
    private Long scheduleId;

}

비즈니스 검증은 Service 또는 Domain에서 수행한다.

---

# 11. 테스트 단계

Backend API 테스트 방법

1 curl 테스트  
2 Postman 테스트  
3 Swagger 테스트

예시

curl -X POST http://localhost:8080/api/v1/bookings \
-H "Authorization: Bearer ACCESS_TOKEN"

테스트 시 확인 사항

- 정상 동작 여부
- 예외 처리
- Validation 동작
- 응답 구조

---

# 12. API 응답 구조 확인

모든 API는 공통 응답 구조를 사용한다.

예시

{
"success": true,
"data": {},
"error": null
}

ApiResponse 구조를 유지한다.

---

# 13. 예외 처리 확인

예외 처리는 GlobalExceptionHandler에서 처리한다.

예시

BusinessException  
ErrorCode

테스트 시 확인 사항

- Invalid Request
- Entity Not Found
- Business Rule Violation

---

# 14. 문서 업데이트

기능 개발 후 문서를 업데이트한다.

업데이트 대상

- Domain 문서
- API 문서
- Development 문서

예시

domain/member-domain.md  
domain/booking-domain.md

---

# 15. Git Commit 단계

기능 개발 완료 후 Git Commit을 수행한다.

Commit 메시지 예시

기능(booking): 예약 생성 API 구현  
수정(auth): JWT 인증 오류 수정  
문서(api): API 가이드 문서 업데이트

한 커밋은 하나의 목적을 가진다.

---

# 16. Branch 전략

Branch 구조

main  
develop  
feature

예시

feature/member-api  
feature/booking-api

기능 개발은 feature branch에서 수행한다.

---

# 17. 코드 리뷰

코드 리뷰 시 확인 사항

- 코드 가독성
- 네이밍 규칙 준수
- 계층 분리 여부
- 예외 처리 일관성
- 테스트 수행 여부

코드 리뷰를 통해 코드 품질을 유지한다.

---

# 18. 배포 준비

배포 전 확인 사항

- API 정상 동작
- DB Migration 적용
- 로그 확인
- 환경 변수 설정

---

# 19. 운영 모니터링

운영 환경에서는 다음을 모니터링한다.

- API 응답 시간
- 에러 발생률
- 서버 자원 사용량
- DB 상태

사용 도구

Scouter  
OpenLens

---

# 20. Development Workflow Summary

LessonRing Backend 개발 흐름

요구사항 정의  
→ Domain 설계  
→ Entity 설계  
→ Repository 작성  
→ Service 구현  
→ Controller 구현  
→ 테스트  
→ 문서 업데이트  
→ Git Commit

이 프로세스를 통해 안정적인 Backend 개발을 수행한다.