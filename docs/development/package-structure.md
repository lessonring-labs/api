# Package Structure

## Root Package

com.lessonring.api

---

## 주요 모듈

common  
auth  
studio  
instructor  
member  
product  
pass  
session  
booking  
attendance  
payment  
notification  
analytics  
integration

---

## 모듈 구조

각 모듈은 동일한 패키지 구조를 따른다.

module  
├ api  
├ application  
├ domain  
└ infrastructure

---

## api

- Controller
- Request DTO
- Response DTO

---

## application

- Service
- Use Case
- Business Logic

---

## domain

- Entity
- Domain Model
- Domain Service

---

## infrastructure

- Repository
- Redis
- Kafka
- External API