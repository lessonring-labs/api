# Domain Architecture

LessonRing Backend의 핵심 도메인 구조와 도메인 간 관계를 정의한다.

이 문서는 다음 목적을 가진다.

- 핵심 비즈니스 도메인 식별
- 도메인 경계(Bounded Context) 정리
- 도메인 책임과 관계 정의
- 상태값 및 핵심 규칙 정리
- 향후 확장 방향 기준 제공

---

# 1. Domain Architecture Overview

LessonRing은 레슨/클래스 예약 SaaS를 위한 Backend 시스템이다.

핵심 도메인은 다음과 같다.

- Studio
- Instructor
- Member
- Membership
- Schedule
- Booking
- Attendance
- Payment
- Notification
- Auth
- Common

전체 흐름

Studio
→ Instructor
→ Member
→ Membership
→ Schedule
→ Booking
→ Attendance
→ Payment
→ Notification

설명

- Studio는 운영 단위이다.
- Instructor는 수업을 진행하는 주체이다.
- Member는 서비스를 이용하는 회원이다.
- Membership은 회원이 보유한 이용권이다.
- Schedule은 실제 운영되는 수업 일정이다.
- Booking은 회원의 예약 기록이다.
- Attendance는 실제 출석 기록이다.
- Payment는 결제 기록이다.
- Notification은 예약/결제/출석과 연계된 알림이다.

---

# 2. Core Domain Map

핵심 도메인 관계는 다음과 같다.

Studio
├─ Instructor
├─ Member
├─ Schedule
├─ Booking
├─ Membership
├─ Attendance
├─ Payment
└─ Notification

Member
├─ Membership
├─ Booking
├─ Attendance
├─ Payment
└─ Notification

Schedule
├─ Instructor
├─ Booking
└─ Attendance

Booking
├─ Member
├─ Schedule
└─ Membership

Attendance
├─ Booking
└─ Membership

Payment
└─ Membership

이 구조에서 핵심 운영 흐름은 아래와 같다.

Member
→ Membership
→ Schedule
→ Booking
→ Attendance

운영/과금 흐름은 아래와 같다.

Member
→ Payment
→ Membership

알림 흐름은 아래와 같다.

Booking / Attendance / Payment
→ Notification

---

# 3. Bounded Context

LessonRing Backend는 현재 단일 애플리케이션이지만, 개념적으로 다음 Bounded Context로 구분할 수 있다.

## 3.1 Identity Context

포함 도메인

- Auth
- Member

책임

- 로그인
- JWT 인증
- RefreshToken 관리
- 사용자 식별

---

## 3.2 Scheduling Context

포함 도메인

- Schedule
- Instructor

책임

- 수업 일정 관리
- 강사 연결
- 정원 관리
- 예약 가능 상태 제공

---

## 3.3 Booking Context

포함 도메인

- Booking
- Attendance

책임

- 예약 생성
- 예약 취소
- 출석 기록
- no-show 관리

---

## 3.4 Membership Context

포함 도메인

- Membership

책임

- 이용권 생성
- 이용권 상태 관리
- 잔여 횟수 차감
- 유효기간 검증

---

## 3.5 Payment Context

포함 도메인

- Payment

책임

- 결제 생성
- 결제 완료/취소
- 환불 처리
- Membership 생성 연결

---

## 3.6 Notification Context

포함 도메인

- Notification

책임

- 예약 알림
- 수업 알림
- 결제 알림
- 이벤트 기반 알림 확장

---

# 4. Domain Responsibilities

## 4.1 Studio

역할

- 시스템 운영의 최상위 단위
- 회원, 강사, 수업, 예약의 소속 단위

핵심 책임

- 스튜디오 식별
- 스튜디오 단위 데이터 분리
- 멀티 스튜디오 확장 기반 제공

---

## 4.2 Instructor

역할

- 수업을 진행하는 주체

핵심 책임

- 강사 정보 관리
- Schedule 연결
- 담당 수업 식별

---

## 4.3 Member

역할

- 서비스를 이용하는 회원

핵심 책임

- 회원 등록
- 회원 조회
- 회원 상태 관리
- 예약/이용권/결제의 기준 사용자 역할

현재 구현 기준

- 회원 등록 API
- 회원 단건 조회 API
- 회원 목록 조회 API

---

## 4.4 Membership

역할

- 회원이 보유한 이용권

