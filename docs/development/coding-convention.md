# Coding Convention

## 네이밍 규칙

### Controller

MemberController  
BookingController  
SessionController

### Service

MemberService  
BookingService  
SessionService

### Repository

MemberRepository  
BookingRepository

### DTO

Request

CreateMemberRequest  
CreateBookingRequest

Response

MemberResponse  
BookingResponse

---

## 기본 코딩 규칙

### Controller

- 비즈니스 로직을 작성하지 않는다.
- 요청/응답 처리만 담당한다.

### Application Layer

- 비즈니스 로직을 처리한다.
- 트랜잭션을 관리한다.

### Domain Layer

- 도메인 모델과 핵심 로직을 포함한다.
- 비즈니스 규칙을 구현한다.

### Infrastructure Layer

- 외부 시스템 연동을 담당한다.
- DB / Redis / Kafka 등의 접근을 처리한다.

---

## DTO 변환 규칙

DTO 변환은 Mapper를 사용한다.

예

MapStruct

Controller → DTO → Application → Domain