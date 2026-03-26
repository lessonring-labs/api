# JWT 토큰 제공자 테스트 설계서

## 1. 기본 정보

| 항목 | 내용 |
|-----|-----|
| 대상 파일 | [JwtTokenProviderImplTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/common/security/JwtTokenProviderImplTest.java) |
| 대상 계층 | JWT 보안 유틸리티 |
| 테스트 유형 | 보안 / 단위 |
| 주 우선순위 | P1 |
| 관련 기능 | 시크릿 초기화, access token 생성 및 검증 |

## 2. 테스트 목적

JWT 시크릿 입력 포맷이 달라도 초기화 로직이 정상 동작하고, 발급된 토큰이 검증 가능하며 사용자 식별값을 정확히 반환하는지 확인한다.

## 3. 범위

### 포함 범위

- 평문 시크릿 지원
- Base64 시크릿 지원
- Base64 URL 시크릿 지원
- 토큰 검증 및 사용자 ID 추출

### 제외 범위

- 토큰 만료 검증
- 변조 토큰 검증
- refresh token 전용 로직

## 4. 사전 조건

- 시크릿 값은 JWT HMAC 서명에 사용할 수 있는 길이를 만족해야 한다.
- `init()` 초기화가 정상 수행되어야 한다.

## 5. 상세 테스트 케이스

| ID | 우선순위 | 유형 | 시나리오 | 입력 | 기대 결과 |
|-----|-----|-----|-----|-----|-----|
| JWT-001 | P1 | 보안 | 평문 시크릿 초기화 | 일반 문자열 secret | 토큰 생성 및 검증 성공, userId 일치 |
| JWT-002 | P1 | 보안 | Base64 시크릿 초기화 | Base64 secret | 토큰 생성 및 검증 성공, userId 일치 |
| JWT-003 | P1 | 보안 | Base64 URL 시크릿 초기화 | URL-safe Base64 secret | 토큰 생성 및 검증 성공, userId 일치 |

## 6. 판정 기준

- 세 가지 입력 포맷 모두 동일한 기능 수준을 보장해야 한다.
- 검증 성공 후 userId 추출값이 발급 시 사용한 값과 다르면 실패다.

## 7. 잔여 리스크

- 만료 토큰, 잘못된 서명 토큰, 손상된 토큰 검증 케이스는 현재 문서 범위 밖이다.

## 현재 테스트 메서드 기준

| 메서드 | 설명 | 문서 케이스 |
|-----|-----|-----|
| `initSupportsRawTextSecret` | 일반 문자열 secret 초기화 지원 | `JWT-001` |
| `initSupportsBase64Secret` | Base64 secret 초기화 지원 | `JWT-002` |
| `initSupportsBase64UrlSecret` | Base64 URL secret 초기화 지원 | `JWT-003` |
