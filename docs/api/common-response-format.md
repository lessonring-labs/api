# Common Response Format

## 개요

LessOnRing API는 모든 API 응답을 동일한 구조로 반환한다.

이 구조는 다음을 목적으로 한다.

- API 응답 일관성 유지
- 프론트엔드 처리 단순화
- 오류 처리 표준화

---

## 기본 응답 구조

성공 응답

{
"success": true,
"data": {}
}

실패 응답

{
"success": false,
"error": {
"code": "ERROR_CODE",
"message": "error message"
}
}

---

## 성공 응답 예시

{
"success": true,
"data": {
"memberId": 1001,
"name": "John"
}
}

---

## 에러 응답 예시

{
"success": false,
"error": {
"code": "MEMBER_NOT_FOUND",
"message": "Member does not exist"
}
}

---

## Response Wrapper

모든 API는 아래 Response Wrapper를 사용한다.

ApiResponse<T>

구조

ApiResponse
├ success
├ data
└ error

---

## 목적

- API 응답 표준화
- 프론트엔드 연동 단순화
- 오류 처리 일관성 확보