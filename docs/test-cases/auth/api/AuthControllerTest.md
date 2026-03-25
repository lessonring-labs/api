# 인증 컨트롤러 테스트 설계서

## 1. 대상 정보

- 테스트 파일: [AuthControllerTest.java](/C:/wms/api/src/test/java/com/lessonring/api/auth/api/AuthControllerTest.java)
- 대상 계층: 인증 API 컨트롤러
- 테스트 유형: API 입력 검증 테스트

## 2. 문서 목적

인증 API 진입점에서 잘못된 요청이 적절한 HTTP 상태 코드와 표준 에러 포맷으로 처리되는지 확인한다.

## 3. 검증 범위

- 로그인 요청 DTO 검증
- 토큰 재발급 요청 DTO 검증
- 로그아웃 path variable 타입 검증
- 공통 에러 응답 포맷 유지 여부

## 4. 비검증 범위

- 로그인 성공 후 토큰 발급 로직
- 실제 Refresh Token 저장/삭제 로직
- 인증/인가 정책의 전체 플로우

## 5. 사전 조건

- 애플리케이션 컨텍스트가 정상적으로 기동되어야 한다.
- `MockMvc` 테스트 환경이 정상 구성되어야 한다.
- 글로벌 예외 처리 규칙이 적용되어 있어야 한다.

## 6. 주요 테스트 데이터

- 로그인 요청: `userId` 누락 요청
- 토큰 재발급 요청: `refreshToken` 누락 요청
- 로그아웃 요청 경로: `/api/v1/auth/logout/abc`

## 7. 상세 테스트 케이스

| ID | 시나리오 | 입력 조건 | 수행 절차 | 기대 결과 |
|-----|-----|-----|-----|-----|
| AUTH-API-001 | 로그인 요청 필수값 누락 | `userId` 없는 JSON 요청 | `/api/v1/auth/login` 호출 | `400 Bad Request`, `success=false`, validation 에러 코드 반환 |
| AUTH-API-002 | 토큰 재발급 필수값 누락 | `refreshToken` 없는 JSON 요청 | `/api/v1/auth/refresh` 호출 | `400 Bad Request`, `success=false`, validation 에러 코드 반환 |
| AUTH-API-003 | 로그아웃 path variable 타입 오류 | 숫자가 아닌 문자열 `abc` 사용 | `/api/v1/auth/logout/abc` 호출 | `400 Bad Request`, 타입 변환 에러 코드 반환 |

## 8. 합격 기준

- 잘못된 입력은 모두 4xx 계열 응답이어야 한다.
- 성공 응답으로 잘못 처리되면 실패다.
- 에러 응답은 공통 응답 포맷을 유지해야 한다.

## 9. 리스크 및 보완 포인트

- 현재 테스트는 인증 성공 시나리오를 다루지 않는다.
- 토큰 발급/재발급/로그아웃 성공 플로우는 별도 서비스 또는 통합 테스트 보강이 필요하다.
