package com.lessonring.api.payment.application;

import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.common.event.DomainEventPublisher;
import com.lessonring.api.membership.domain.Membership;
import com.lessonring.api.membership.domain.MembershipType;
import com.lessonring.api.membership.domain.repository.MembershipRepository;
import com.lessonring.api.payment.api.request.PaymentApproveRequest;
import com.lessonring.api.payment.api.response.PaymentApproveResponse;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.PaymentOperation;
import com.lessonring.api.payment.domain.PaymentOperationStatus;
import com.lessonring.api.payment.domain.PaymentOperationType;
import com.lessonring.api.payment.domain.PaymentMethod;
import com.lessonring.api.payment.domain.PaymentStatus;
import com.lessonring.api.payment.domain.repository.PaymentRepository;
import com.lessonring.api.payment.infrastructure.lock.PaymentStateLockManager;
import com.lessonring.api.payment.infrastructure.pg.PgApproveResponse;
import com.lessonring.api.payment.infrastructure.pg.PgClient;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PaymentPgServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private PgClient pgClient;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @Mock
    private PaymentOperationService paymentOperationService;

    @Mock
    private ApproveRequestHashGenerator approveRequestHashGenerator;

    @Mock
    private PaymentStateLockManager paymentStateLockManager;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private PaymentPgService paymentPgService;

    @BeforeEach
    void setUp() {
        given(approveRequestHashGenerator.generate(anyLong(), any(), any(), any()))
                .willReturn("approve-hash");
        given(paymentStateLockManager.tryLock(anyLong())).willReturn(true);
        given(paymentOperationService.startOrGet(anyLong(), any(PaymentOperationType.class), any(), any()))
                .willAnswer(invocation -> new PaymentOperationStartResult(
                        Mockito.mock(PaymentOperation.class),
                        PaymentOperationStatus.PROCESSING,
                        true
                ));
    }

    @Test
    @DisplayName("PG 승인 성공 시 결제가 완료되고 이용권이 생성된다")
    void approve_success() {
        // given
        Long studioId = 1L;
        Long memberId = 10L;

        Payment payment = Payment.create(
                studioId,
                memberId,
                "10회권 결제",
                PaymentMethod.CARD,
                100_000L,
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );
        setField(payment, "id", 1L);

        PaymentApproveRequest request = new PaymentApproveRequest();
        setField(request, "paymentKey", "paymentKey_123");
        setField(request, "orderId", "ORDER_123");
        setField(request, "amount", 100_000L);

        given(paymentRepository.findById(1L)).willReturn(java.util.Optional.of(payment));

        given(pgClient.approve(any())).willReturn(
                PgApproveResponse.builder()
                        .provider("TOSS")
                        .paymentKey("paymentKey_123")
                        .orderId("ORDER_123")
                        .amount(100_000L)
                        .success(true)
                        .rawResponse("{\"status\":\"DONE\"}")
                        .build()
        );

        Membership membership = Membership.create(
                studioId,
                memberId,
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );
        setField(membership, "id", 999L);

        given(membershipRepository.save(any(Membership.class))).willReturn(membership);

        // when
        PaymentApproveResponse response = paymentPgService.approve(1L, request);

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getMembershipId()).isEqualTo(999L);
        assertThat(payment.getPgPaymentKey()).isEqualTo("paymentKey_123");
        assertThat(payment.getPgProvider()).isEqualTo("TOSS");
        assertThat(payment.getPgOrderId()).isEqualTo("ORDER_123");

        assertThat(response.getPaymentId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        assertThat(response.getPaymentKey()).isEqualTo("paymentKey_123");
        assertThat(response.getAmount()).isEqualTo(100_000L);
    }

    @Test
    @DisplayName("PG 승인 실패 시 결제는 FAILED 상태가 된다")
    void approve_fail() {
        // given
        Long studioId = 1L;
        Long memberId = 10L;

        Payment payment = Payment.create(
                studioId,
                memberId,
                "10회권 결제",
                PaymentMethod.CARD,
                100_000L,
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );
        setField(payment, "id", 1L);

        PaymentApproveRequest request = new PaymentApproveRequest();
        setField(request, "paymentKey", "paymentKey_fail");
        setField(request, "orderId", "ORDER_FAIL");
        setField(request, "amount", 100_000L);

        given(paymentRepository.findById(1L)).willReturn(java.util.Optional.of(payment));

        given(pgClient.approve(any())).willReturn(
                PgApproveResponse.builder()
                        .provider("TOSS")
                        .paymentKey("paymentKey_fail")
                        .orderId("ORDER_FAIL")
                        .amount(100_000L)
                        .success(false)
                        .failureReason("PG 승인 실패")
                        .rawResponse("{\"code\":\"FAILED\"}")
                        .build()
        );

        // when & then
        assertThatThrownBy(() -> paymentPgService.approve(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("PG 승인에 실패했습니다.");

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(payment.getFailedReason()).contains("PG 승인 실패");
    }

    @Test
    @DisplayName("READY 상태가 아닌 결제는 승인할 수 없다")
    void approve_fail_when_payment_not_ready() {
        // given
        Long studioId = 1L;
        Long memberId = 10L;

        Payment payment = Payment.create(
                studioId,
                memberId,
                "10회권 결제",
                PaymentMethod.CARD,
                100_000L,
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );
        setField(payment, "id", 1L);
        payment.fail("이미 실패 처리됨", "{}");

        PaymentApproveRequest request = new PaymentApproveRequest();
        setField(request, "paymentKey", "paymentKey_123");
        setField(request, "orderId", "ORDER_123");
        setField(request, "amount", 100_000L);

        given(paymentRepository.findById(1L)).willReturn(java.util.Optional.of(payment));

        // when & then
        assertThatThrownBy(() -> paymentPgService.approve(1L, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST);
                });
    }

    @Test
    @DisplayName("이미 COMPLETED 상태인 결제는 재승인할 수 없다")
    void approve_fail_when_payment_already_completed() {
        // given
        Long studioId = 1L;
        Long memberId = 10L;

        Payment payment = Payment.create(
                studioId,
                memberId,
                "10회권 결제",
                PaymentMethod.CARD,
                100_000L,
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );
        setField(payment, "id", 1L);
        payment.complete(999L, "paymentKey_123", "{\"status\":\"DONE\"}");

        PaymentApproveRequest request = new PaymentApproveRequest();
        setField(request, "paymentKey", "paymentKey_123");
        setField(request, "orderId", "ORDER_123");
        setField(request, "amount", 100_000L);

        given(paymentRepository.findById(1L)).willReturn(java.util.Optional.of(payment));

        // when & then
        assertThatThrownBy(() -> paymentPgService.approve(1L, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.PAYMENT_APPROVE_ALREADY_COMPLETED);
                });
    }

    @Test
    @DisplayName("이미 FAILED 상태인 결제는 재승인할 수 없다")
    void approve_fail_when_payment_already_failed() {
        // given
        Long studioId = 1L;
        Long memberId = 10L;

        Payment payment = Payment.create(
                studioId,
                memberId,
                "10회권 결제",
                PaymentMethod.CARD,
                100_000L,
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );
        setField(payment, "id", 1L);
        payment.fail("이미 실패 처리됨", "{\"code\":\"FAILED\"}");

        PaymentApproveRequest request = new PaymentApproveRequest();
        setField(request, "paymentKey", "paymentKey_retry");
        setField(request, "orderId", "ORDER_RETRY");
        setField(request, "amount", 100_000L);

        given(paymentRepository.findById(1L)).willReturn(java.util.Optional.of(payment));

        // when & then
        assertThatThrownBy(() -> paymentPgService.approve(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("READY 상태의 결제만 승인할 수 있습니다.");
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
