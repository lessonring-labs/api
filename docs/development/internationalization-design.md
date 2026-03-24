# 다국어 설계안

이 문서는 현재 LessonRing Backend 프로젝트에 다국어 기능을 도입할 때의 권장 구조를 정리한 설계안이다.

목표:

- 현재 코드 구조를 크게 깨지 않고 다국어를 도입한다.
- 시스템 문구와 도메인 데이터를 구분해서 관리한다.
- 확장 가능한 구조를 유지하되, 초기 도입 비용은 낮춘다.

---

# 기본 방향

다국어는 한 가지 방식으로 모두 처리하지 않는다.

현재 프로젝트에서는 번역 대상을 다음 세 가지로 나누는 것이 효율적이다.

1. 시스템 문구
2. enum 표시 문구
3. 번역이 필요한 도메인 데이터

이 세 가지를 구분하지 않으면 다음 문제가 생긴다.

- enum에 UI 문구가 과하게 들어감
- DB 테이블에 번역용 컬럼이 불필요하게 늘어남
- 에러 메시지와 도메인 데이터가 같은 방식으로 얽힘

---

# 1. 시스템 문구

대상:

- 예외 메시지
- validation 메시지
- 공통 응답 메시지
- 서버에서 생성하는 기본 알림 문구

권장 방식:

- Spring `MessageSource`
- `messages.properties`
- `messages_ko.properties`
- `messages_en.properties`

이 방식을 가장 먼저 도입하는 것이 좋다.

이유:

- Spring Boot와 가장 잘 맞는다.
- 예외/검증 문구에 바로 적용 가능하다.
- 초기 도입 비용이 낮다.

예시 키:

```text
error.payment.not-found=결제 정보를 찾을 수 없습니다.
error.payment.already-canceled=이미 취소된 결제입니다.
notification.booking.confirmed=예약이 완료되었습니다.
payment.status.COMPLETED=결제 완료
```

---

# 2. Locale 결정 방식

권장 우선순위:

1. `Accept-Language` 헤더
2. 로그인 사용자 설정 locale
3. 시스템 기본값

현재 프로젝트 기준 추천:

- 초기 단계: `Accept-Language` 기반
- 사용자 설정이 필요해지면 `member`에 locale 저장 컬럼 추가 검토

추천 기본값:

- `ko`

이유:

- 현재 에러 메시지와 서비스 대상 언어가 한국어 중심이다.

---

# 3. enum 처리 방식

현재 프로젝트에는 다음과 같은 enum이 많다.

- `PaymentStatus`
- `BookingStatus`
- `MembershipStatus`
- `MembershipType`
- `ScheduleStatus`
- `AttendanceStatus`
- `MemberStatus`
- `InstructorStatus`
- `StudioStatus`
- `Gender`
- `ErrorCode`

권장 방식:

- enum은 내부 코드 역할만 유지한다.
- 표시 문구는 `MessageSource` 키로 분리한다.

예:

```text
enum.payment-status.READY=결제 대기
enum.payment-status.COMPLETED=결제 완료
enum.payment-status.CANCELED=결제 취소
enum.payment-status.FAILED=결제 실패
```

권장 이유:

- enum이 UI 문구 책임을 지지 않음
- 다국어 추가가 쉬움
- 문구 변경 시 코드 수정 범위를 줄일 수 있음

비권장 방식:

- 모든 enum에 `description`을 직접 넣는 방식

이유:

- 문구 변경에 취약함
- 다국어 확장 시 enum이 과도하게 커짐
- 번역 텍스트와 도메인 코드가 강하게 결합됨

---

# 4. 에러 메시지 구조

현재 프로젝트의 `ErrorCode`는 다음 정보를 가진다.

- HTTP status
- code
- message

다국어 도입 시 권장 방향:

- `code`는 유지
- `message`는 최종 표시 문구가 아니라 기본 fallback 또는 메시지 키 역할로 전환

추천 구조 예시:

```text
PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "error.payment.not-found")
```

또는

```text
PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "결제 정보를 찾을 수 없습니다.")
```

그리고 응답 생성 시:

- locale 기준 메시지 조회 성공: 번역 메시지 사용
- 조회 실패: enum 내 기본 메시지 fallback 사용

이 구조가 가장 현실적이다.

---

# 5. 알림(Notification) 다국어 전략

현재 `notification` 엔티티는 다음 필드를 가진다.

- `title`
- `content`
- `type`

다국어 도입 시 선택지는 두 가지다.

## 방식 A. 완성 문구 저장