핵심 책임

- 이용권 생성
- 유효기간 관리
- 잔여 횟수 관리
- 이용권 사용 가능 여부 검증

현재 구현 기준

- 이용권 생성 API
- 이용권 단건 조회 API
- 회원별 이용권 목록 조회 API

---

## 4.5 Schedule

역할

- 실제 운영되는 수업 일정

핵심 책임

- 수업 생성
- 수업 조회
- 정원(capacity) 관리
- 예약 가능 상태 제공

현재 구현 기준

- 수업 생성 API
- 수업 단건 조회 API
- 수업 목록 조회 API

---

## 4.6 Booking

역할

- 회원의 예약 기록

핵심 책임

- 예약 생성
- 예약 취소
- 정원 검증
- 중복 예약 검증
- 이용권 연결 및 예약 가능 여부 검증

현재 구현 기준

- 예약 생성 API
- 예약 단건 조회 API
- 예약 목록 조회 API
- 예약 취소 API

---

## 4.7 Attendance

역할

- 실제 출석 기록

핵심 책임

- 출석 기록 생성
- Booking 상태 변경
- Membership 차감 연결
- no-show 정책 적용 기반 제공

현재 상태

- 구현 진행 예정 또는 부분 설계 상태

---

## 4.8 Payment

역할

- 결제 및 과금 기록

핵심 책임

- 결제 생성
- 결제 완료 처리
- 결제 취소/환불
- Membership 생성 연결

현재 상태

- 설계 중심, 구현 예정

---

## 4.9 Notification

역할

- 예약/수업/결제 관련 알림

핵심 책임

- 예약 생성 알림
- 예약 취소 알림
- 수업 시작 알림
- 결제 완료 알림

현재 상태

- Entity 중심 기반 존재
- 알림 기능은 확장 예정

---

# 5. Aggregate 관점 정리

현재 프로젝트는 전형적인 DDD Aggregate를 엄격히 도입한 구조는 아니지만,
도메인 설계 관점에서 아래와 같이 생각할 수 있다.

## 5.1 Member Aggregate

Root

- Member

연관

- Membership
- Booking
- Attendance
- Payment
- Notification

설명

Member는 사용자 식별과 운영 기준이 되는 중심 도메인이다.

---

## 5.2 Schedule Aggregate

Root

- Schedule

연관

- Instructor
- Booking
- Attendance

설명

Schedule은 실제 수업 운영의 기준이며 정원 및 예약 가능 상태를 제공한다.

---

## 5.3 Booking Aggregate

Root

- Booking

연관

- Member
- Schedule
- Membership

설명

Booking은 실제 예약 기록이며, 이후 Attendance와 연결된다.

---

## 5.4 Membership Aggregate

Root

- Membership

연관

- Member
- Booking
- Attendance
- Payment

설명

Membership은 이용 가능 여부와 잔여 횟수 기준을 가진다.

---

# 6. Domain Relationship Detail

## 6.1 Member ↔ Membership

관계

Member 1 : N Membership

의미

- 한 회원은 여러 이용권을 가질 수 있다.
- 예약 시 Membership이 사용된다.

---

## 6.2 Schedule ↔ Instructor

관계

Instructor 1 : N Schedule

의미

- 한 강사는 여러 수업을 담당할 수 있다.

---

## 6.3 Schedule ↔ Booking

관계

Schedule 1 : N Booking

의미

- 하나의 수업에 여러 예약이 생성될 수 있다.

---

## 6.4 Member ↔ Booking

관계

Member 1 : N Booking

의미

- 회원은 여러 예약을 가질 수 있다.

---

## 6.5 Booking ↔ Membership

관계

Booking N : 1 Membership

의미

- 예약은 특정 이용권을 기반으로 생성된다.

---

## 6.6 Booking ↔ Attendance

관계

Booking 1 : 1 Attendance 또는 1 : 0..1 Attendance

의미

- 예약 후 실제 출석 기록이 생성된다.
- 아직 출석하지 않았으면 Attendance가 없을 수 있다.

---

## 6.7 Payment ↔ Membership

관계

Payment 1 : 1 Membership 또는 1 : N Membership 확장 가능

의미

- 결제 완료 후 이용권 생성과 연결된다.

---

# 7. Domain State Model

## 7.1 ScheduleStatus

현재 사용 상태값

