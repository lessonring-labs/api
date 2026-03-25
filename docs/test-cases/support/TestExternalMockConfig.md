# 테스트 외부 연동 목 설정 문서

## 1. 대상 정보

- 파일: [TestExternalMockConfig.java](/C:/wms/api/src/test/java/com/lessonring/api/support/TestExternalMockConfig.java)
- 역할: 테스트 지원 설정

## 2. 문서 목적

이 파일은 테스트 케이스 자체가 아니라, 외부 PG 연동을 테스트 환경에서 안전하게 대체하기 위한 지원 구성의 역할을 설명한다.

## 3. 주요 기능

- `PgClient`를 Mockito mock bean으로 등록
- 결제 승인/환불/webhook 통합 테스트에서 외부 네트워크 의존성 제거
- 테스트 시나리오별로 PG 응답 성공/실패를 자유롭게 주입 가능하게 지원

## 4. 적용 대상 테스트

- 결제 승인 통합 테스트
- 환불 통합 테스트
- 결제 동시성 테스트
- webhook 관련 테스트

## 5. 운영상 의미

외부 PG가 불안정하거나 네트워크가 차단된 환경에서도 핵심 결제 테스트를 반복 가능하게 만드는 기반 설정이다.
