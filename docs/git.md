# Git Commit Convention

LessonRing 프로젝트의 Git 커밋 규칙을 정의한다.

목표

- Git 히스토리를 문서처럼 관리
- 변경 이력 파악 용이
- 기능 단위 개발 추적 가능
- 프로젝트 구조 이해 가능


--------------------------------------------------

# Commit Format

type(scope): subject

body


예시

기능(schedule): 스케줄 생성 API 구현

- Schedule Entity 생성
- ScheduleService 생성
- 스케줄 생성 API 추가


--------------------------------------------------

# Commit Type (한글)

기능      : 새로운 기능 추가
수정      : 버그 수정
리팩토링  : 코드 구조 개선
문서      : 문서 변경
테스트    : 테스트 코드
설정      : 환경 / 설정


예시

기능(auth): JWT 로그인 구현
수정(booking): 예약 중복 검증 버그 수정
리팩토링(common): 예외 처리 구조 개선
문서(docs): README 업데이트
설정(infra): Flyway 설정 추가


--------------------------------------------------

# Scope (도메인 기준)

auth          인증
member        회원
schedule      스케줄
booking       예약
membership    이용권
attendance    출석
payment       결제
notification  알림
common        공통
infra         인프라
docs          문서


예시

기능(member): 회원 조회 API 구현
기능(schedule): 수업 스케줄 생성 API 구현
기능(booking): 예약 생성 API 구현


--------------------------------------------------

# Commit 작성 규칙

1️⃣ Subject

- 50자 이내
- 명령형 사용
- 마침표 사용하지 않음

좋은 예

예약 생성 API 구현

나쁜 예

예약 생성 API를 구현했습니다.


--------------------------------------------------

2️⃣ Body

변경된 내용을 bullet 형태로 작성

- Booking Entity 생성
- 예약 생성 서비스 로직 추가
- 중복 예약 검증 로직 추가


--------------------------------------------------

# Commit 단위 규칙

기본 원칙

1 기능 = 1 커밋


예시

기능(schedule): 스케줄 생성 API 구현
기능(schedule): 스케줄 조회 API 구현
기능(schedule): 스케줄 목록 조회 API 구현


--------------------------------------------------

# Initial Commit 예시

기능(core): 백엔드 핵심 도메인 1차 구현

- Core 도메인 구조 설계
- 공통 인프라 구성
- JWT 인증 구조 구현
- Member / Schedule / Booking / Membership 도메인 구현
- 예약 및 이용권 검증 로직 추가
- API 버저닝 및 공통 응답 구조 적용


--------------------------------------------------

# 권장 Commit 흐름

기능(auth): JWT 인증 구조 구현
기능(member): 회원 도메인 구현
기능(schedule): 스케줄 도메인 구현
기능(booking): 예약 도메인 구현
기능(membership): 이용권 도메인 구현
기능(attendance): 출석 도메인 구현

이 방식은 Git 히스토리만 봐도 프로젝트 구조를 이해할 수 있다.