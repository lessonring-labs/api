package com.lessonring.api.payment.application;

import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.common.event.DomainEventPublisher;
import com.lessonring.api.membership.domain.MembershipType;
import com.lessonring.api.membership.domain.repository.MembershipRepository;
import com.lessonring.api.payment.api.request.PaymentApproveRequest;
import com.lessonring.api.payment.api.response.PaymentApproveResponse;
import com.lessonring.api.payment.domain.*;
import com.lessonring.api.payment.domain.repository.PaymentOperationRepository;
import com.lessonring.api.payment.domain.repository.PaymentRepository;
import com.lessonring.api.payment.infrastructure.lock.PaymentStateLockManager;
import com.lessonring.api.payment.infrastructure.pg.PgApproveResponse;
import com.lessonring.api.payment.infrastructure.pg.PgClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class PaymentPgServiceIntegrationTest {

    @Autowired
    private PaymentPgService paymentPgService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private PaymentOperationRepository paymentOperationRepository;

    @MockBean
    private PgClient pgClient;

    @MockBean
    private PaymentStateLockManager paymentStateLockManager;

    @MockBean
    private DomainEventPublisher domainEventPublisher;

    @BeforeEach
    void setUp() {
        Mockito.reset(pgClient, paymentStateLockManager, domainEventPublisher);
        Mockito.when(paymentStateLockManager.tryLock(any(Long.class))).thenReturn(true);
        paymentOperationRepository.deleteAll();
        paymentRepository.deleteAll();
        membershipRepository.deleteAll();
    }

    @Nested
    @DisplayName("approve 멱등성/상태 테스트")
    class Approve {

        @Test
        @DisplayName("정상 approve 성공")
        void approve_success() {
            Payment payment = saveReadyPayment();

            Mockito.when(pgClient.approve(any()))
                    .thenReturn(successPgResponse("paymentKey_123", "ORDER_123", 100_000L));

            PaymentApproveRequest request = approveRequest("paymentKey_123", "ORDER_123", 100_000L);

            PaymentApproveResponse response =
                    paymentPgService.approve(payment.getId(), request, "approve-key-1");

            Payment savedPayment = paymentRepository.findById(payment.getId()).orElseThrow();

            assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
            assertThat(savedPayment.getMembershipId()).isNotNull();
            assertThat(savedPayment.getPgPaymentKey()).isEqualTo("paymentKey_123");
            assertThat(savedPayment.getPgProvider()).isEqualTo("TOSS");
            assertThat(savedPayment.getPgOrderId()).isEqualTo("ORDER_123");

            assertThat(response.getPaymentId()).isEqualTo(payment.getId());
            assertThat(response.getStatus()).isEqualTo(PaymentStatus.COMPLETED.name());
            assertThat(response.getPaymentKey()).isEqualTo("paymentKey_123");
            assertThat(response.getAmount()).isEqualTo(100_000L);

            assertThat(membershipRepository.findAll()).hasSize(1);

            PaymentOperation operation = paymentOperationRepository
                    .findByPaymentIdAndOperationTypeAndIdempotencyKey(
                            payment.getId(),
                            PaymentOperationType.APPROVE,
                            "approve-key-1"
                    )
                    .orElseThrow();

            assertThat(operation.getStatus()).isEqualTo(PaymentOperationStatus.SUCCEEDED);
            assertThat(operation.getProviderReference()).isEqualTo("paymentKey_123");
            assertThat(operation.getResponsePayload()).isNotBlank();
            assertThat(operation.getErrorCode()).isNull();
            assertThat(operation.getErrorMessage()).isNull();

            verify(pgClient, times(1)).approve(any());
            verify(paymentStateLockManager, times(1)).tryLock(payment.getId());
        }

        @Test
        @DisplayName("동일 idempotencyKey + 동일 요청이면 기존 응답 재사용")
        void approve_idempotent_hit_returns_stored_response() {
            Payment payment = saveReadyPayment();

            Mockito.when(pgClient.approve(any()))
                    .thenReturn(successPgResponse("paymentKey_123", "ORDER_123", 100_000L));

            PaymentApproveRequest request = approveRequest("paymentKey_123", "ORDER_123", 100_000L);

            PaymentApproveResponse first =
                    paymentPgService.approve(payment.getId(), request, "approve-key-1");

            PaymentApproveResponse second =
                    paymentPgService.approve(payment.getId(), request, "approve-key-1");

            assertThat(second.getPaymentId()).isEqualTo(first.getPaymentId());
            assertThat(second.getStatus()).isEqualTo(first.getStatus());
            assertThat(second.getPaymentKey()).isEqualTo(first.getPaymentKey());
            assertThat(second.getAmount()).isEqualTo(first.getAmount());

            assertThat(membershipRepository.findAll()).hasSize(1);

            PaymentOperation operation = paymentOperationRepository
                    .findByPaymentIdAndOperationTypeAndIdempotencyKey(
                            payment.getId(),
                            PaymentOperationType.APPROVE,
                            "approve-key-1"
                    )
                    .orElseThrow();

            assertThat(operation.getStatus()).isEqualTo(PaymentOperationStatus.SUCCEEDED);
            verify(pgClient, times(1)).approve(any());
        }

        @Test
        @DisplayName("동일 idempotencyKey + 다른 요청이면 예외")
        void approve_same_idempotency_key_but_different_payload_throws() {
            Payment payment = saveReadyPayment();

            Mockito.when(pgClient.approve(any()))
                    .thenReturn(successPgResponse("paymentKey_123", "ORDER_123", 100_000L));

            PaymentApproveRequest firstRequest = approveRequest("paymentKey_123", "ORDER_123", 100_000L);
            PaymentApproveRequest secondRequest = approveRequest("paymentKey_999", "ORDER_123", 100_000L);

            paymentPgService.approve(payment.getId(), firstRequest, "approve-key-1");

            assertThatThrownBy(() ->
                    paymentPgService.approve(payment.getId(), secondRequest, "approve-key-1")
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST);
                    });

            verify(pgClient, times(1)).approve(any());
        }

        @Test
        @DisplayName("READY 상태가 아니면 승인 실패")
        void approve_fails_when_payment_is_not_ready() {
            Payment payment = saveReadyPayment();
            payment.fail("이미 실패 처리됨", "{\"code\":\"FAILED\"}");
            paymentRepository.saveAndFlush(payment);

            PaymentApproveRequest request = approveRequest("paymentKey_123", "ORDER_123", 100_000L);

            assertThatThrownBy(() ->
                    paymentPgService.approve(payment.getId(), request, "approve-key-1")
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST);
                    });

            PaymentOperation operation = paymentOperationRepository
                    .findByPaymentIdAndOperationTypeAndIdempotencyKey(
                            payment.getId(),
                            PaymentOperationType.APPROVE,
                            "approve-key-1"
                    )
                    .orElseThrow();

            assertThat(operation.getStatus()).isEqualTo(PaymentOperationStatus.FAILED);
            verify(pgClient, times(0)).approve(any());
        }

        @Test
        @DisplayName("PG 실패면 operation FAILED 기록")
        void approve_pg_failure_marks_operation_failed() {
            Payment payment = saveReadyPayment();

            Mockito.when(pgClient.approve(any()))
                    .thenReturn(failPgResponse("paymentKey_fail", "ORDER_FAIL", 100_000L));

            PaymentApproveRequest request = approveRequest("paymentKey_fail", "ORDER_FAIL", 100_000L);

            assertThatThrownBy(() ->
                    paymentPgService.approve(payment.getId(), request, "approve-key-1")
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST);
                    });

            PaymentOperation operation = paymentOperationRepository
                    .findByPaymentIdAndOperationTypeAndIdempotencyKey(
                            payment.getId(),
                            PaymentOperationType.APPROVE,
                            "approve-key-1"
                    )
                    .orElseThrow();

            assertThat(operation.getStatus()).isEqualTo(PaymentOperationStatus.FAILED);
            assertThat(operation.getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST.name());
            assertThat(operation.getErrorMessage()).isNotBlank();

            Payment savedPayment = paymentRepository.findById(payment.getId()).orElseThrow();
            assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.FAILED);
            assertThat(membershipRepository.findAll()).isEmpty();

            verify(pgClient, times(1)).approve(any());
        }

        @Test
        @DisplayName("락 획득 실패면 승인 실패")
        void approve_lock_fail_throws() {
            Payment payment = saveReadyPayment();

            Mockito.doReturn(false)
                    .when(paymentStateLockManager)
                    .tryLock(payment.getId());

            PaymentApproveRequest request = approveRequest("paymentKey_123", "ORDER_123", 100_000L);

            assertThatThrownBy(() ->
                    paymentPgService.approve(payment.getId(), request, "approve-key-1")
            )
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> {
                        BusinessException be = (BusinessException) ex;
                        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.PAYMENT_LOCK_ACQUISITION_FAILED);
                    });

            PaymentOperation operation = paymentOperationRepository
                    .findByPaymentIdAndOperationTypeAndIdempotencyKey(
                            payment.getId(),
                            PaymentOperationType.APPROVE,
                            "approve-key-1"
                    )
                    .orElseThrow();

            assertThat(operation.getStatus()).isEqualTo(PaymentOperationStatus.FAILED);
            verify(pgClient, times(0)).approve(any());
        }
    }

    private Payment saveReadyPayment() {
        Payment payment = Payment.create(
                1L,
                10L,
                "10회권 결제",
                PaymentMethod.CARD,
                100_000L,
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );
        return paymentRepository.save(payment);
    }

    private PaymentApproveRequest approveRequest(String paymentKey, String orderId, Long amount) {
        PaymentApproveRequest request = new PaymentApproveRequest();
        setField(request, "paymentKey", paymentKey);
        setField(request, "orderId", orderId);
        setField(request, "amount", amount);
        return request;
    }

    private PgApproveResponse successPgResponse(String paymentKey, String orderId, Long amount) {
        return PgApproveResponse.builder()
                .provider("TOSS")
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .success(true)
                .rawResponse("{\"status\":\"DONE\"}")
                .build();
    }

    private PgApproveResponse failPgResponse(String paymentKey, String orderId, Long amount) {
        return PgApproveResponse.builder()
                .provider("TOSS")
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .success(false)
                .failureReason("PG 승인 실패")
                .rawResponse("{\"code\":\"FAILED\"}")
                .build();
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Field findField(Class<?> type, String fieldName) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }
}
