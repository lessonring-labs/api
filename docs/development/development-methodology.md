# LessOnRing 개발 방법론

## 개요

LessOnRing은 Agile과 DevOps를 기반으로 한 개발 방법론을 채택하고 있으며,
이벤트 기반 아키텍처(Event Driven Architecture)와
모듈형 모놀리식 구조(Modular Monolith)를 함께 적용하여
빠른 개발과 안정적인 확장을 동시에 고려한 구조로 설계되었습니다.

본 프로젝트는 단순 CRUD 중심이 아닌,
도메인 중심 설계 + 상태 기반 비즈니스 로직 + 이벤트 흐름을 핵심으로 합니다.

---

## 1. Agile (애자일)

LessOnRing은 짧은 개발 주기와 빠른 피드백을 중심으로 하는 Agile 방식으로 개발됩니다.

### 핵심 원칙

- 기능 단위(Feature 단위) 개발
- 2주 단위 스프린트 운영
- 요구사항 → 설계 → 개발 → 테스트 → 배포까지 반복
- 빠른 피드백 반영 및 지속적인 개선

### 적용 방식

기획 → API/도메인 설계 → 개발 → 테스트 → 배포 → 피드백 → 개선

초기 완성도가 아닌 지속적인 진화(Iteration)를 목표로 합니다.

---

## 2. DevOps

개발, 배포, 운영을 하나의 흐름으로 통합하는 DevOps 방식을 적용합니다.

### 핵심 구성

- GitHub 기반 소스 관리
- GitHub Actions 기반 CI/CD 자동화
- Docker 기반 컨테이너 빌드
- Kubernetes(k3s) 기반 운영 환경

### 배포 흐름

코드 커밋 → CI 실행 → 테스트 → Docker 이미지 생성 → 배포 → 모니터링

### 목표

- 배포 자동화
- 장애 대응 속도 향상
- 운영 효율성 극대화

---

## 3. Event Driven Architecture

LessOnRing은 이벤트 기반 구조를 사용하여 도메인 간 결합도를 낮추고 확장성을 확보합니다.

### 구조 개념

도메인 이벤트 → 이벤트 발행 → 이벤트 핸들러 → 후속 처리

### 예시

결제 완료 → PaymentCompletedEvent 발생  
→ Notification 생성  
→ 통계 반영

### 적용 이유

- 도메인 간 의존성 최소화
- 비동기 처리 가능
- 기능 확장 시 기존 코드 영향 최소화

### 향후 확장

현재: Spring Event  
확장: Kafka 기반 이벤트 스트리밍

---

## 4. Modular Monolith

초기에는 Modular Monolith 구조를 채택하여 개발합니다.

### 구조 특징

- 하나의 애플리케이션으로 구성
- 내부는 도메인 단위로 모듈 분리
- 계층 구조 명확히 유지

payment  
membership  
booking  
schedule  
member  
notification

각 모듈은 다음 구조를 따릅니다.

api  
application  
domain  
infrastructure

### 장점

- 빠른 개발 속도
- 낮은 운영 복잡도
- 테스트 용이성

### 확장 전략

Modular Monolith → 필요 시 MSA로 점진적 전환

---

## 5. 도메인 설계 전략

### 상태 기반 설계

모든 핵심 도메인은 상태(State)를 기준으로 동작합니다.

Payment  
READY → COMPLETED → CANCELED  
↘ FAILED

Booking  
RESERVED → ATTENDED  
↘ CANCELED  
↘ NO_SHOW

상태 전이가 비즈니스 로직의 핵심입니다.

---

### Entity 중심 비즈니스 로직

비즈니스 로직은 Service가 아닌 Entity 내부에서 처리합니다.

payment.complete(...)  
payment.fail(...)  
payment.syncCompletedFromWebhook(...)

Service는 흐름 제어만 담당합니다.

---

### 결제(Payment) 설계

Payment는 단순 결제가 아닌 다음을 포함합니다.

- 결제 상태 관리
- PG 연동 정보
- 이용권 스냅샷
- 환불 기준 데이터

---

### 환불 정책

COUNT권  
환불 금액 = (결제금액 / 총 횟수) × 남은 횟수

PERIOD권  
환불 금액 = (결제금액 × 남은 일수) / 전체 일수

---

### 정합성 보장

환불 시 다음이 동시에 처리됩니다.

Payment → CANCELED  
Membership → REFUNDED  
Booking → 미래 예약 자동 취소

---

## 6. 테스트 전략

### 테스트 계층

Unit Test (Mockito)  
Integration Test (SpringBootTest)  
Controller Test (WebMvcTest)

### 원칙

- Service → 단위 테스트
- 핵심 비즈니스 → 통합 테스트
- API → Controller 테스트

### 금지 사항

외부 API 직접 호출 금지  
PG 실제 호출 금지  
과도한 DB 의존 테스트 금지

---

## 7. 예외 처리 전략

### GlobalExceptionHandler 사용

모든 에러는 표준화된 형태로 반환합니다.

{
"success": false,
"error": {
"code": "C002",
"message": "잘못된 요청입니다."
}
}

### ErrorCode 기반 설계

C002 → 타입 오류  
C003 → 필수값 누락  
C004 → validation 오류

---

## 8. 트랜잭션 및 동시성 전략

### 트랜잭션

Service 레이어에서 @Transactional 관리

### 동시성 제어

Redis Distributed Lock 적용

적용 대상

- 예약(Booking)
- 스케줄 정원(capacity)

---

## 9. 확장 전략

### 향후 고려 사항

- 멀티 PG 지원
- Kafka 이벤트 확장
- MSA 전환
- idempotency 처리 (중복 요청 방지)

---

## 최종 개발 방법론

Agile + DevOps + Event Driven Architecture + Modular Monolith

이를 통해 다음을 실현합니다.

- 빠른 개발 속도
- 자동화된 배포
- 안정적인 운영
- 유연한 확장 구조

---

## 결론

LessOnRing은

도메인 중심 설계 + 상태 기반 로직 + 이벤트 흐름 + 자동화된 운영

을 기반으로 하는 현업 수준의 확장형 플랫폼 아키텍처를 목표로 합니다.