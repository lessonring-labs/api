# Payment 엔티티 리팩토링 설계안

이 문서는 `Payment` 엔티티의 필드 선언은 유지하면서, 아래 메서드 영역이 과도하게 복잡해지는 문제를 어떻게 풀지에 대한 설계 방향을 정리한다.

목표:

- 엔티티의 역할을 유지한다.
- 상태 전이 로직은 남기되, 메서드 복잡도를 줄인다.
- PG 연동, 환불 정책, 멱등 처리 같은 주변 책임을 적절한 계층으로 분리한다.

---

# 현재 문제 정의

`Payment` 엔티티는 보통 다음 성격의 로직을 함께 가지고 있을 가능성이 높다.

- 결제 승인 처리
- 결제 실패 처리
- 결제 취소 처리
- 상태 전이 검증
- PG 응답값 반영
- 이용권 스냅샷 값 관리
- 예외 메시지 및 방어 로직

이 로직이 한두 개의 큰 메서드에 몰리면 다음 문제가 생긴다.

- 메서드 길이가 길어짐
- 조건문이 중첩됨
- 상태 전이 규칙이 눈에 잘 안 들어옴
- 테스트가 시나리오별로 분리되지 않음
- 엔티티가 외부 시스템 정보까지 과하게 알게 됨

---

# 리팩토링 방향

핵심 원칙은 다음과 같다.

- 엔티티에는 상태와 핵심 불변식만 남긴다.
- 상태 가능 여부 판단은 enum 또는 private 검증 메서드로 이동한다.
- 여러 엔티티나 외부 시스템이 얽힌 정책은 도메인 서비스로 이동한다.
- 파라미터가 많은 메서드는 명시적인 입력 객체로 감싼다.

---

# 추천 구조

## 1. 메서드를 시나리오 단위로 분리

현재 큰 메서드가 있다면 아래처럼 시나리오 중심으로 나눈다.

- `markApproved(...)`
- `markFailed(...)`
- `markCanceled(...)`
- `recordPgMetadata(...)`
- `snapshotMembership(...)`

이름 기준:

- 상태 변경은 `mark...`
- 값 반영은 `record...`, `apply...`, `snapshot...`
- 검증은 `validate...`, `ensure...`

---

## 2. 검증과 상태 변경을 분리

엔티티 메서드 내부에서 가장 먼저 줄여야 하는 것은 긴 조건문이다.

예시 방향:

```java
public void markApproved(PgApprovalResult result) {
    validateApprovable(result);
    applyApproval(result);
}
```

```java
private void validateApprovable(PgApprovalResult result) {
    ensureNotCanceled();
    ensureAmountMatches(result.amount());
    ensureStatusCanBeApproved();
}
```

```java
private void applyApproval(PgApprovalResult result) {
    this.status = PaymentStatus.PAID;
    this.paidAt = result.paidAt();
    this.pgProvider = result.pgProvider();
    this.pgOrderId = result.pgOrderId();
    this.pgPaymentKey = result.pgPaymentKey();
    this.pgRawResponse = result.rawResponse();
}
```

이렇게 나누면 public 메서드는 시나리오를 설명하고, private 메서드는 세부 구현을 숨긴다.

---

## 3. 상태 가능 여부는 `PaymentStatus`로 이동

복잡한 `if` 문이 상태 중심이라면 enum으로 옮기는 것이 좋다.

예시:

- `status.canApprove()`
- `status.canCancel()`
- `status.canFail()`

이후 엔티티는 다음처럼 단순해진다.

```java
private void ensureStatusCanBeCanceled() {
    if (!status.canCancel()) {
        throw new IllegalStateException("취소할 수 없는 결제 상태입니다.");
    }
}
```

효과:

- 상태 전이 규칙이 enum 한 곳에 모임
- 엔티티 본문이 짧아짐
- 테스트 포인트가 명확해짐

---

## 4. PG 관련 값은 입력 객체로 묶기

