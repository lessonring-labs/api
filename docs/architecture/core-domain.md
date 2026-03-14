# LessOnRing Backend Core Domain Entities

## Overview
LessOnRing Backend의 핵심 도메인 엔티티는 아래 8개로 구성한다.

- Studio
- Instructor
- Member
- Membership
- Schedule
- Booking
- Attendance
- Payment

모든 엔티티는 공통으로 BaseEntity를 상속받는다.

------------------------------------------------------------

# BaseEntity

공통 필드

BaseEntity
- createdAt : LocalDateTime
- createdBy : Long
- updatedAt : LocalDateTime
- updatedBy : Long

설명
createdAt  : 생성 일시
createdBy  : 생성 사용자 ID
updatedAt  : 수정 일시
updatedBy  : 수정 사용자 ID

운영 SaaS 특성상 누가 생성/수정했는지 추적하기 위해 createdBy / updatedBy를 포함한다.

------------------------------------------------------------

# 1. Studio

역할
스튜디오(센터) 정보와 운영 기준이 되는 최상위 엔티티

필드

Studio
- id : Long
- name : String
- phone : String
- address : String
- detailAddress : String
- timezone : String
- businessNumber : String
- status : StudioStatus

상태값

StudioStatus
- ACTIVE
- INACTIVE
- CLOSED

설명
name           : 스튜디오명
phone          : 대표 연락처
address        : 기본 주소
detailAddress  : 상세 주소
timezone       : 운영 시간대
businessNumber : 사업자등록번호
status         : 운영 상태

------------------------------------------------------------

# 2. Instructor

역할
스튜디오에 소속되어 수업을 담당하는 강사 엔티티

필드

Instructor
- id : Long
- studioId : Long
- name : String
- phone : String
- email : String
- profileImageUrl : String
- status : InstructorStatus
- memo : String

상태값

InstructorStatus
- ACTIVE
- INACTIVE
- LEAVE

설명
studioId        : 소속 스튜디오 ID
name            : 강사명
phone           : 연락처
email           : 이메일
profileImageUrl : 프로필 이미지
status          : 강사 상태
memo            : 운영 메모

------------------------------------------------------------

# 3. Member

역할
스튜디오에 등록되어 예약, 출석, 결제, 이용권 사용을 수행하는 회원 엔티티

필드

Member
- id : Long
- studioId : Long
- name : String
- phone : String
- email : String
- gender : Gender
- birthDate : LocalDate
- status : MemberStatus
- joinedAt : LocalDateTime
- memo : String

상태값

Gender
- MALE
- FEMALE
- NONE

MemberStatus
- ACTIVE
- INACTIVE
- BLOCKED

설명
studioId  : 소속 스튜디오 ID
name      : 회원명
phone     : 연락처
email     : 이메일
gender    : 성별
birthDate : 생년월일
status    : 회원 상태
joinedAt  : 등록일시
memo      : 운영 메모

------------------------------------------------------------

# 4. Membership

역할
회원이 실제로 사용할 수 있는 횟수권/기간권 엔티티

필드

Membership
- id : Long
- studioId : Long
- memberId : Long
- name : String
- type : MembershipType
- totalCount : Integer
- remainingCount : Integer
- startDate : LocalDate
- endDate : LocalDate
- status : MembershipStatus

상태값

MembershipType
- COUNT
- PERIOD

MembershipStatus
- ACTIVE
- EXPIRED
- USED_UP
- CANCELED

설명
studioId       : 스튜디오 ID
memberId       : 회원 ID
name           : 이용권명
type           : 이용권 유형
totalCount     : 총 횟수
remainingCount : 잔여 횟수
startDate      : 시작일
endDate        : 종료일
status         : 이용권 상태

------------------------------------------------------------

# 5. Schedule

역할
특정 날짜와 시간에 실제로 운영되는 수업 일정 엔티티

필드

Schedule
- id : Long
- studioId : Long
- instructorId : Long
- title : String
- type : ScheduleType
- startAt : LocalDateTime
- endAt : LocalDateTime
- capacity : Integer
- bookedCount : Integer
- status : ScheduleStatus

상태값

ScheduleType
- PERSONAL
- GROUP

ScheduleStatus
- OPEN
- CLOSED
- CANCELED
- COMPLETED

설명
studioId     : 스튜디오 ID
instructorId : 강사 ID
title        : 수업명
type         : 수업 유형
startAt      : 시작 시간
endAt        : 종료 시간
capacity     : 정원
bookedCount  : 예약 인원
status       : 일정 상태

------------------------------------------------------------

# 6. Booking

역할
회원이 특정 일정에 대해 생성한 예약 엔티티

필드

Booking
- id : Long
- studioId : Long
- memberId : Long
- scheduleId : Long
- membershipId : Long
- status : BookingStatus
- bookedAt : LocalDateTime
- canceledAt : LocalDateTime
- cancelReason : String

상태값

BookingStatus
- BOOKED
- CANCELED

설명
studioId     : 스튜디오 ID
memberId     : 회원 ID
scheduleId   : 일정 ID
membershipId : 이용권 ID
status       : 예약 상태
bookedAt     : 예약 시각
canceledAt   : 취소 시각
cancelReason : 취소 사유

------------------------------------------------------------

# 7. Attendance

역할
예약 이후 실제 출석 결과를 기록하는 엔티티

필드

Attendance
- id : Long
- bookingId : Long
- memberId : Long
- scheduleId : Long
- status : AttendanceStatus
- checkedAt : LocalDateTime
- note : String

상태값

AttendanceStatus
- ATTENDED
- ABSENT
- NO_SHOW

설명
bookingId : 예약 ID
memberId  : 회원 ID
scheduleId: 일정 ID
status    : 출석 상태
checkedAt : 출석 처리 시간
note      : 메모

------------------------------------------------------------

# 8. Payment

역할
회원의 결제 및 환불 이력을 관리하는 엔티티

필드

Payment
- id : Long
- studioId : Long
- memberId : Long
- membershipId : Long
- amount : Long
- method : PaymentMethod
- status : PaymentStatus
- paidAt : LocalDateTime
- canceledAt : LocalDateTime

상태값

PaymentMethod
- CASH
- CARD
- TRANSFER
- OTHER

PaymentStatus
- PAID
- CANCELED
- REFUNDED

설명
studioId     : 스튜디오 ID
memberId     : 회원 ID
membershipId : 이용권 ID
amount       : 결제 금액
method       : 결제 수단
status       : 결제 상태
paidAt       : 결제 시각
canceledAt   : 취소 시각

------------------------------------------------------------

# Domain Relationship

Studio
├─ Instructor
├─ Member
├─ Membership
├─ Schedule
└─ Payment

Member
├─ Membership
├─ Booking
├─ Attendance
└─ Payment

Instructor
└─ Schedule

Schedule
├─ Booking
└─ Attendance

Booking
├─ Member
├─ Schedule
└─ Membership

Attendance
└─ Booking

Payment
└─ Membership

------------------------------------------------------------

# 핵심 운영 흐름

Member
→ Membership 구매
→ Schedule 예약 (Booking)
→ 수업 참여 (Attendance)
→ 결제 기록 (Payment)