예:

- `title = "예약 완료"`
- `content = "2026-03-24 19:00 수업 예약이 완료되었습니다."`

장점:

- 구현이 단순하다.
- 발송 시점 문구가 그대로 남는다.

단점:

- 사용자 언어 변경 시 기존 알림 번역 불가
- 다국어 확장성이 낮다.

## 방식 B. 메시지 키 + 파라미터 저장

예:

- `message_key = notification.booking.confirmed`
- `message_args = {"scheduleName":"...", "startAt":"..."}`

장점:

- locale에 따라 동적으로 번역 가능
- 문구 수정이 쉬움
- 다국어 확장성이 높음

단점:

- 구조 변경 필요
- 렌더링 레이어 추가 필요

현재 프로젝트 추천:

- 초기에는 방식 A 유지
- 알림 다국어 요구가 실제로 생기면 방식 B로 확장

---

# 6. 도메인 데이터 다국어 전략

모든 테이블을 다국어화하면 비용이 너무 크다.

대상은 다음처럼 구분해야 한다.

## 번역이 필요 없는 데이터

- 회원 이름
- 메모
- 결제 주문명
- 사용자 입력 텍스트

이 값들은 원문 저장이 맞다.

## 번역이 필요한 마스터 데이터

- 수업 유형 표시명
- 알림 유형 표시명
- 공통 코드명

이런 항목은 번역 테이블로 관리하는 것이 좋다.

예시:

```text
membership_type
membership_type_translation
```

또는

```text
code_dictionary
code_dictionary_translation
```

추천 패턴:

- 원본 테이블: 코드 중심
- 번역 테이블: `code + locale + label + description`

---

# 7. API 응답 전략

현재 응답 DTO는 enum 자체를 그대로 내보내거나 `name()`을 사용한다.

예:

- `PaymentResponse.status`
- `BookingResponse.status`
- `MemberResponse.gender`

권장 방향:

초기 단계:

- 내부 코드값은 그대로 유지
- 프론트에서 번역

예:

```json
{
  "status": "COMPLETED"
}
```

확장 단계:

- 필요하면 code + label 동시 제공

예:

```json
{
  "status": "COMPLETED",
  "statusLabel": "결제 완료"
}
```

현재 프로젝트 추천:

- 기본은 코드값 유지
- 관리 화면이나 앱에서 즉시 표시 문구가 필요할 때만 label 추가 검토

---

# 8. 현재 프로젝트에 맞는 도입 순서

가장 현실적인 순서는 다음과 같다.

1. `MessageSource` 도입
2. 예외 메시지와 validation 메시지 다국어화
3. enum 표시 문구를 메시지 키 방식으로 정리
4. `Accept-Language` 기반 locale 처리 추가
5. 필요 시 사용자 locale 저장
6. 알림 다국어 전략 결정
7. 번역이 필요한 마스터 데이터에만 translation 테이블 도입

---

# 9. 현재 프로젝트 기준 권장 범위

우선 적용 추천:

- `ErrorCode`
- validation 메시지
- `PaymentStatus`, `BookingStatus`, `MembershipStatus` 같은 상태 enum의 표시 문구
- Swagger/OpenAPI 설명 일부

나중에 검토:

- `Notification`
- 마스터 데이터 번역 테이블
- 사용자별 locale 저장

보류 가능:

- 모든 엔티티 텍스트 컬럼의 다국어화
- 사용자 입력 데이터 번역 저장

---

# 10. 권장 설계 원칙 요약

- 시스템 문구는 `MessageSource`로 관리한다.
- enum은 코드만 유지하고 표시 문구는 분리한다.
- 도메인 데이터는 정말 필요한 경우에만 translation 테이블을 도입한다.
- 알림은 초기에는 완성 문구 저장, 필요 시 메시지 키 방식으로 확장한다.
- API 응답은 코드 중심으로 유지하고, 표시 문구는 선택적으로 추가한다.

---

# 결론

현재 프로젝트에서 가장 효율적인 다국어 도입 방식은 다음과 같다.

- 1단계: `MessageSource` 기반으로 에러/검증/기본 문구를 다국어화
- 2단계: enum 표시 문구를 메시지 키로 분리
- 3단계: 필요한 마스터 데이터에만 translation 구조 도입

즉, 처음부터 모든 데이터를 다국어화하지 말고, "시스템 문구 -> enum 표시 문구 -> 일부 도메인 데이터" 순서로 확장하는 방식이 가장 안전하고 유지보수하기 쉽다.
