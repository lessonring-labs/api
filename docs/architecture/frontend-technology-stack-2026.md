# LessonRing Frontend Technology Stack 2026

이 문서는 LessonRing 서비스의 Web Frontend, Admin Web, React Native Mobile App을 위한 2026 기준 표준 프론트엔드 기술 스택과 아키텍처 원칙을 정의한다.

LessonRing은 레슨 스튜디오 운영을 위한 통합 관리 서비스이며, 프론트엔드는 다음 도메인을 안정적으로 지원해야 한다.

- 스튜디오 관리
- 강사 관리
- 회원 관리
- 이용권 관리
- 수업 일정 관리
- 예약 관리
- 출석 관리
- 결제 관리
- 알림
- 운영 지표 및 분석

이 문서의 목적은 단순 기술 나열이 아니라, LessonRing의 실제 업무 흐름을 기준으로 프론트엔드 표준을 정하는 것이다.

---

# 1. LessonRing 서비스 특성

LessonRing 프론트엔드는 일반 콘텐츠 서비스와 다른 운영형 제품 특성을 가진다.

## 1.1 운영 중심 화면 비중이 높다

관리자와 스튜디오 운영자는 다음 업무를 매일 반복한다.

- 회원 등록 및 수정
- 이용권 판매 및 상태 확인
- 수업 스케줄 생성 및 변경
- 예약 현황 확인
- 출석 처리 및 no-show 관리
- 결제 승인, 환불, 결제 오류 대응
- 알림 발송 및 이력 확인

따라서 LessonRing FE는 단순 랜딩 페이지 중심 UI가 아니라, 데이터 정합성과 업무 처리 속도가 중요한 운영형 화면을 우선 설계해야 한다.

## 1.2 도메인 간 연결이 강하다

LessonRing의 핵심 엔터티는 서로 강하게 연결된다.

- `member`는 `membership`, `booking`, `attendance`, `payment`, `notification`과 연결된다.
- `schedule`은 `booking`과 연결된다.
- `payment`는 `membership` 생성 및 환불 플로우와 연결된다.
- `booking`은 출석, no-show, 이용권 차감과 연결된다.

프론트엔드에서 각 화면을 독립적으로 만들더라도, 실제 사용자 흐름은 도메인 횡단 형태로 동작한다.

예시:

- 회원 상세 화면에서 이용권, 예약, 결제, 출석 이력을 함께 본다.
- 결제 완료 이후 이용권 생성 결과를 즉시 확인해야 한다.
- 예약 취소 이후 이용권 사용 상태와 잔여 횟수가 반영되어야 한다.

## 1.3 실시간성보다 정합성이 더 중요하다

LessonRing의 주요 업무는 다음 특성을 가진다.

- 잘못된 예약 가능 수량 노출은 운영 사고로 이어진다.
- 중복 결제 승인이나 환불 오류는 직접 금전 손실로 이어진다.
- no-show 처리나 출석 처리 오류는 이용권 차감 오류로 이어진다.

따라서 프론트엔드는 빠른 반응성뿐 아니라, 서버 상태와의 동기화 정확성을 더 우선해야 한다.

---

# 2. 제품 관점 아키텍처 목표

LessonRing FE 아키텍처의 목표는 다음과 같다.

- 웹과 모바일에서 일관된 개발 방식 유지
- 운영 화면과 사용자 화면을 동시에 감당할 수 있는 구조 확보
- 결제, 예약, 출석 같은 정합성 민감 도메인에서 안전한 상태 처리
- API 계약 변경에 강한 타입 기반 구조 확보
- 장애 발생 시 운영팀이 빠르게 원인을 추적할 수 있는 관측성 확보

---

# 3. 최종 표준 기술 스택

## 3.1 Core Engine & Framework

| 영역 | 표준 기술 | LessonRing 내 역할 |
|-----|-----|-----|
| UI Library | React | 웹과 모바일 공통 컴포넌트 사고방식의 기준 |
| Web Framework | Next.js (App Router) | 사용자 웹, 관리자 웹, SEO/SSR 대응, 라우팅 표준 |
| Language | TypeScript | 예약, 결제, 이용권 등 정합성 민감 도메인의 타입 안정성 확보 |
| Mobile | React Native | 회원용 앱 또는 운영용 모바일 앱 확장 기반 |

