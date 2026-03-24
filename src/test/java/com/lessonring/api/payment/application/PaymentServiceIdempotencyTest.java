package com.lessonring.api.payment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.lessonring.api.member.domain.Member;
import com.lessonring.api.member.domain.repository.MemberRepository;
import com.lessonring.api.membership.domain.MembershipType;
import com.lessonring.api.payment.api.request.PaymentCreateRequest;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.PaymentMethod;
import com.lessonring.api.payment.domain.repository.PaymentRepository;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceIdempotencyTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private com.lessonring.api.membership.domain.repository.MembershipRepository membershipRepository;

    @Mock
    private com.lessonring.api.booking.domain.repository.BookingRepository bookingRepository;

    @Mock
    private com.lessonring.api.common.event.DomainEventPublisher domainEventPublisher;

    @Mock
    private com.lessonring.api.payment.infrastructure.pg.PgClient pgClient;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("같은 idempotencyKey로 결제 생성 요청이 들어오면 기존 Payment를 반환한다")
    void create_should_return_existing_payment_when_idempotency_key_exists() {
        // given
        Long memberId = 10L;
        String idempotencyKey = "PAYMENT_CREATE_MEMBER_10_001";

        Member member = org.mockito.Mockito.mock(Member.class);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        Payment existingPayment = Payment.create(
                1L,
                memberId,
                "10회권 결제",
                PaymentMethod.CARD,
                100_000L,
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                idempotencyKey
        );
        setField(existingPayment, "id", 1L);

        PaymentCreateRequest request = new PaymentCreateRequest();
        setField(request, "studioId", 1L);
        setField(request, "memberId", memberId);
        setField(request, "orderName", "10회권 결제");
        setField(request, "paymentMethod", PaymentMethod.CARD);
        setField(request, "amount", 100_000L);
        setField(request, "membershipName", "10회권");
        setField(request, "membershipType", MembershipType.COUNT);
        setField(request, "membershipTotalCount", 10);
        setField(request, "membershipStartDate", LocalDate.now());
        setField(request, "membershipEndDate", LocalDate.now().plusDays(30));
        setField(request, "idempotencyKey", idempotencyKey);

        given(paymentRepository.findByIdempotencyKey(idempotencyKey))
                .willReturn(Optional.of(existingPayment));

        // when
        Payment result = paymentService.create(request);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getIdempotencyKey()).isEqualTo(idempotencyKey);
    }

    @Test
    @DisplayName("idempotencyKey가 없으면 새로운 Payment를 생성한다")
    void create_should_create_new_payment_when_idempotency_key_is_absent() {
        // given
        Long memberId = 10L;

        Member member = org.mockito.Mockito.mock(Member.class);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        PaymentCreateRequest request = new PaymentCreateRequest();
        setField(request, "studioId", 1L);
        setField(request, "memberId", memberId);
        setField(request, "orderName", "10회권 결제");
        setField(request, "paymentMethod", PaymentMethod.CARD);
        setField(request, "amount", 100_000L);
        setField(request, "membershipName", "10회권");
        setField(request, "membershipType", MembershipType.COUNT);
        setField(request, "membershipTotalCount", 10);
        setField(request, "membershipStartDate", LocalDate.now());
        setField(request, "membershipEndDate", LocalDate.now().plusDays(30));
        setField(request, "idempotencyKey", null);

        Payment newPayment = Payment.create(
                1L,
                memberId,
                "10회권 결제",
                PaymentMethod.CARD,
                100_000L,
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                null
        );
        setField(newPayment, "id", 2L);

        given(paymentRepository.save(org.mockito.ArgumentMatchers.any(Payment.class)))
                .willReturn(newPayment);

        // when
        Payment result = paymentService.create(request);

        // then
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getIdempotencyKey()).isNull();
    }

    @Test
    @DisplayName("새로운 idempotencyKey이면 새로운 Payment를 생성한다")
    void create_should_create_new_payment_when_idempotency_key_not_found() {
        // given
        Long memberId = 10L;
        String idempotencyKey = "PAYMENT_CREATE_MEMBER_10_002";

        Member member = org.mockito.Mockito.mock(Member.class);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        PaymentCreateRequest request = new PaymentCreateRequest();
        setField(request, "studioId", 1L);
        setField(request, "memberId", memberId);
        setField(request, "orderName", "10회권 결제");
        setField(request, "paymentMethod", PaymentMethod.CARD);
        setField(request, "amount", 100_000L);
        setField(request, "membershipName", "10회권");
        setField(request, "membershipType", MembershipType.COUNT);
        setField(request, "membershipTotalCount", 10);
        setField(request, "membershipStartDate", LocalDate.now());
        setField(request, "membershipEndDate", LocalDate.now().plusDays(30));
        setField(request, "idempotencyKey", idempotencyKey);

        Payment newPayment = Payment.create(
                1L,
                memberId,
                "10회권 결제",
                PaymentMethod.CARD,
                100_000L,
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                idempotencyKey
        );
        setField(newPayment, "id", 3L);

        given(paymentRepository.findByIdempotencyKey(idempotencyKey))
                .willReturn(Optional.empty());
        given(paymentRepository.save(org.mockito.ArgumentMatchers.any(Payment.class)))
                .willReturn(newPayment);

        // when
        Payment result = paymentService.create(request);

        // then
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getIdempotencyKey()).isEqualTo(idempotencyKey);
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