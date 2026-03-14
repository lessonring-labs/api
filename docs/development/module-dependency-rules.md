# Module Dependency Rules

LessOnRing API는 모듈 간 의존성을 명확하게 관리하기 위해  
계층별 의존 규칙을 정의한다.

---

## 허용된 의존 관계

api → application  
application → domain  
application → infrastructure  
infrastructure → domain

---

## 금지된 의존 관계

domain → api  
domain → application  
api → infrastructure 직접 호출

---

## 모듈 통신 규칙

모듈 간 통신은 **Application Layer를 통해서만 수행한다.**

예

Controller  
→ Application Service  
→ Domain  
→ Infrastructure

직접 호출 예 (금지)

Controller → Repository  
Controller → Kafka Producer

---

## 목적

이 규칙을 통해 다음을 보장한다.

- 계층 구조 유지
- 코드 의존성 명확화
- 테스트 용이성 확보
- 유지보수성 향상