## 3.2 Data & Backend Layer

| 영역 | 표준 기술 | LessonRing 내 역할 |
|-----|-----|-----|
| Database | PostgreSQL | 백엔드 주 데이터 저장소 |
| ORM / Schema | Prisma | FE BFF 또는 Node 계층 확장 시 스키마 정합성 확보 |
| Integration | REST API / Webhook | Spring Boot API 연동 및 외부 결제/알림 이벤트 수신 |

## 3.3 State Management & Data Fetching

| 영역 | 표준 기술 | LessonRing 내 역할 |
|-----|-----|-----|
| Server State | TanStack Query | 회원, 예약, 결제, 통계 데이터 조회 및 캐싱 |
| Client State | Zustand | 필터, 선택 상태, 세션 메타, UI 전역 상태 관리 |
| Form State | React Hook Form | 회원 등록, 이용권 등록, 결제 생성, 일정 등록 폼 처리 |
| Validation | Zod | 입력 검증, API 응답 검증, 결제/외부 연동 payload 검증 |

## 3.4 UI & Design System

| 영역 | 표준 기술 | LessonRing 내 역할 |
|-----|-----|-----|
| Styling | Tailwind CSS | 운영 화면과 공통 UI를 빠르게 구성 |
| Admin Grid | AG Grid Community | 회원/예약/결제/정산성 화면의 대량 데이터 처리 |
| Mobile Navigation | React Navigation | 모바일 화면 전환 표준 |
| Mobile List | FlatList | 일정, 예약, 알림 목록 최적화 |
| UI Workshop | Storybook | LessonRing 공통 UI 컴포넌트와 디자인 시스템 관리 |

## 3.5 API Communication & Quality

| 영역 | 표준 기술 | LessonRing 내 역할 |
|-----|-----|-----|
| HTTP Client | Axios | 토큰 처리, 공통 에러 응답 처리, 인터셉터 관리 |
| Package Manager | Yarn | 패키지 의존성 관리 표준 |
| Testing | Jest / React Testing Library | 핵심 업무 플로우 테스트 |
| Code Quality | ESLint / Prettier | 코드 품질 및 포맷 일관성 확보 |

## 3.6 Architecture & Monitoring

| 영역 | 표준 기술 | LessonRing 내 역할 |
|-----|-----|-----|
| UI Architecture | Component Based / Atomic Design | 공통 UI와 도메인 기능 화면의 책임 분리 |
| Error Monitoring | Sentry | 결제, 예약, 인증 오류 추적 |
| Uptime Monitoring | Watchdog | 사용자 앱 및 관리자 웹의 외부 가용성 감시 |

---

# 4. LessonRing에 이 스택이 적합한 이유

## 4.1 React + Next.js

LessonRing 웹은 다음 두 가지를 동시에 만족해야 한다.

- 빠른 화면 개발
- 장기 유지보수 가능한 구조

Next.js App Router는 다음 장점이 있다.

- 관리자 웹과 사용자 웹을 같은 기준으로 설계할 수 있다.
- SSR과 정적 렌더링을 함께 사용할 수 있다.
- 인증, 라우팅, 레이아웃, 서버 컴포넌트 전략을 표준화할 수 있다.

LessonRing은 단순 블로그형 서비스가 아니므로, 페이지 수보다 업무 흐름 복잡도가 높다. Next.js는 이 복잡도를 프레임워크 수준에서 줄이는 데 유리하다.

## 4.2 TypeScript

LessonRing 도메인은 상태 전이가 명확하다.

예시:

- `BookingStatus`: `RESERVED`, `ATTENDED`, `CANCELED`, `NO_SHOW`
- `PaymentStatus`: `READY`, `COMPLETED`, `CANCELED`, `REFUNDED`, `FAILED`
- `MemberStatus`: `ACTIVE`, `INACTIVE`, `BLOCKED`

이런 상태를 문자열로 느슨하게 다루면 운영 사고가 발생한다. TypeScript는 상태 전이 로직, 화면 분기, API 계약을 안전하게 유지하는 데 필수다.

