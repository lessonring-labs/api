# API Versioning

## 개요

LessOnRing API는 서비스 확장과 호환성을 유지하기 위해  
명확한 API 버전 관리 전략(API Versioning Strategy)을 사용한다.

버전 관리는 다음을 목표로 한다.

- 클라이언트 호환성 유지
- API 변경 관리
- 안정적인 서비스 운영

---

## Version 정책

LessOnRing API는 **URL 기반 버전 관리 방식**을 사용한다.

예시

/api/v1/members  
/api/v1/bookings

---

## Base URL

모든 API는 다음 Base URL을 따른다.

```
/api/v1
```

예시

```
GET /api/v1/members
POST /api/v1/bookings
```

---

## 버전 변경 기준

다음과 같은 경우 API 버전을 증가시킨다.

- Response 구조 변경
- Request 구조 변경
- 필수 파라미터 변경
- 기존 API 동작 변경

---

## 버전 유지 정책

기존 버전은 일정 기간 유지한다.

예시

```
v1 유지
v2 신규 기능 추가
```

구조

```
/api/v1
/api/v2
```

---

## Deprecated 정책

더 이상 사용하지 않는 API는 Deprecated 처리한다.

예시

```
v1 → Deprecated
v2 → Active
```

Deprecated API는 일정 기간 이후 제거한다.

---

## 목적

이 전략을 통해 다음을 보장한다.

- API 안정성
- 클라이언트 호환성
- 서비스 확장성