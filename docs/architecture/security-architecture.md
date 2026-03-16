# Security Architecture

LessonRing Backend의 보안 아키텍처 설계 기준을 정의한다.

이 문서는 다음 목적을 가진다.

- 인증(Authentication) 구조 정의
- 권한(Authorization) 전략 정의
- API 보안 정책 정의
- 토큰 관리 정책 정의
- 보안 확장 방향 제시

---

# 1. Security Architecture Overview

LessonRing Backend는 다음 보안 구조를 사용한다.

JWT 기반 인증  
Stateless API 구조  
Spring Security 기반 필터 체인  
AccessToken + RefreshToken 구조

보안 흐름

Client  
→ Login  
→ AccessToken 발급  
→ API 요청 (Authorization Header)  
→ Security Filter 검증  
→ Controller 접근

---

# 2. Authentication 구조

LessonRing은 **JWT 기반 인증 방식**을 사용한다.

토큰 종류

AccessToken  
RefreshToken

역할

AccessToken  
→ API 인증

RefreshToken  
→ AccessToken 재발급

---

# 3. Login Flow

로그인 흐름

Client  
→ Login API 호출

Server

사용자 검증  
→ AccessToken 생성  
→ RefreshToken 생성  
→ Token 반환

응답 예시

{
"success": true,
"data": {
"accessToken": "...",
"refreshToken": "..."
},
"error": null
}

---

# 4. AccessToken

AccessToken은 API 인증을 위한 JWT 토큰이다.

특징

짧은 만료 시간  
Stateless 인증  
Header 기반 인증

Header

Authorization: Bearer {accessToken}

예시

Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...

---

# 5. RefreshToken

RefreshToken은 AccessToken 재발급을 위한 토큰이다.

특징

긴 만료 시간  
서버 저장 가능  
AccessToken 재발급 용도

RefreshToken 사용 흐름

AccessToken 만료  
→ RefreshToken API 호출  
→ 새 AccessToken 발급

---

# 6. JWT Payload 구조

JWT Payload 예시

{
"sub": "1",
"iat": 1773464060,
"exp": 1773467660
}

설명

sub  
→ 사용자 식별자

iat  
→ 토큰 생성 시간

exp  
→ 토큰 만료 시간

---

# 7. Security Filter Chain

Spring Security Filter Chain 구조

SecurityFilterChain
↓
JWT Authentication Filter
↓
Authentication Manager
↓
Controller

JWT Filter 역할

- Authorization Header 확인
- JWT 검증
- 사용자 인증 설정

---

# 8. JWT Authentication Filter

JWT Filter는 다음 역할을 수행한다.

1 Authorization Header 확인  
2 JWT Token 추출  
3 Token 유효성 검증  
4 사용자 정보 추출  
5 SecurityContext 설정

검증 실패 시

Unauthorized Response 반환

---

# 9. Authorization 전략

LessonRing은 현재 **기본 인증 구조** 중심으로 동작한다.

기본 원칙

로그인 사용자만 API 접근 가능

공개 API

/auth/login

보호 API

/members  
/schedules  
/bookings  
/memberships  
/attendance  
/payment

---

# 10. API Security Policy

API 접근 정책

Public API

/auth/login

Authenticated API

/api/v1/**

즉

인증 없는 접근 불가

---

# 11. Password Security

비밀번호 정책

- 평문 저장 금지
- 해시 저장

권장 방식

BCrypt

예시

BCryptPasswordEncoder

---

# 12. Token Expiration 정책

권장 설정

AccessToken

15분 ~ 60분

RefreshToken

7일 ~ 30일

현재 프로젝트

AccessToken 중심 인증

---

# 13. Logout 정책

Logout 처리 방식

클라이언트 토큰 삭제  
또는

RefreshToken 무효화

Logout API

/api/v1/auth/logout

---

# 14. Token Storage 전략

Client

AccessToken

Memory 또는 Secure Storage

RefreshToken

HttpOnly Cookie 또는 Secure Storage

---

# 15. CORS 정책

API 서버는 CORS 정책을 적용한다.

허용 대상

Frontend Domain

예시

https://lessonring.com

허용 Method

GET  
POST  
PATCH  
DELETE

---

# 16. CSRF 정책

JWT 기반 API는 기본적으로 CSRF 영향이 낮다.

Spring Security 설정

CSRF Disable

이유

Stateless API

---

# 17. Rate Limiting (향후)

보안 강화를 위해 Rate Limit 적용 가능

예

Login 시도 제한  
API 요청 제한

구현 방법

Redis 기반 Rate Limit  
API Gateway Rate Limit

---

# 18. Logging & Security Monitoring

다음 상황에서 보안 로그를 기록한다.

로그인 실패  
토큰 검증 실패  
비정상 API 접근  
권한 없는 접근

로그 레벨

WARN  
ERROR

---

# 19. Future Security Enhancements

향후 보안 확장

Role 기반 권한 관리  
RBAC 도입  
API Gateway 인증  
OAuth2 Social Login  
Multi Factor Authentication

---

# 20. Security Design Principles

LessonRing 보안 설계 원칙

Stateless 인증 유지  
JWT 기반 인증  
민감 데이터 최소 저장  
AccessToken 짧은 만료  
RefreshToken 재발급 구조

---

# 21. Summary

LessonRing Backend는 다음 보안 구조를 사용한다.

JWT 기반 인증  
Spring Security Filter Chain  
AccessToken / RefreshToken 구조  
Authorization Header 인증

향후

RBAC  
OAuth2  
MFA  
API Gateway

확장을 고려하여 설계한다.