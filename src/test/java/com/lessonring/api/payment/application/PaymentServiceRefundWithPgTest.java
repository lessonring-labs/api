package com.lessonring.api.payment.application;

import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.event.DomainEventPublisher;
import com.lessonring.api.membership.domain.Membership;
import com.lessonring.api.membership.domain.MembershipType;
import com.lessonring.api.membership.domain.repository.MembershipRepository;
import com.lessonring.api.payment.api.response.RefundResponse;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.PaymentMethod;
import com.lessonring.api.payment.domain.PaymentStatus;
import com.lessonring.api.payment.domain.repository.PaymentRepository;
import com.lessonring.api.payment.infrastructure.pg.PgCancelResponse;
import com.lessonring.api.payment.infrastructure.pg.PgClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PaymentServiceRefundWithPgTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private com.lessonring.api.booking.domain.repository.BookingRepository bookingRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @Mock
    private PgClient pgClient;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("환불 시 PG 취소가 성공하면 내부 환불이 완료된다")
    void refund_with_pg_cancel_success() {
        Payment payment = createCompletedPayment();
        Membership membership = createMembership();

        given(paymentRepository.findById(1L)).willReturn(Optional.of(payment));
        given(membershipRepository.findById(100L)).willReturn(Optional.of(membership));
        given(bookingRepository.findRefundTargetBookings(any(), any())).willReturn(java.util.List.of());
        given(pgClient.cancel(any())).willReturn(
                PgCancelResponse.builder()
                        .provider("TOSS")
                        .paymentKey("paymentKey_123")
                        .success(true)
                        .rawResponse("{\"status\":\"CANCELED\"}")
                        .build()
        );

        RefundResponse response = paymentService.refund(1L);

        assertThat(response.getRefundAmount()).isGreaterThan(0L);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
    }

    @Test
    @DisplayName("환불 시 PG 취소가 실패하면 예외가 발생한다")
    void refund_with_pg_cancel_fail() {
        Payment payment = createCompletedPayment();
        Membership membership = createMembership();

        given(paymentRepository.findById(1L)).willReturn(Optional.of(payment));
        given(membershipRepository.findById(100L)).willReturn(Optional.of(membership));
        given(pgClient.cancel(any())).willReturn(
                PgCancelResponse.builder()
                        .provider("TOSS")
                        .paymentKey("paymentKey_123")
                        .success(false)
                        .failureReason("PG 오류")
                        .rawResponse("{}")
                        .build()
        );

        assertThatThrownBy(() -> paymentService.refund(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("PG 결제 취소에 실패했습니다.");
    }

    private Payment createCompletedPayment() {
        Payment payment = Payment.create(
                1L,
                10L,
                "10회권 결제",
                PaymentMethod.CARD,
                100_000L,
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(30)
        );
        setField(payment, "id", 1L);
        payment.complete(100L, "paymentKey_123", "{\"status\":\"DONE\"}");
        return payment;
    }

    private Membership createMembership() {
        Membership membership = Membership.create(
                1L,
                10L,
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(30)
        );
        setField(membership, "id", 100L);
        return membership;
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