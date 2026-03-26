# QA - JWT 토큰 제공자 테스트 케이스

## 1. 문서 기본 정보

| 항목 | 내용 |
|-----|-----|
| 문서명 | JWT 토큰 제공자 테스트 케이스 |
| 기준 테스트 파일 | [JwtTokenProviderImplTest.java](/Users/devyn/IdeaProjects/lessonring-labs/api/src/test/java/com/lessonring/api/common/security/JwtTokenProviderImplTest.java) |
| 모듈 | 보안 |
| 테스트 유형 | 단위 / 보안 |
| 우선순위 | P1 |

## 2. 테스트 목적

시크릿 포맷이 달라져도 JWT 초기화와 토큰 검증이 정상 동작하는지 검증한다.

## 3. 테스트 케이스 상세

### JWT-QA-001 일반 문자열 시크릿 검증

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | JWT provider 초기화 가능 |
| 입력값 | 일반 문자열 secret, userId=1 |
| 수행절차 | 토큰 생성 후 검증 및 userId 추출 수행 |
| 예상결과 | validate=true, userId=1 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### JWT-QA-002 Base64 시크릿 검증

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | JWT provider 초기화 가능 |
| 입력값 | Base64 secret, userId=2 |
| 수행절차 | 토큰 생성 후 검증 및 userId 추출 수행 |
| 예상결과 | validate=true, userId=2 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

### JWT-QA-003 Base64 URL 시크릿 검증

| 항목 | 내용 |
|-----|-----|
| 우선순위 | P1 |
| 사전조건 | JWT provider 초기화 가능 |
| 입력값 | Base64 URL secret, userId=3 |
| 수행절차 | 토큰 생성 후 검증 및 userId 추출 수행 |
| 예상결과 | validate=true, userId=3 |
| 실제결과 |  |
| 판정 | 미수행 |
| 증빙 |  |
| 결함 ID |  |

## 4. 수행 요약

| 항목 | 내용 |
|-----|-----|
| 수행자 |  |
| 수행일 |  |
| 수행 버전 |  |
| 결과 요약 |  |
| 비고 |  |

## 현재 테스트 메서드 기준

| 메서드 | 설명 | QA 케이스 |
|-----|-----|-----|
| `initSupportsRawTextSecret` | 일반 문자열 secret 초기화 지원 | `JWT-QA-001` |
| `initSupportsBase64Secret` | Base64 secret 초기화 지원 | `JWT-QA-002` |
| `initSupportsBase64UrlSecret` | Base64 URL secret 초기화 지원 | `JWT-QA-003` |
