# 보안 메모

## Querydsl CVE-2024-49203

### 요약

- 영향 의존성: `com.querydsl:querydsl-jpa:5.1.0`
- 보안 이슈: `CVE-2024-49203`
- NVD 점수: `9.8`
- 상태: `Disputed`

이 이슈는 신뢰할 수 없는 외부 입력으로 `orderBy` 절을 구성할 때 발생할 수 있는 HQL Injection 위험과 관련이 있습니다.

### 현재 프로젝트 판단

2026년 3월 24일 기준, 이 프로젝트에서 Querydsl을 사용하는 파일은 다음과 같습니다.

- [`src/main/java/com/lessonring/api/booking/infrastructure/query/BookingQueryRepository.java`](src/main/java/com/lessonring/api/booking/infrastructure/query/BookingQueryRepository.java)
- [`src/main/java/com/lessonring/api/studio/infrastructure/query/StudioQueryRepository.java`](src/main/java/com/lessonring/api/studio/infrastructure/query/StudioQueryRepository.java)
- [`src/main/java/com/lessonring/api/analytics/infrastructure/query/AnalyticsQueryRepository.java`](src/main/java/com/lessonring/api/analytics/infrastructure/query/AnalyticsQueryRepository.java)
- [`src/main/java/com/lessonring/api/instructor/infrastructure/query/InstructorQueryRepository.java`](src/main/java/com/lessonring/api/instructor/infrastructure/query/InstructorQueryRepository.java)

현재 코드 검토 결과는 다음과 같습니다.

- 요청 파라미터나 기타 외부 입력을 직접 받는 `orderBy(...)` 사용이 없음
- `PathBuilder`, 동적 경로 생성, 문자열 기반 정렬 식 조합이 없음
- `Pageable` 또는 `Sort` 요청 값을 Querydsl 정렬로 그대로 전달하는 흐름이 없음

현재 실제 Querydsl 조회 구현이 들어 있는 파일은 다음입니다.

- [`src/main/java/com/lessonring/api/booking/infrastructure/query/BookingQueryRepository.java`](src/main/java/com/lessonring/api/booking/infrastructure/query/BookingQueryRepository.java)

해당 쿼리는 고정된 조건과 조인만 사용합니다.

### 위험 판단

- 이 경고가 스캐너에 계속 보이는 이유는 upstream `com.querydsl` 좌표에서 `5.1.0`에 대한 공식 패치 릴리스를 제공하지 않기 때문입니다.
- 현재 코드베이스 기준으로는 알려진 취약 사용 패턴이 노출되어 있지 않습니다.
- 따라서 현재 구현 기준 위험도는 낮다고 판단하지만, 의존성 경고 자체는 향후 변경을 위해 계속 추적하는 것이 맞습니다.

### 개발 시 주의사항

새 Querydsl 조회를 추가할 때는 다음 원칙을 지켜야 합니다.

- 원시 요청 파라미터로 `orderBy` 절을 직접 만들지 않을 것
- 외부 필드명을 Querydsl 경로에 직접 매핑하지 않을 것
- 동적 정렬이 필요하면 허용 목록 기반의 고정 정렬 필드만 사용할 것
- Querydsl 쿼리 생성이 더 동적으로 바뀌면 이 보안 이슈를 다시 검토할 것

### 향후 선택지

1. 현재 의존성을 유지하고, 현재 코드 사용 방식 기준으로 수용 가능한 위험임을 문서화한다.
2. 정책상 경고 제거가 필요하면 `io.github.openfeign.querydsl` 같은 유지보수 포크로 이전한다.

### 현재 결론

현재 프로젝트 상태에서는 OpenFeign Querydsl 포크로의 이전이 필수는 아닙니다.

그 이유는 다음과 같습니다.

- 현재 코드에서 외부 입력 기반 `orderBy` 사용 패턴이 확인되지 않음
- 남아 있는 경고는 라이브러리 advisory 자체이며, 현재 구현이 바로 취약하다는 의미는 아님
- 포크 이전은 단순 버전 변경이 아니라 좌표 변경, 빌드 설정 점검, 생성 코드 호환성 확인까지 필요한 별도 작업임

따라서 현재 단계에서는 기존 `com.querydsl` 의존성을 유지하고, Querydsl 사용 방식이 더 동적으로 바뀌는 시점에 재검토하는 것을 기본 방침으로 합니다.
