# 프로젝트 로드맵

이 문서는 2026-03-24 기준 LessonRing Backend 프로젝트의 현재 상태와 다음 우선순위를 정리한 로드맵이다.

목표:

- 이미 정리된 기반 작업을 명확히 기록한다.
- 다음 개발 우선순위를 현실적으로 제시한다.
- 기능, 품질, 운영, 확장 과제를 분리해서 관리한다.

---

# 현재 상태 요약

현재 프로젝트는 다음 기반 작업이 이미 정리된 상태다.

- 핵심 도메인 구현
  - `member`, `membership`, `schedule`, `booking`, `attendance`, `payment`, `notification`, `studio`, `instructor`
- 결제 흐름
  - PG 승인/취소
  - webhook 로그
  - `payment_operation` 테이블 및 멱등성 구조
- DB 구조
  - Flyway migration `V1~V13` 정리
  - baseline `V1__init_schema.sql` 추가
  - ERD / DDL / DBML 문서 정리
- 실행 환경
  - `application.yml`, `application-local.yml`, `application-dev.yml` 분리
  - IntelliJ `.run` 실행 구성 추가
- 문서
  - 도메인 문서 정리
  - Git 가이드 정리
  - 기술 스택 문서 정리
  - 엔티티 리팩토링 가이드 추가
  - 다국어 설계안 추가
- 보안
  - 의존성 취약점 일부 정리
  - Querydsl 경고에 대한 판단 근거 문서화

즉, 현재는 "핵심 도메인과 기본 운영 기반은 갖춰졌고, 다음 단계는 품질/운영/확장 정리"로 보는 것이 맞다.

---

# 로드맵 원칙

현재 프로젝트의 우선순위는 다음 기준으로 잡는다.

1. 실제 운영 안정성
2. 결제/예약/이용권 핵심 흐름의 신뢰성
3. 실행/배포/관측 기반 정리
4. 확장 기능 도입

즉, 새 기능을 계속 추가하기보다:

- 현재 핵심 흐름을 더 안정하게 만들고
- 운영 가능한 상태를 먼저 만들고
- 그 다음 확장 기능을 넣는 순서가 적절하다.

---

# Phase 1. 운영 안정화

기간 감각:

- 가장 먼저 진행해야 하는 단기 과제

목표:

- 현재 구현된 핵심 흐름을 운영 가능한 수준으로 안정화

주요 과제:

- `local`, `dev` 기준 실제 부팅/연결 검증
- DB migration 실제 적용 검증
- Redis 연결 및 분산 락 동작 점검
- 결제 승인/취소/webhook 시나리오 점검
- 예외 응답 형식 일관성 검증
- Swagger 문서와 실제 API 응답 일치 여부 검증

완료 기준:

- `local`, `dev` 프로필에서 기본 부팅 가능
- migration으로 초기 DB 구성 가능
- 핵심 API가 기본 happy path 기준 동작
- 결제 흐름에서 중복 처리/멱등 처리 기본 검증 완료

---

# Phase 2. 테스트 강화

기간 감각:

- 운영 안정화 다음으로 바로 이어질 과제

목표:

- 결제/예약/출석/이용권 흐름 회귀 방지

우선 테스트 대상:

- `PaymentService`
- `PaymentPgService`
- `PaymentWebhookService`
- `BookingService`
- `AttendanceService`
- `MembershipService`

권장 테스트 범위:

- 상태 전이 테스트
- 멱등 처리 테스트
- 환불 가능/불가 조건 테스트
- 중복 예약 방지 테스트
- 이용권 차감/복구 테스트
- webhook 중복 수신 테스트

완료 기준:

- 핵심 서비스 단위 테스트 확보
- 주요 결제/예약 시나리오 회귀 테스트 가능
- 취약한 도메인 상태 전이 케이스를 자동 검증 가능

---

# Phase 3. 도메인 모델 정리

목표:

- 엔티티와 서비스 책임을 더 명확히 분리
- 복잡한 메서드와 상태 전이 로직을 정리

우선 대상:

1. `Payment`
2. `Booking`
3. `Membership`
4. `Attendance`

주요 과제:

- 긴 엔티티 메서드 분리
- 상태 전이 규칙의 enum 이동 검토
- 값 객체 도입 후보 정리
- 서비스와 엔티티 책임 경계 정리

관련 문서:

- [`payment-entity-refactoring-plan.md`](/C:/wms/api/docs/development/payment-entity-refactoring-plan.md#L1)
- [`entity-refactoring-guidelines.md`](/C:/wms/api/docs/development/entity-refactoring-guidelines.md#L1)

완료 기준:

- 핵심 엔티티 메서드 복잡도 감소
- 엔티티/서비스 책임 경계 명확화
- 신규 기능 추가 시 구조 일관성 유지 가능

---

# Phase 4. 운영 기능 확장

목표:

- 실서비스 운영에 필요한 기능 보강

후보 과제:

- 관리자용 조회 API 보강
- 통계/분석 조회 기능 보강
- 알림 기능 고도화
- 사용자별 운영 편의 기능 추가

현재 코드 기준 유력 항목:

- `analytics` 영역 실질 구현
- `notification` 기능 확장
- 결제 이력/환불 이력 조회 강화

완료 기준:

- 운영자가 핵심 데이터를 조회/추적 가능
- 결제/예약/이용권 상태를 화면에서 쉽게 확인 가능

---

# Phase 5. 다국어 / 문구 체계 정리

목표:

- 시스템 문구와 enum 표시값의 다국어 기반 마련

주요 과제:

- `MessageSource` 도입
- `ErrorCode` 메시지 다국어화
- validation 메시지 다국어화
- enum 표시 문구 분리
- locale 결정 방식 정리

관련 문서:

- [`internationalization-design.md`](/C:/wms/api/docs/development/internationalization-design.md#L1)

완료 기준:

- 기본 시스템 문구 한국어/영어 처리 가능
- 다국어 확장 시 구조 변경 비용 최소화

---

# Phase 6. 외부 연동 및 확장 기술 도입

목표:

- 현재 플레이스홀더 상태의 기술을 실제 사용 수준으로 전환

후보 항목:

- Kafka
- Feign
- Kakao OAuth Login
- MapStruct

판단 기준:

- 실제 기능 요구가 생긴 뒤 도입
- 먼저 도입하고 나중에 쓰는 방식은 피함

우선순위 제안:

1. Kakao OAuth Login
2. Feign
3. Kafka
4. MapStruct

이유:

- OAuth/외부 API 연동은 서비스 기능과 직접 연결됨
- Kafka는 운영/이벤트 구조까지 같이 바뀌므로 가장 나중이 안전함

---

# Phase 7. 배포 / 관측 체계

목표:

- 개발 서버를 넘어 실제 운영 체계에 가까운 기반 마련

후보 항목:

- Docker 이미지 정리
- CI/CD 파이프라인
- 환경변수/시크릿 관리
- 로그/모니터링 체계
- Kubernetes/k3s 운영 여부 검토

도입 순서 제안:

1. Docker
2. GitHub Actions
3. 로그/모니터링
4. 인프라 오케스트레이션

완료 기준:

- 수동 실행이 아닌 표준 배포 절차 확보
- 장애 추적 가능한 로그/모니터링 체계 확보

---

# 추천 우선순위 한눈에 보기

가장 먼저 할 일:

1. `local`, `dev` 실제 실행 검증
2. 결제/예약/이용권 핵심 흐름 테스트 강화
3. 엔티티/서비스 책임 정리

그 다음 할 일:

4. 운영 조회 기능 보강
5. 다국어 기반 마련
6. 외부 연동 확장

마지막에 할 일:

7. Kafka 등 구조 변화가 큰 기술 도입
8. 인프라/배포 고도화

---

# 현재 프로젝트 기준 현실적인 다음 액션

지금 시점에서 가장 현실적인 다음 작업 5개는 아래다.

1. `local`, `dev` 프로필로 실제 bootRun 검증
2. 결제 서비스 테스트 작성
3. webhook 중복/멱등 시나리오 테스트 작성
4. `Payment`, `Membership` 엔티티 메서드 구조 정리 설계 구체화
5. `ErrorCode`와 validation 메시지의 다국어 적용 범위 정의

---

# 결론

현재 프로젝트는 "기능이 없는 초기 상태"는 이미 지났고, "핵심 도메인은 있으나 품질과 운영 기반을 다듬어야 하는 단계"에 있다.

따라서 로드맵의 핵심은 새 기능을 무작정 추가하는 것이 아니라:

- 운영 안정화
- 테스트 강화
- 도메인 구조 정리
- 이후 확장 기능 도입

순서로 진행하는 것이다.