## 4.3 TanStack Query

LessonRing은 서버 데이터 중심 서비스다.

- 회원 목록
- 예약 목록
- 스케줄 캘린더
- 출석 현황
- 결제 상세
- 분석 지표

이 데이터를 전역 스토어에 직접 보관하면 캐시 정책과 동기화 책임이 흐려진다. TanStack Query는 조회, refetch, invalidation, 로딩/에러 상태를 분리해 서버 상태를 가장 안정적으로 다룰 수 있다.

## 4.4 Zustand

LessonRing의 전역 상태는 많아 보여도 실제로는 대부분 UI 상태다.

- 검색 조건
- 선택한 스튜디오
- 현재 열려 있는 상세 패널
- 캘린더 뷰 모드
- 관리자 화면의 일시적 선택 상태

이 용도에는 보일러플레이트가 적고 가벼운 Zustand가 적합하다.

## 4.5 React Hook Form + Zod

LessonRing 관리자 화면은 입력 폼이 많다.

- 회원 등록
- 강사 등록
- 이용권 생성
- 스케줄 생성
- 예약 생성
- 결제 요청
- 환불 사유 입력

이 폼들은 단순 입력창이 아니라, 비즈니스 제약을 반영해야 한다.

- 필수값 검증
- 날짜 범위 검증
- 횟수형 이용권 검증
- 결제 금액 검증
- 상태별 입력 가능 여부 검증

React Hook Form과 Zod 조합은 복잡한 폼에서 성능과 검증 일관성을 함께 확보하기 좋다.

## 4.6 AG Grid Community

LessonRing Admin Web은 표 형식 데이터가 많다.

- 회원 목록
- 예약 이력
- 출석 기록
- 결제 내역
- 알림 발송 이력

운영 화면은 단순 테이블이 아니라 다음 기능이 필요하다.

- 정렬
- 서버 페이징
- 컬럼 표시 제어
- 다중 선택
- 빠른 검색
- CSV/엑셀 추출 연계

따라서 Admin Web의 핵심 리스트 화면은 AG Grid 기반으로 표준화하는 것이 적합하다.

---

# 5. LessonRing 프론트엔드 애플리케이션 구성

## 5.1 사용자 Web

주요 책임:

- 로그인 및 인증
- 내 이용권 확인
- 수업 일정 조회
- 예약 및 취소
- 결제 진행
- 알림 확인

권장 스택:

- `Next.js`
- `React`
- `TypeScript`
- `TanStack Query`
- `React Hook Form`
- `Zod`
- `Axios`
- `Tailwind CSS`
- `Sentry`

## 5.2 Admin Web

주요 책임:

- 스튜디오 운영 대시보드
- 회원/강사/이용권/스케줄/예약/출석/결제/알림 관리
- 대량 조회 및 필터링
- 장애 대응용 운영 화면

권장 스택:

- `Next.js`
- `React`
- `TypeScript`
- `TanStack Query`
- `Zustand`
- `React Hook Form`
- `Zod`
- `AG Grid Community`
- `Tailwind CSS`
- `Storybook`
- `Axios`
- `Jest`
- `React Testing Library`
- `Sentry`
- `Watchdog`

## 5.3 Mobile App

주요 책임:

- 회원의 예약 조회 및 취소
- 일정 확인
- 결제 상태 확인
- 알림 수신
- 출석 또는 멤버십 관련 간편 조회

권장 스택:

- `React Native`
- `TypeScript`
- `TanStack Query`
- `Zustand`
- `React Hook Form`
- `Zod`
- `React Navigation`
- `FlatList`
- `Axios`
- `Sentry`

---

# 6. LessonRing 도메인별 FE 책임

## 6.1 Member

회원 도메인 화면은 다음을 지원해야 한다.

- 회원 목록 조회
- 회원 상세 조회
- 회원 등록 및 수정
- 상태별 화면 분기
- 회원 기준 예약/출석/결제/이용권 연결 조회

프론트엔드 포인트:

- 회원 상세는 단일 엔터티 화면이 아니라 운영 허브 화면으로 설계한다.
- 이름, 연락처, 상태, 가입일, 메모 등 핵심 필드를 빠르게 확인할 수 있어야 한다.

