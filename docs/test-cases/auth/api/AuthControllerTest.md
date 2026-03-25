# 인증 컨트롤러 테스트 설계서

## 1. 기본 정보

| 항목 | 내용 |
|-----|-----|
| 대상 파일 | [AuthControllerTest.java](/C:/wms/api/src/test/java/com/lessonring/api/auth/api/AuthControllerTest.java) |
| 대상 계층 | 인증 API 컨트롤러 |
| 테스트 유형 | API |
| 주 우선순위 | P1 |
| 관련 기능 | 로그인, 토큰 재발급, 로그아웃 입력 검증 |

## 2. 테스트 목적

인증 관련 API가 잘못된 요청을 비즈니스 로직까지 전달하지 않고, 표준화된 에러 응답으로 반환하는지 확인한다.

## 3. 범위

### 포함 범위

- 로그인 요청 DTO validation
- 재발급 요청 DTO validation
- 로그아웃 path variable 타입 검증
- 공통 에러 응답 구조 확인

### 제외 범위

- 로그인 성공 시 토큰 발급
- refresh token 실제 저장/삭제
- 인증 권한 정책 전체 플로우

## 4. 사전 조건

- `MockMvc` 실행 환경이 정상 구성되어 있어야 한다.
- 글로벌 예외 처리기가 활성화되어 있어야 한다.
- validation annotation이 컨트롤러 경계에서 적용되어야 한다.

## 5. 테스트 데이터

| 항목 | 값 |
|-----|-----|
| 로그인 요청 | `userId` 누락 JSON |
| 재발급 요청 | `refreshToken` 누락 JSON |
| 로그아웃 경로 | `/api/v1/auth/logout/abc` |

## 6. 상세 테스트 케이스

| ID | 우선순위 | 유형 | 시나리오 | 입력 | 수행 절차 | 기대 결과 |
|-----|-----|-----|-----|-----|-----|-----|
| AUTH-API-001 | P1 | API | 로그인 요청 필수값 누락 | `userId` 없음 | 로그인 API 호출 | `400`, `success=false`, validation 에러 코드 |
| AUTH-API-002 | P1 | API | 토큰 재발급 요청 필수값 누락 | `refreshToken` 없음 | 재발급 API 호출 | `400`, `success=false`, validation 에러 코드 |
| AUTH-API-003 | P1 | API | 로그아웃 path variable 타입 오류 | 숫자 대신 `abc` | 로그아웃 API 호출 | `400`, `success=false`, 타입 변환 에러 코드 |

## 7. 판정 기준

- 잘못된 입력은 반드시 `4xx`로 종료되어야 한다.
- 공통 응답 포맷이 깨지면 실패다.
- 컨트롤러가 예외를 삼키거나 성공 응답을 반환하면 실패다.

## 8. 추적 포인트

- 인증 API 입력 검증 회귀 여부
- 글로벌 예외 처리 규칙 유지 여부

## 9. 잔여 리스크

- 성공 시나리오 보장이 없다.
- 실제 인증 상태 변화나 refresh token lifecycle은 별도 테스트가 필요하다.
