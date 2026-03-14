# Transaction Strategy

## 개요

LessOnRing API는 데이터 정합성과 안정적인 서비스 운영을 위해  
명확한 트랜잭션 전략(Transaction Strategy)을 사용한다.

트랜잭션은 Application Layer(Service)에서 관리한다.

---

## 기본 원칙

- 트랜잭션은 Service 계층에서 관리한다.
- Controller에서는 트랜잭션을 사용하지 않는다.
- Domain Layer에서는 트랜잭션을 직접 사용하지 않는다.
- Infrastructure Layer는 트랜잭션을 관리하지 않는다.

---

## 기본 사용 방식

Spring의 @Transactional을 사용한다.

예시

@Transactional
public void createBooking(CreateBookingCommand command) {
bookingRepository.save(booking);
}

---

## Read / Write 트랜잭션

조회 API는 readOnly 옵션을 사용한다.

@Transactional(readOnly = true)
public Member findMember(Long memberId) {
return memberRepository.findById(memberId);
}

---

## 이벤트 처리와 트랜잭션

도메인 이벤트는 트랜잭션 커밋 이후 처리한다.

예시

Booking 생성
↓
DB Commit
↓
BookingCreatedEvent 발생
↓
Kafka Publish

---

## 목적

이 전략을 통해 다음을 보장한다.

- 데이터 정합성 유지
- 트랜잭션 범위 명확화
- 서비스 안정성 확보