## 6.2 Membership

이용권 도메인은 LessonRing 운영의 핵심이다.

- 이용권 생성
- 잔여 횟수 및 유효기간 표시
- 회원별 이용권 이력 조회
- 환불/만료/사용 완료 상태 반영

프론트엔드 포인트:

- 예약 가능 여부와 연결되는 데이터를 명확히 표시해야 한다.
- 결제 완료 후 생성된 이용권을 즉시 반영해야 한다.

## 6.3 Schedule

일정 화면은 단순 목록보다 시간축 중심 UX가 중요하다.

- 일자별/주간별 수업 조회
- 강사 기준 필터링
- 스튜디오 기준 필터링
- 정원과 예약 현황 확인

프론트엔드 포인트:

- 캘린더 뷰와 리스트 뷰를 함께 고려한다.
- 예약 가능 상태와 마감 상태를 시각적으로 명확히 구분해야 한다.

## 6.4 Booking

예약 도메인은 정합성 민감도가 높다.

- 예약 생성
- 예약 취소
- 예약 상태 확인
- no-show 처리 결과 확인

프론트엔드 포인트:

- 낙관적 업데이트는 제한적으로 사용한다.
- 예약 완료 직후 서버 응답 기준으로 잔여 좌석, 이용권 상태를 다시 동기화한다.
- 중복 예약 방지 UX가 필요하다.

## 6.5 Attendance

출석 도메인은 예약과 이용권 사용에 직접 연결된다.

- 출석 체크
- 결석/no-show 상태 반영
- 회원별 출석 이력 조회

프론트엔드 포인트:

- 현장 운영자가 빠르게 처리할 수 있도록 모바일 친화 UI도 고려한다.
- 잘못된 탭 한 번이 운영 이슈가 되지 않도록 확인 UX를 둔다.

## 6.6 Payment

결제 도메인은 가장 높은 안정성이 필요하다.

- 결제 생성
- 결제 승인
- 결제 실패 확인
- 환불 처리
- PG 연동 결과 확인

프론트엔드 포인트:

- `READY`, `COMPLETED`, `FAILED`, `REFUNDED` 상태에 따라 액션 버튼을 엄격히 제어한다.
- 결제 완료 후 membership 생성 여부를 함께 노출한다.
- 환불 플로우는 확인 단계와 사유 입력 단계를 분리한다.

## 6.7 Notification

알림 도메인은 운영 보조 성격이 강하다.

- 발송 이력 조회
- 회원별 알림 상태 확인
- 특정 이벤트 이후 발송 성공 여부 추적

프론트엔드 포인트:

- 운영자 기준으로 실패 알림을 빠르게 찾을 수 있어야 한다.
- 회원 상세와 연결해 보는 UX가 유용하다.

## 6.8 Analytics

분석 화면은 단순 차트보다 운영 의사결정 보조가 목적이다.

- 일별 예약 수
- 결제 현황
- 출석률
- 이용권 판매 추이

프론트엔드 포인트:

- 조회 기간 필터가 중요하다.
- 대시보드 숫자와 원천 리스트 화면의 연결성이 필요하다.

---

# 7. 상태 관리 표준

## 7.1 TanStack Query 사용 대상

다음 데이터는 반드시 `TanStack Query`로 관리한다.

- 회원 목록 및 상세
- 이용권 목록 및 상세
- 스케줄 캘린더 데이터
- 예약 목록 및 상세
- 출석 이력
- 결제 목록 및 상세
- 알림 이력
- 대시보드 집계 데이터

규칙:

- Query Key는 도메인 기준으로 표준화한다.
- mutation 후 invalidation 규칙을 화면별이 아니라 도메인별로 정의한다.
- 결제/예약/출석 관련 mutation은 성공 후 관련 엔터티를 함께 재조회한다.

예시:

- `members.list`
- `members.detail`
- `bookings.list`
- `bookings.detail`
- `payments.list`
- `payments.detail`
- `analytics.dashboard`

## 7.2 Zustand 사용 대상

다음 상태는 `Zustand`에 둔다.

