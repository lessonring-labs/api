# JWT 토큰 제공자 테스트 설계서

## 1. 대상 정보

- 테스트 파일: [JwtTokenProviderImplTest.java](/C:/wms/api/src/test/java/com/lessonring/api/common/security/JwtTokenProviderImplTest.java)
- 대상 계층: JWT 보안 유틸리티
- 테스트 유형: 단위 테스트

## 2. 문서 목적

JWT 토큰 발급기에 입력되는 시크릿 포맷이 달라도 초기화와 토큰 검증 로직이 동일하게 동작하는지 검증한다.

## 3. 검증 범위

- 일반 문자열 시크릿 초기화
- Base64 시크릿 초기화
- Base64 URL 시크릿 초기화
- access token 생성 및 사용자 ID 추출

## 4. 사전 조건

- `JwtTokenProviderImpl` 초기화 메서드가 정상 호출되어야 한다.
- 테스트 시크릿 길이가 JWT 서명 키 요구 조건을 만족해야 한다.

## 5. 상세 테스트 케이스

| ID | 시나리오 | 입력 조건 | 수행 절차 | 기대 결과 |
|-----|-----|-----|-----|-----|
| JWT-001 | 일반 문자열 시크릿 사용 | 평문 시크릿 값 | 토큰 생성 후 검증 및 userId 추출 | 토큰 유효, userId 정확 |
| JWT-002 | Base64 시크릿 사용 | Base64 인코딩 시크릿 값 | 토큰 생성 후 검증 및 userId 추출 | 토큰 유효, userId 정확 |
| JWT-003 | Base64 URL 시크릿 사용 | URL-safe Base64 시크릿 값 | 토큰 생성 후 검증 및 userId 추출 | 토큰 유효, userId 정확 |

## 6. 합격 기준

- 세 가지 시크릿 포맷 모두에서 토큰 검증이 성공해야 한다.
- userId 추출 결과가 토큰 생성 시 입력한 값과 일치해야 한다.

## 7. 리스크 및 보완 포인트

- 만료 토큰, 변조 토큰, 잘못된 서명 토큰 검증은 현재 문서 범위에 포함되지 않는다.
- refresh token 관련 검증도 별도 보강이 필요하다.
