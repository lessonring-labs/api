package com.lessonring.api.payment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.lessonring.api.payment.api.request.PaymentWebhookRequest;
import com.lessonring.api.membership.domain.Membership;
import com.lessonring.api.membership.domain.MembershipType;
import com.lessonring.api.membership.domain.repository.MembershipRepository;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.PaymentMethod;
import com.lessonring.api.payment.domain.PaymentStatus;
import com.lessonring.api.payment.domain.repository.PaymentRepository;
import com.lessonring.api.payment.domain.repository.PaymentWebhookLogRepository;
import com.lessonring.api.payment.infrastructure.pg.PgPaymentStatusResponse;
import com.lessonring.api.support.TestExternalMockConfig;
import java.lang.reflect.Field;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestExternalMockConfig.class)
class PaymentWebhookReplayTest {

    @Autowired
    private PaymentWebhookService paymentWebhookService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private PaymentWebhookLogRepository paymentWebhookLogRepository;

    @Autowired
    @MockBean
    private PaymentWebhookPgVerificationService paymentWebhookPgVerificationService;

    @BeforeEach
    void setUp() {
        paymentWebhookLogRepository.deleteAll();
        membershipRepository.deleteAll();
        paymentRepository.deleteAll();
        Mockito.reset(paymentWebhookPgVerificationService);
    }

    @AfterEach
    void tearDown() {
        paymentWebhookLogRepository.deleteAll();
        membershipRepository.deleteAll();
        paymentRepository.deleteAll();
        Mockito.reset(paymentWebhookPgVerificationService);
    }

    @Test
    @DisplayName("동일 transmissionId 재전송이면 두 번째 요청은 무시된다")
    void duplicated_transmission_id_is_ignored() {
        Payment payment = saveReadyPayment("ORDER_REPLAY_1");

        PaymentWebhookRequest request = webhookRequest(
                "PAYMENT_COMPLETED",
                "ORDER_REPLAY_1",
                "paymentKey_replay_1",
                null,
                "{\"status\":\"DONE\"}"
        );

        Mockito.when(paymentWebhookPgVerificationService.verifyCompleted(any(), any()))
                .thenReturn(
                        PgPaymentStatusResponse.builder()
                                .paymentKey("paymentKey_replay_1")
                                .orderId("ORDER_REPLAY_1")
                                .totalAmount(100_000L)
                                .status("DONE")
                                .rawResponse("{\"status\":\"DONE\"}")
                                .build()
                );

        paymentWebhookService.handle(
                "tx-replay-1",
                "dummy-signature",
                "{\"eventType\":\"PAYMENT_COMPLETED\"}",
                request
        );

        paymentWebhookService.handle(
                "tx-replay-1",
                "dummy-signature",
                "{\"eventType\":\"PAYMENT_COMPLETED\"}",
                request
        );

        Payment savedPayment = paymentRepository.findById(payment.getId()).orElseThrow();

        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(savedPayment.getPgPaymentKey()).isEqualTo("paymentKey_replay_1");

        assertThat(paymentWebhookLogRepository.findAll()).hasSize(1);
        assertThat(
                paymentWebhookLogRepository.findByProviderAndTransmissionId("TOSS", "tx-replay-1")
        ).isPresent();

        verify(paymentWebhookPgVerificationService, times(1))
                .verifyCompleted(any(), any());
    }

    @Test
    @DisplayName("transmissionId 없이 동일 completed webhook 재전송되면 상태는 한 번만 반영되고 두 번째는 skip 된다")
    void replay_without_transmission_id_completed_is_skipped_by_status() {
        Payment payment = saveReadyPayment("ORDER_REPLAY_2");

        PaymentWebhookRequest request = webhookRequest(
                "PAYMENT_COMPLETED",
                "ORDER_REPLAY_2",
                "paymentKey_replay_2",
                null,
                "{\"status\":\"DONE\"}"
        );

        Mockito.when(paymentWebhookPgVerificationService.verifyCompleted(any(), any()))
                .thenReturn(
                        PgPaymentStatusResponse.builder()
                                .paymentKey("paymentKey_replay_2")
                                .orderId("ORDER_REPLAY_2")
                                .totalAmount(100_000L)
                                .status("DONE")
                                .rawResponse("{\"status\":\"DONE\"}")
                                .build()
                );

        paymentWebhookService.handle(
                null,
                "dummy-signature",
                "{\"eventType\":\"PAYMENT_COMPLETED\"}",
                request
        );

        paymentWebhookService.handle(
                null,
                "dummy-signature",
                "{\"eventType\":\"PAYMENT_COMPLETED\"}",
                request
        );

        Payment savedPayment = paymentRepository.findById(payment.getId()).orElseThrow();

        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(savedPayment.getPgPaymentKey()).isEqualTo("paymentKey_replay_2");

        assertThat(paymentWebhookLogRepository.findAll()).hasSize(2);

        verify(paymentWebhookPgVerificationService, times(1))
                .verifyCompleted(any(), any());
    }