- 관리자 필터 조건
- 캘린더 뷰 모드
- 현재 선택한 스튜디오
- 다이얼로그 오픈 상태
- 다중 선택된 행 정보

다음 상태는 두지 않는다.

- API 응답 원본
- 결제 상세 응답
- 예약 목록 데이터
- 출석 리스트 데이터

## 7.3 폼 상태 표준

다음 폼은 `React Hook Form + Zod`를 기본으로 사용한다.

- 회원 등록 폼
- 이용권 생성 폼
- 일정 생성 폼
- 예약 생성 폼
- 결제 생성 폼
- 환불 폼

---

# 8. API 통신 표준

## 8.1 LessonRing API 특성

현재 LessonRing 백엔드는 Spring Boot REST API 기반이다.

프론트엔드는 다음과 같은 패턴을 고려해야 한다.

- 인증 토큰 기반 API 호출
- 운영 화면의 목록/상세/검색 API 호출
- 결제 승인 및 환불 요청
- Webhook 후속 상태 반영

## 8.2 Axios 인스턴스 표준

Axios 클라이언트는 최소 다음 단위로 분리한다.

- `publicApiClient`
- `memberApiClient`
- `adminApiClient`

공통 인터셉터 책임:

- Access Token 주입
- Refresh Token 갱신 처리
- 공통 에러 포맷 정규화
- 권한 오류 처리
- 요청 추적 헤더 추가

## 8.3 API 계층 구조

LessonRing FE는 다음 구조를 따른다.

```text
Page / Screen
-> Feature Hook
-> Domain API Module
-> Axios Client
```

예시:

- `useMemberDetailQuery()`
- `useCreateBookingMutation()`
- `useRefundPaymentMutation()`

컴포넌트 내부에서 직접 URL을 하드코딩해 호출하지 않는다.

## 8.4 외부 연동 처리 기준

결제나 외부 OAuth 연동처럼 신뢰도가 낮은 응답은 Zod로 응답 검증한다.

우선 적용 대상:

- Toss Payments 승인 응답
- Toss Payments 환불 응답
- 외부 OAuth 프로필 응답
- Webhook payload 응답형 데이터

---

# 9. UI / 디자인 시스템 표준

## 9.1 디자인 원칙

LessonRing UI는 화려함보다 운영 효율이 중요하다.

우선순위:

- 빠른 정보 인지
- 오류 가능성 감소
- 반복 업무 처리 속도
- 모바일과 데스크톱 모두에서의 가독성

## 9.2 Tailwind CSS 운영 원칙

- 색상은 상태 의미와 연결한다.
- 예약 가능, 마감, 취소, 환불, 실패 상태는 명확히 구분한다.
- 운영 화면에서 과도한 장식보다 정보 밀도와 정렬 일관성을 우선한다.

## 9.3 Storybook 운영 대상

다음 공통 컴포넌트는 Storybook에서 관리한다.

- 버튼
- 입력 컴포넌트
- 상태 배지
- 테이블 셀 렌더러
- 모달
- 폼 섹션
- 날짜/시간 표시 컴포넌트
- 빈 상태 / 에러 상태 컴포넌트

## 9.4 AG Grid 적용 화면

다음 화면은 AG Grid 적용을 기본값으로 한다.

- 회원 목록
- 예약 목록
- 출석 목록
- 결제 목록
- 알림 발송 이력

---

# 10. 권장 폴더 구조

```text
src
├─ app
├─ widgets
├─ features
├─ entities
├─ shared
│  ├─ api
│  ├─ auth
│  ├─ config
│  ├─ hooks
│  ├─ lib
│  ├─ types
│  ├─ ui
│  └─ utils
├─ styles
└─ stories
```

LessonRing 기준 역할은 다음과 같다.

- `app`: 라우트, 레이아웃, 인증 경계
- `widgets`: 대시보드 섹션, 회원 상세 패널, 결제 요약 블록
- `features`: 예약 생성, 결제 환불, 출석 체크, 회원 검색
- `entities`: member, booking, payment, membership, schedule 등 도메인 단위 모델과 UI
- `shared`: 공통 API, 인증, 유틸리티, 디자인 시스템

