package com.lessonring.api.payment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.membership.domain.MembershipType;
import com.lessonring.api.payment.api.request.PaymentWebhookRequest;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.PaymentMethod;
import com.lessonring.api.payment.domain.PaymentStatus;
import com.lessonring.api.payment.domain.PaymentWebhookLog;
import com.lessonring.api.payment.domain.repository.PaymentRepository;
import com.lessonring.api.payment.domain.repository.PaymentWebhookLogRepository;
import com.lessonring.api.payment.infrastructure.lock.PaymentStateLockManager;
import com.lessonring.api.payment.infrastructure.pg.PgPaymentStatusResponse;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentWebhookServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentWebhookLogRepository paymentWebhookLogRepository;

    @Mock
    private PaymentStateLockManager paymentStateLockManager;

    @Mock
    private PaymentWebhookPgVerificationService paymentWebhookPgVerificationService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentWebhookService paymentWebhookService;

    @Test
    @DisplayName("completed webhook updates payment to completed")
    void handle_completed_webhook_success() throws Exception {
        Payment payment = createReadyPayment();
        setField(payment, "id", 1L);
        setField(payment, "pgOrderId", "ORDER_123");

        PaymentWebhookRequest request = request(
                "PAYMENT_COMPLETED",
                "ORDER_123",
                "paymentKey_123",
                null,
                "{\"status\":\"DONE\"}"
        );

        stubCommonFlow(payment);
        given(paymentWebhookPgVerificationService.verifyCompleted(payment, request))
                .willReturn(pgResponse("paymentKey_123", "ORDER_123", 100_000L, "DONE", "{\"status\":\"DONE\"}"));
        given(objectMapper.writeValueAsString(any()))
                .willReturn("{\"eventType\":\"PAYMENT_COMPLETED\"}");
        given(paymentWebhookLogRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        paymentWebhookService.handle(null, null, null, request);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getPgPaymentKey()).isEqualTo("paymentKey_123");
        assertThat(payment.getPgRawResponse()).isEqualTo("{\"status\":\"DONE\"}");
        assertThat(payment.getPaidAt()).isNotNull();
    }

    @Test
    @DisplayName("failed webhook updates payment to failed")
    void handle_failed_webhook_success() throws Exception {
        Payment payment = createReadyPayment();
        setField(payment, "id", 1L);
        setField(payment, "pgOrderId", "ORDER_FAIL");

        PaymentWebhookRequest request = request(
                "PAYMENT_FAILED",
                "ORDER_FAIL",
                "paymentKey_fail",
                "approval failed",
                "{\"code\":\"FAILED\"}"
        );

        stubCommonFlow(payment);
        given(paymentWebhookPgVerificationService.verifyFailed(payment, request))
                .willReturn(pgResponse("paymentKey_fail", "ORDER_FAIL", 100_000L, "FAILED", "{\"code\":\"FAILED\"}"));
        given(objectMapper.writeValueAsString(any()))
                .willReturn("{\"eventType\":\"PAYMENT_FAILED\"}");
        given(paymentWebhookLogRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        paymentWebhookService.handle(null, null, null, request);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(payment.getFailedReason()).isEqualTo("approval failed");
        assertThat(payment.getPgRawResponse()).isEqualTo("{\"code\":\"FAILED\"}");
    }

    @Test
    @DisplayName("canceled webhook updates payment to canceled")
    void handle_canceled_webhook_success() throws Exception {
        Payment payment = createReadyPayment();
        setField(payment, "id", 1L);
        setField(payment, "pgOrderId", "ORDER_CANCEL");

        PaymentWebhookRequest request = request(
                "PAYMENT_CANCELED",
                "ORDER_CANCEL",
                "paymentKey_cancel",
                null,
                "{\"status\":\"CANCELED\"}"
        );

        stubCommonFlow(payment);
        given(paymentWebhookPgVerificationService.verifyCanceled(payment, request))
                .willReturn(pgResponse("paymentKey_cancel", "ORDER_CANCEL", 100_000L, "CANCELED", "{\"status\":\"CANCELED\"}"));
        given(objectMapper.writeValueAsString(any()))
                .willReturn("{\"eventType\":\"PAYMENT_CANCELED\"}");
        given(paymentWebhookLogRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        paymentWebhookService.handle(null, null, null, request);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
        assertThat(payment.getPgRawResponse()).isEqualTo("{\"status\":\"CANCELED\"}");
        assertThat(payment.getCanceledAt()).isNotNull();
    }

    @Test
    @DisplayName("missing orderId throws business exception")
    void handle_webhook_fail_when_order_id_missing() {
        PaymentWebhookRequest request = request("PAYMENT_COMPLETED", null, "paymentKey_123", null, null);

        assertThatThrownBy(() -> paymentWebhookService.handle(null, null, null, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("unknown eventType is ignored")
    void handle_webhook_ignore_unknown_event_type() {
        Payment payment = createReadyPayment();
        setField(payment, "id", 1L);
        setField(payment, "pgOrderId", "ORDER_UNKNOWN");

        PaymentWebhookRequest request = request("UNKNOWN_EVENT", "ORDER_UNKNOWN", null, null, null);

        stubCommonFlow(payment);

        paymentWebhookService.handle(null, null, null, request);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.READY);
    }

    @Test
    @DisplayName("already completed payment ignores completed webhook")
    void handle_completed_webhook_should_ignore_when_already_completed() throws Exception {
        Payment payment = createReadyPayment();
        setField(payment, "id", 1L);
        setField(payment, "pgOrderId", "ORDER_123");
        payment.syncCompletedFromWebhook("paymentKey_123", "{\"status\":\"DONE\"}");

        PaymentWebhookRequest request = request(
                "PAYMENT_COMPLETED",
                "ORDER_123",
                "paymentKey_123",
                null,
                "{\"status\":\"DONE_AGAIN\"}"
        );

        stubCommonFlow(payment);
        given(paymentWebhookPgVerificationService.verifyCompleted(payment, request))
                .willReturn(pgResponse("paymentKey_123", "ORDER_123", 100_000L, "DONE", "{\"status\":\"DONE_AGAIN\"}"));
        given(objectMapper.writeValueAsString(any()))
                .willReturn("{\"eventType\":\"PAYMENT_COMPLETED\"}");
        given(paymentWebhookLogRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        paymentWebhookService.handle(null, null, null, request);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getPgPaymentKey()).isEqualTo("paymentKey_123");
    }

    @Test
    @DisplayName("already canceled payment ignores canceled webhook")
    void handle_canceled_webhook_should_ignore_when_already_canceled() throws Exception {
        Payment payment = createReadyPayment();
        setField(payment, "id", 1L);
        setField(payment, "pgOrderId", "ORDER_CANCEL");
        payment.syncCanceledFromWebhook("{\"status\":\"CANCELED\"}");

        PaymentWebhookRequest request = request(
                "PAYMENT_CANCELED",
                "ORDER_CANCEL",
                "paymentKey_cancel",
                null,
                "{\"status\":\"CANCELED_AGAIN\"}"
        );

        stubCommonFlow(payment);
        given(paymentWebhookPgVerificationService.verifyCanceled(payment, request))
                .willReturn(pgResponse("paymentKey_cancel", "ORDER_CANCEL", 100_000L, "CANCELED", "{\"status\":\"CANCELED_AGAIN\"}"));
        given(objectMapper.writeValueAsString(any()))
                .willReturn("{\"eventType\":\"PAYMENT_CANCELED\"}");
        given(paymentWebhookLogRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        paymentWebhookService.handle(null, null, null, request);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
        assertThat(payment.getCanceledAt()).isNotNull();
    }

    @Test
    @DisplayName("already failed payment ignores failed webhook")
    void handle_failed_webhook_should_ignore_when_already_failed() throws Exception {
        Payment payment = createReadyPayment();
        setField(payment, "id", 1L);
        setField(payment, "pgOrderId", "ORDER_FAIL");
        payment.syncFailedFromWebhook("approval failed", "{\"code\":\"FAILED\"}");

        PaymentWebhookRequest request = request(
                "PAYMENT_FAILED",
                "ORDER_FAIL",
                "paymentKey_fail",
                "failed again",
                "{\"code\":\"FAILED_AGAIN\"}"
        );

        stubCommonFlow(payment);
        given(paymentWebhookPgVerificationService.verifyFailed(payment, request))
                .willReturn(pgResponse("paymentKey_fail", "ORDER_FAIL", 100_000L, "FAILED", "{\"code\":\"FAILED_AGAIN\"}"));
        given(objectMapper.writeValueAsString(any()))
                .willReturn("{\"eventType\":\"PAYMENT_FAILED\"}");
        given(paymentWebhookLogRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        paymentWebhookService.handle(null, null, null, request);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(payment.getFailedReason()).isEqualTo("approval failed");
    }

    @Test
    @DisplayName("duplicate transmissionId is ignored")
    void handle_should_ignore_when_same_transmission_id_already_processed() {
        PaymentWebhookRequest request = request(
                "PAYMENT_COMPLETED",
                "ORDER_123",
                "paymentKey_123",
                null,
                "{\"status\":\"DONE\"}"
        );

        given(paymentWebhookLogRepository.findByProviderAndTransmissionId("TOSS", "tx-123"))
                .willReturn(Optional.of(
                        PaymentWebhookLog.create(
                                "TOSS",
                                "tx-123",
                                "PAYMENT_COMPLETED",
                                "ORDER_123",
                                "paymentKey_123",
                                "{}"
                        )
                ));

        paymentWebhookService.handle("tx-123", null, null, request);
    }

    private void stubCommonFlow(Payment payment) {
        given(paymentRepository.findByPgOrderId(payment.getPgOrderId()))
                .willReturn(Optional.of(payment));
        given(paymentRepository.findById(payment.getId()))
                .willReturn(Optional.of(payment));
        given(paymentStateLockManager.tryLock(payment.getId(), 3L, TimeUnit.SECONDS))
                .willReturn(true);
    }

    private PaymentWebhookRequest request(
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

    private Payment createReadyPayment() {
        return Payment.create(
                1L,
                10L,
                "10-count payment",
                PaymentMethod.CARD,
                100_000L,
                "10-count",
                MembershipType.COUNT,
                10,
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );
    }

    private PgPaymentStatusResponse pgResponse(
            String paymentKey,
            String orderId,
            Long totalAmount,
            String status,
            String rawResponse
    ) {
        return PgPaymentStatusResponse.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .totalAmount(totalAmount)
                .status(status)
                .rawResponse(rawResponse)
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
