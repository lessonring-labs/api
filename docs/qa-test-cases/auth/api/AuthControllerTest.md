# QA - 인증 컨트롤러 테스트 케이스

## 1. 문서 기본 정보

| 항목 | 내용 |
|-----|-----|
| 문서명 | 인증 컨트롤러 테스트 케이스 |
| 기준 테스트 파일 | [AuthControllerTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/auth/api/AuthControllerTest.java) |
| 모듈 | 인증 |
| 테스트 유형 | API |
| 우선순위 | P1 |
| 작성 기준 | 자동화 테스트 기준 수동 검수용 전환 |

## 2. 테스트 목적

인증 API가 잘못된 입력을 정상적으로 차단하고 표준 에러 응답을 반환하는지 검증한다.

## 3. 테스트 환경

| 항목 | 값 |
|-----|-----|
| 실행 환경 | Local / Dev |
| 인증 상태 | 로그아웃 또는 Mock 인증 가능 상태 |
| 필요 도구 | Postman 또는 MockMvc 결과 확인 도구 |

## 4. 선행 조건

- 애플리케이션이 정상 기동되어 있어야 한다.
- 공통 예외 처리기가 활성화되어 있어야 한다.

## 5. 테스트 케이스 상세

### AUTH-QA-001 로그인 요청 시 userId 누락 검증

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | 로그인 API 접근 가능 |
| 입력값 | `{}` |
| 수행절차 | `POST /api/v1/auth/login` 호출 |
| 예상결과 | `400 Bad Request`, `success=false`, validation 에러 코드 반환 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### AUTH-QA-002 토큰 재발급 요청 시 refreshToken 누락 검증

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | 재발급 API 접근 가능 |
| 입력값 | `{}` |
| 수행절차 | `POST /api/v1/auth/refresh` 호출 |
| 예상결과 | `400 Bad Request`, `success=false`, validation 에러 코드 반환 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### AUTH-QA-003 로그아웃 요청 시 path variable 타입 오류 검증

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | 로그아웃 API 접근 가능 |
| 입력값 | 경로 변수 `abc` |
| 수행절차 | `POST /api/v1/auth/logout/abc` 호출 |
| 예상결과 | `400 Bad Request`, `success=false`, 타입 변환 에러 코드 반환 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

## 6. 수행 요약

| 항목 | 내용 |
|-----|-----|
| 수행자 |  |
| 수행일 |  |
| 수행 버전 |  |
| 결과 요약 |  |
| 비고 |  |

## 현재 테스트 메서드 기준

| 메서드 | DisplayName | QA 케이스 |
|-----|-----|-----|
| `login_validation_fail_userId_null` | 로그인 요청 시 userId가 없으면 validation 에러가 발생한다 | `AUTH-QA-001` |
| `refresh_validation_fail_refreshToken_blank` | 토큰 재발급 요청 시 refreshToken이 없으면 validation 에러가 발생한다 | `AUTH-QA-002` |
| `logout_validation_fail_userId_type_mismatch` | 로그아웃 요청 시 userId path variable 타입이 올바르지 않으면 타입 에러가 발생한다 | `AUTH-QA-003` |