아래 성격의 값이 여러 메서드에 반복 전달되면 객체로 묶는 것이 좋다.

- `pgProvider`
- `pgOrderId`
- `pgPaymentKey`
- `pgRawResponse`
- `paidAt`
- `amount`

후보 객체:

- `PgApprovalResult`
- `PgCancelResult`
- `PaymentFailureInfo`

효과:

- 메서드 시그니처가 짧아짐
- 필드 의미가 분명해짐
- 외부 연동 결과를 엔티티가 한 번에 받기 쉬워짐

---

## 5. 이용권 스냅샷도 묶기

`Payment`에는 이용권 생성 시점 스냅샷 값이 포함되어 있다.

- `membershipName`
- `membershipType`
- `membershipTotalCount`
- `membershipStartDate`
- `membershipEndDate`

이 묶음은 값 객체 또는 입력 객체로 다룰 수 있다.

후보:

- `MembershipSnapshot`
- `PaymentMembershipSnapshot`

효과:

- 생성/초기화 로직이 짧아짐
- 결제 엔티티가 들고 있는 스냅샷 의미가 명확해짐

---

## 6. 엔티티 밖으로 빼야 하는 책임

아래 성격은 `Payment` 엔티티에 남기지 않는 것이 좋다.

- 환불 시 예약/이용권 연쇄 처리 정책
- 멱등 처리 판단
- PG 응답 성공/실패 해석
- webhook 중복 처리
- 여러 엔티티를 동시에 바꾸는 트랜잭션 흐름

이런 로직은 다음 계층으로 이동한다.

- `PaymentService`
- `PaymentPgService`
- `PaymentWebhookService`
- 별도 도메인 서비스

기준:

- `Payment` 하나만 보고 판단할 수 없는 로직은 엔티티 밖으로 뺀다.

---

# 추천 리팩토링 순서

1. `Payment` public 메서드를 시나리오 단위로 쪼갠다.
2. 긴 조건문을 `validate...` 또는 `ensure...` private 메서드로 추출한다.
3. 상태 가능 여부 로직을 `PaymentStatus`로 이동한다.
4. PG 승인/취소/실패 입력을 별도 객체로 묶는다.
5. 이용권 스냅샷 입력도 별도 객체로 묶는다.
6. 엔티티 밖이 더 적절한 정책 로직을 서비스로 이동한다.

---

# 리팩토링 후 기대 형태

리팩토링 이후 `Payment` 엔티티는 다음 느낌이 되는 것이 좋다.

- 필드 선언
- 생성 팩토리
- `markApproved(...)`
- `markCanceled(...)`
- `markFailed(...)`
- `validate...`, `ensure...`
- `apply...`

즉, 엔티티를 읽었을 때 결제의 상태 변화 흐름이 바로 보이되, 세부 정책과 외부 연동 세부사항은 과도하게 보이지 않는 구조가 이상적이다.

---

# 적용 판단 기준

아래 항목이 보이면 리팩토링 대상이다.

- public 메서드가 15줄을 넘는다.
- 한 메서드 안에서 `if`가 3개 이상 반복된다.
- 파라미터가 5개 이상이다.
- 한 메서드가 검증, 상태 변경, 외부 응답 반영을 동시에 한다.
- 메서드를 읽을 때 "무엇을 하는지"보다 "어떻게 하는지"가 먼저 보인다.

---

# 결론

`Payment` 엔티티는 단순 CRUD 엔티티가 아니라 도메인 규칙이 많은 엔티티이므로 메서드가 어느 정도 있는 것은 자연스럽다.

문제는 메서드 수가 아니라 한 메서드에 너무 많은 책임이 들어 있는 것이다.

따라서 개선 방향은 필드를 줄이는 것이 아니라:

- 메서드를 시나리오 단위로 쪼개고
- 상태 전이 규칙을 enum으로 옮기고
- 입력 묶음을 값 객체로 만들고
- 정책과 오케스트레이션을 서비스로 분리하는 것

에 있다.