---

# 11. 테스트 표준

## 11.1 반드시 테스트해야 하는 LessonRing 핵심 플로우

- 로그인 및 인증 유지
- 회원 생성 및 조회
- 이용권 생성 및 표시
- 예약 생성 및 취소
- 출석 처리
- 결제 승인
- 환불 처리
- 권한별 화면 접근 제어

## 11.2 테스트 우선순위

1. 결제, 예약, 출석 같은 정합성 민감 플로우
2. 관리자 운영의 대량 조회 및 필터 기능
3. 공통 폼 검증 로직
4. 공통 UI 컴포넌트

## 11.3 테스트 도구

- `Jest`
- `React Testing Library`

필요 시 다음을 추가한다.

- `MSW`

---

# 12. 운영 및 관측성 표준

## 12.1 Sentry

Sentry는 다음 이벤트를 우선 추적한다.

- 결제 승인 실패
- 환불 요청 실패
- 예약 생성 실패
- 인증 토큰 갱신 실패
- 관리자 주요 화면 런타임 오류

## 12.2 Watchdog

외부 관점 가용성 체크 대상:

- 사용자 웹 메인 엔드포인트
- 관리자 웹 로그인 엔드포인트
- 결제 완료 후 리다이렉트 핵심 경로

## 12.3 운영 로그 연계

프론트엔드 오류는 다음 정보와 함께 남기는 것이 좋다.

- 사용자 역할
- 화면 경로
- studioId
- memberId
- bookingId
- paymentId

민감정보는 마스킹한다.

---

# 13. 권장 개발 규칙

## 13.1 필수 규칙

- `any` 사용을 최소화한다.
- 상태 enum은 문자열 하드코딩 대신 타입과 상수로 관리한다.
- 결제/예약/출석 액션 버튼은 상태 기반으로 활성화 여부를 통제한다.
- 멱등성이 필요한 액션은 중복 클릭 방지 UX를 제공한다.
- 금전 관련 화면은 optimistic update보다 서버 재동기화를 우선한다.

## 13.2 금지 규칙

- 예약 가능 여부를 프론트엔드 로컬 계산만으로 확정 표시하는 것
- 결제 완료를 클라이언트 상태만으로 성공 처리하는 것
- 공통 컴포넌트에 도메인 특화 로직을 섞는 것
- 서버 상태를 Zustand에 중복 저장하는 것
- 관리자 테이블을 임의 구현으로 중복 생산하는 것

---

# 14. LessonRing 최종 권고안

## Web / Admin 표준

- `React`
- `Next.js (App Router)`
- `TypeScript`
- `TanStack Query`
- `Zustand`
- `React Hook Form`
- `Zod`
- `Tailwind CSS`
- `AG Grid Community`
- `Axios`
- `Storybook`
- `Jest`
- `React Testing Library`
- `ESLint`
- `Prettier`
- `Sentry`
- `Watchdog`

## Mobile 표준

- `React Native`
- `TypeScript`
- `TanStack Query`
- `Zustand`
- `React Hook Form`
- `Zod`
- `React Navigation`
- `FlatList`
- `Axios`
- `Sentry`

이 조합은 LessonRing의 실제 운영 요구를 만족한다.

- 운영형 Admin Web에 필요한 대량 데이터 처리
- 예약/결제/출석 도메인의 높은 정합성 요구
- 회원 중심 통합 조회 UX
- 웹과 모바일 간 개발 방식 통일
- 장기 유지보수 가능한 타입 기반 구조

---

# 15. 결론

LessonRing 프론트엔드의 표준은 범용적인 최신 조합을 따르는 것이 아니라, 실제 스튜디오 운영 업무를 안전하고 빠르게 처리할 수 있는 구조를 택하는 것이다.

따라서 LessonRing FE 표준은 다음과 같이 정의한다.

`React + Next.js + TypeScript`를 중심으로, `TanStack Query`, `Zustand`, `React Hook Form`, `Zod`, `Tailwind CSS`, `AG Grid Community`, `Storybook`, `Axios`, `Jest`, `Sentry`를 결합한 운영형 프론트엔드 아키텍처를 표준으로 채택한다.