    @Test
    @DisplayName("transmissionId 없이 동일 canceled webhook 재전송되면 상태는 한 번만 반영되고 두 번째는 skip 된다")
    void replay_without_transmission_id_canceled_is_skipped_by_status() {
        Payment payment = saveCompletedPayment("ORDER_REPLAY_3", "paymentKey_replay_3");

        PaymentWebhookRequest request = webhookRequest(
                "PAYMENT_CANCELED",
                "ORDER_REPLAY_3",
                "paymentKey_replay_3",
                null,
                "{\"status\":\"CANCELED\"}"
        );

        Mockito.when(paymentWebhookPgVerificationService.verifyCanceled(any(), any()))
                .thenReturn(
                        PgPaymentStatusResponse.builder()
                                .paymentKey("paymentKey_replay_3")
                                .orderId("ORDER_REPLAY_3")
                                .totalAmount(100_000L)
                                .status("CANCELED")
                                .rawResponse("{\"status\":\"CANCELED\"}")
                                .build()
                );

        paymentWebhookService.handle(
                null,
                "dummy-signature",
                "{\"eventType\":\"PAYMENT_CANCELED\"}",
                request
        );

        paymentWebhookService.handle(
                null,
                "dummy-signature",
                "{\"eventType\":\"PAYMENT_CANCELED\"}",
                request
        );

        Payment savedPayment = paymentRepository.findById(payment.getId()).orElseThrow();

        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
        assertThat(paymentWebhookLogRepository.findAll()).hasSize(2);

        verify(paymentWebhookPgVerificationService, times(1))
                .verifyCanceled(any(), any());
    }

    @Test
    @DisplayName("동일 transmissionId 이지만 payload가 달라도 현재 정책상 두 번째 요청은 무시된다")
    void same_transmission_id_with_different_payload_is_ignored_by_current_policy() {
        Payment payment = saveReadyPayment("ORDER_REPLAY_4");

        PaymentWebhookRequest firstRequest = webhookRequest(
                "PAYMENT_COMPLETED",
                "ORDER_REPLAY_4",
                "paymentKey_replay_4",
                null,
                "{\"status\":\"DONE\"}"
        );

        PaymentWebhookRequest secondRequest = webhookRequest(
                "PAYMENT_FAILED",
                "ORDER_REPLAY_4",
                "paymentKey_replay_4",
                "PG 승인 실패",
                "{\"status\":\"FAILED\"}"
        );

        Mockito.when(paymentWebhookPgVerificationService.verifyCompleted(any(), any()))
                .thenReturn(
                        PgPaymentStatusResponse.builder()
                                .paymentKey("paymentKey_replay_4")
                                .orderId("ORDER_REPLAY_4")
                                .totalAmount(100_000L)
                                .status("DONE")
                                .rawResponse("{\"status\":\"DONE\"}")
                                .build()
                );

        paymentWebhookService.handle(
                "tx-replay-4",
                "dummy-signature",
                "{\"eventType\":\"PAYMENT_COMPLETED\"}",
                firstRequest
        );

        paymentWebhookService.handle(
                "tx-replay-4",
                "dummy-signature",
                "{\"eventType\":\"PAYMENT_FAILED\"}",
                secondRequest
        );

        Payment savedPayment = paymentRepository.findById(payment.getId()).orElseThrow();

        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(paymentWebhookLogRepository.findAll()).hasSize(1);

        verify(paymentWebhookPgVerificationService, times(1))
                .verifyCompleted(any(), any());
        verify(paymentWebhookPgVerificationService, times(0))
                .verifyFailed(any(), any());
    }

    private Payment saveReadyPayment(String orderId) {
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
        setField(payment, "pgOrderId", orderId);
        return paymentRepository.save(payment);
    }

    private Payment saveCompletedPayment(String orderId, String paymentKey) {
        Payment payment = saveReadyPayment(orderId);
        payment.syncCompletedFromWebhook(paymentKey, "{\"status\":\"DONE\"}");
        Membership membership = membershipRepository.save(Membership.create(
                payment.getStudioId(),
                payment.getMemberId(),
                payment.getMembershipName(),
                payment.getMembershipType(),
                payment.getMembershipTotalCount(),
                payment.getMembershipStartDate(),
                payment.getMembershipEndDate()
        ));
        payment.linkMembership(membership.getId());
        return paymentRepository.save(payment);
    }

    private PaymentWebhookRequest webhookRequest(
            String eventType,
            String orderId,
            String paymentKey,
            String failureReason,
            String rawResponse
    ) {
        PaymentWebhookRequest request = new PaymentWebhookRequest();
        setField(request, "eventType", eventType);
        setField(request, "orderId", orderId);
        setField(request, "paymentKey", paymentKey);
        setField(request, "failureReason", failureReason);
        setField(request, "rawResponse", rawResponse);
        return request;
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