- OPEN
- CLOSED
- CANCELED

설명

OPEN  
예약 가능 상태

CLOSED  
예약 불가 상태

CANCELED  
수업 취소 상태

---

## 7.2 BookingStatus

현재 사용 상태값

- RESERVED
- CANCELED
- ATTENDED
- NO_SHOW

설명

RESERVED  
예약 완료 상태

CANCELED  
예약 취소 상태

ATTENDED  
출석 완료 상태

NO_SHOW  
예약 후 미출석 상태

---

## 7.3 MembershipStatus

현재 사용 상태값

- ACTIVE
- EXPIRED
- SUSPENDED
- USED_UP

설명

ACTIVE  
정상 사용 가능

EXPIRED  
기간 만료

SUSPENDED  
운영 중지 또는 제한

USED_UP  
잔여 횟수 모두 사용

---

## 7.4 AttendanceStatus

현재 사용 상태값

- ATTENDED
- ABSENT
- CANCELED

설명

ATTENDED  
정상 출석

ABSENT  
결석

CANCELED  
수업 또는 처리 취소 상태

---

# 8. Domain Rules

## 8.1 Booking 생성 규칙

예약 생성 시 최소 검증

- 회원 존재 여부
- 스케줄 존재 여부
- 이용권 존재 여부
- 이용권 소유자 일치 여부
- 스케줄 정원 초과 여부
- 중복 예약 여부
- 스케줄 상태 OPEN 여부
- 시작 시간이 현재 시각 이후인지 여부

---

## 8.2 Attendance 처리 규칙

출석 처리 시 최소 규칙

- Booking이 RESERVED 상태여야 한다.
- 동일 Booking에 중복 Attendance 생성 불가
- Membership이 사용 가능 상태여야 한다.
- 출석 처리 시 Membership 잔여 횟수 차감
- 출석 처리 시 Booking 상태는 ATTENDED로 변경

---

## 8.3 Membership 사용 규칙

이용권 사용 가능 조건

- status == ACTIVE
- remainingCount > 0
- endDate가 현재 날짜보다 이전이 아니어야 함

---

## 8.4 Schedule 예약 가능 규칙

예약 가능 조건

- ScheduleStatus == OPEN
- bookedCount < capacity
- startAt > now

---

# 9. Current Implementation Status

현재 구현 완료 기준

## 완료

- Auth
- Member
- Schedule
- Booking
- Membership

## 진행 중 / 예정

- Attendance
- Payment
- Notification 고도화
- Analytics

도메인 관점으로 보면 현재는 아래까지 실제 API 흐름이 동작한다.

Member
→ Schedule
→ Booking
→ Membership

Membership은 Booking 검증과 연결되기 시작한 상태다.

---

# 10. Planned Domain Expansion

향후 확장 예정

## Booking

- 예약 대기열
- 동시성 제어
- Redis Lock
- 예약 자동 취소

## Membership

- 기간권 / 횟수권 구분 고도화
- 일시정지
- 이용권 연장
- 환불 연결

## Attendance

- no-show 정책
- 강사 체크인
- QR 출석

## Payment

- PG 연동
- 자동 결제
- 환불 정책

## Notification

- 비동기 이벤트 기반 알림
- Push / SMS / 카카오 알림톡

---

# 11. Domain Design Principles

LessonRing Backend는 다음 원칙을 따른다.

- 도메인 중심 모듈 구조를 유지한다.
- Controller에는 비즈니스 로직을 두지 않는다.
- Application Service에서 흐름을 제어한다.
- 도메인 상태값은 Enum으로 명확히 관리한다.
- 공통 예외는 BusinessException + ErrorCode 구조를 사용한다.
- API 응답은 ApiResponse 구조를 사용한다.
- 향후 Event Driven Architecture 확장을 고려한다.

---

# 12. Summary

LessonRing의 핵심 도메인은 Member, Membership, Schedule, Booking, Attendance, Payment, Notification으로 구성된다.

현재 시스템은 Member / Schedule / Booking / Membership 중심의 1차 운영 흐름이 구현되어 있으며,
Attendance, Payment, Notification은 이후 단계에서 비즈니스적으로 더 깊게 연결될 예정이다.

이 문서는 도메인의 책임, 관계, 상태값, 핵심 비즈니스 규칙을 정리하여
향후 기능 추가와 리팩토링의 기준 문서로 사용한다.