# Security Architecture

## 개요

LessOnRing 플랫폼은 사용자 인증과 권한 관리를 위해  
JWT 기반 인증 구조와 RBAC 권한 관리 모델을 사용한다.

보안 구조는 다음을 목표로 한다.

- 안전한 사용자 인증
- 권한 기반 접근 제어
- API 보호

---

## 인증 방식

LessOnRing은 **JWT 기반 인증**을 사용한다.

로그인 흐름

Client  
↓  
Login Request  
↓  
JWT Token 발급  
↓  
Client Token 저장  
↓  
API 호출 시 Authorization Header 전달

Header 예시

```
Authorization: Bearer access_token
```

---

## OAuth Login

LessOnRing은 Kakao OAuth 로그인도 지원한다.

OAuth 흐름

Client  
↓  
Kakao Login  
↓  
OAuth 인증  
↓  
Access Token 발급  
↓  
LessOnRing JWT 발급

---

## Role 기반 권한 관리

RBAC(Role Based Access Control)을 사용한다.

사용자 Role

Member  
Instructor  
AcademyAdmin

예시 권한

Member
- 수업 예약
- 수강권 조회

Instructor
- 수업 관리
- 출석 관리

AcademyAdmin
- 회원 관리
- 결제 관리
- 스튜디오 관리

---

## API 보호

모든 API는 다음 방식으로 보호된다.

- JWT 인증
- Role 기반 권한 검사
- HTTPS 사용

---

## 보안 구성

보안 구성 요소

Spring Security  
JWT Filter  
OAuth Login (Kakao)

---

## 목적

이 구조를 통해 다음을 보장한다.

- 안전한 사용자 인증
- 권한 기반 API 접근 제어
- 플랫폼 보안 강화