package com.lessonring.api.payment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.payment.api.request.PaymentWebhookRequest;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.PaymentMethod;
import com.lessonring.api.payment.domain.PaymentStatus;
import com.lessonring.api.payment.domain.PaymentWebhookLog;
import com.lessonring.api.payment.domain.repository.PaymentRepository;
import com.lessonring.api.payment.domain.repository.PaymentWebhookLogRepository;
import com.lessonring.api.membership.domain.MembershipType;
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
class PaymentWebhookServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentWebhookLogRepository paymentWebhookLogRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentWebhookService paymentWebhookService;

    @Test
    @DisplayName("PAYMENT_COMPLETED webhook 수신 시 Payment 상태가 COMPLETED로 동기화된다")
    void handle_completed_webhook_success() throws Exception {
        // given
        Payment payment = createReadyPayment();
        setField(payment, "id", 1L);
        setField(payment, "pgOrderId", "ORDER_123");

        PaymentWebhookRequest request = new PaymentWebhookRequest();
        setField(request, "eventType", "PAYMENT_COMPLETED");
        setField(request, "orderId", "ORDER_123");
        setField(request, "paymentKey", "paymentKey_123");
        setField(request, "rawResponse", "{\"status\":\"DONE\"}");

        given(paymentRepository.findByPgOrderId("ORDER_123"))
                .willReturn(Optional.of(payment));
        given(objectMapper.writeValueAsString(any()))
                .willReturn("{\"eventType\":\"PAYMENT_COMPLETED\"}");
        given(paymentWebhookLogRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        paymentWebhookService.handle(null, null, request);

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getPgPaymentKey()).isEqualTo("paymentKey_123");
        assertThat(payment.getPgRawResponse()).isEqualTo("{\"status\":\"DONE\"}");
        assertThat(payment.getPaidAt()).isNotNull();
    }

    @Test
    @DisplayName("PAYMENT_FAILED webhook 수신 시 Payment 상태가 FAILED로 동기화된다")
    void handle_failed_webhook_success() throws Exception {
        // given
        Payment payment = createReadyPayment();
        setField(payment, "id", 1L);
        setField(payment, "pgOrderId", "ORDER_FAIL");

        PaymentWebhookRequest request = new PaymentWebhookRequest();
        setField(request, "eventType", "PAYMENT_FAILED");
        setField(request, "orderId", "ORDER_FAIL");
        setField(request, "failureReason", "승인 실패");
        setField(request, "rawResponse", "{\"code\":\"FAILED\"}");

        given(paymentRepository.findByPgOrderId("ORDER_FAIL"))
                .willReturn(Optional.of(payment));
        given(objectMapper.writeValueAsString(any()))
                .willReturn("{\"eventType\":\"PAYMENT_FAILED\"}");
        given(paymentWebhookLogRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        paymentWebhookService.handle(null, null, request);

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(payment.getFailedReason()).isEqualTo("승인 실패");
        assertThat(payment.getPgRawResponse()).isEqualTo("{\"code\":\"FAILED\"}");
    }

    @Test
    @DisplayName("PAYMENT_CANCELED webhook 수신 시 Payment 상태가 CANCELED로 동기화된다")
    void handle_canceled_webhook_success() throws Exception {
        // given
        Payment payment = createReadyPayment();
        setField(payment, "id", 1L);
        setField(payment, "pgOrderId", "ORDER_CANCEL");

        PaymentWebhookRequest request = new PaymentWebhookRequest();
        setField(request, "eventType", "PAYMENT_CANCELED");
        setField(request, "orderId", "ORDER_CANCEL");
        setField(request, "rawResponse", "{\"status\":\"CANCELED\"}");

        given(paymentRepository.findByPgOrderId("ORDER_CANCEL"))
                .willReturn(Optional.of(payment));
        given(objectMapper.writeValueAsString(any()))
                .willReturn("{\"eventType\":\"PAYMENT_CANCELED\"}");
        given(paymentWebhookLogRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        paymentWebhookService.handle(null, null, request);

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
        assertThat(payment.getPgRawResponse()).isEqualTo("{\"status\":\"CANCELED\"}");
        assertThat(payment.getCanceledAt()).isNotNull();
    }

    @Test
    @DisplayName("orderId 없는 webhook 요청은 예외가 발생한다")
    void handle_webhook_fail_when_orderId_missing() {
        // given
        PaymentWebhookRequest request = new PaymentWebhookRequest();
        setField(request, "eventType", "PAYMENT_COMPLETED");
        setField(request, "paymentKey", "paymentKey_123");

        // when & then
        assertThatThrownBy(() -> paymentWebhookService.handle(null, null, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("orderId가 없는 webhook 요청입니다.");
    }

    @Test
    @DisplayName("지원하지 않는 eventType은 예외 없이 무시된다")
    void handle_webhook_ignore_unknown_eventType() {
        // given
        Payment payment = createReadyPayment();
        setField(payment, "id", 1L);
        setField(payment, "pgOrderId", "ORDER_UNKNOWN");

        PaymentWebhookRequest request = new PaymentWebhookRequest();
        setField(request, "eventType", "UNKNOWN_EVENT");
        setField(request, "orderId", "ORDER_UNKNOWN");

        given(paymentRepository.findByPgOrderId("ORDER_UNKNOWN"))
                .willReturn(Optional.of(payment));

        // when
        paymentWebhookService.handle(null, null, request);

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.READY);
    }

    @Test
    @DisplayName("이미 COMPLETED 상태인 결제에 같은 COMPLETED webhook이 다시 와도 무시된다")
    void handle_completed_webhook_should_ignore_when_already_completed() throws Exception {
        // given
        Payment payment = createReadyPayment();
        setField(payment, "id", 1L);
        setField(payment, "pgOrderId", "ORDER_123");
        payment.syncCompletedFromWebhook("paymentKey_123", "{\"status\":\"DONE\"}");

        PaymentWebhookRequest request = new PaymentWebhookRequest();
        setField(request, "eventType", "PAYMENT_COMPLETED");
        setField(request, "orderId", "ORDER_123");
        setField(request, "paymentKey", "paymentKey_123");
        setField(request, "rawResponse", "{\"status\":\"DONE_AGAIN\"}");

        given(paymentRepository.findByPgOrderId("ORDER_123"))
                .willReturn(Optional.of(payment));
        given(objectMapper.writeValueAsString(any()))
                .willReturn("{\"eventType\":\"PAYMENT_COMPLETED\"}");
        given(paymentWebhookLogRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        paymentWebhookService.handle(null, null, request);

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getPgPaymentKey()).isEqualTo("paymentKey_123");
    }

    @Test
    @DisplayName("이미 CANCELED 상태인 결제에 같은 CANCELED webhook이 다시 와도 무시된다")
    void handle_canceled_webhook_should_ignore_when_already_canceled() throws Exception {
        // given
        Payment payment = createReadyPayment();
        setField(payment, "id", 1L);
        setField(payment, "pgOrderId", "ORDER_CANCEL");
        payment.syncCanceledFromWebhook("{\"status\":\"CANCELED\"}");

        PaymentWebhookRequest request = new PaymentWebhookRequest();
        setField(request, "eventType", "PAYMENT_CANCELED");
        setField(request, "orderId", "ORDER_CANCEL");
        setField(request, "rawResponse", "{\"status\":\"CANCELED_AGAIN\"}");

        given(paymentRepository.findByPgOrderId("ORDER_CANCEL"))
                .willReturn(Optional.of(payment));
        given(objectMapper.writeValueAsString(any()))
                .willReturn("{\"eventType\":\"PAYMENT_CANCELED\"}");
        given(paymentWebhookLogRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        paymentWebhookService.handle(null, null, request);

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
        assertThat(payment.getCanceledAt()).isNotNull();
    }

    @Test
    @DisplayName("이미 FAILED 상태인 결제에 같은 FAILED webhook이 다시 와도 무시된다")
    void handle_failed_webhook_should_ignore_when_already_failed() throws Exception {
        // given
        Payment payment = createReadyPayment();
        setField(payment, "id", 1L);
        setField(payment, "pgOrderId", "ORDER_FAIL");
        payment.syncFailedFromWebhook("승인 실패", "{\"code\":\"FAILED\"}");

        PaymentWebhookRequest request = new PaymentWebhookRequest();
        setField(request, "eventType", "PAYMENT_FAILED");
        setField(request, "orderId", "ORDER_FAIL");
        setField(request, "failureReason", "다시 실패");
        setField(request, "rawResponse", "{\"code\":\"FAILED_AGAIN\"}");

        given(paymentRepository.findByPgOrderId("ORDER_FAIL"))
                .willReturn(Optional.of(payment));
        given(objectMapper.writeValueAsString(any()))
                .willReturn("{\"eventType\":\"PAYMENT_FAILED\"}");
        given(paymentWebhookLogRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        paymentWebhookService.handle(null, null, request);

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(payment.getFailedReason()).isEqualTo("승인 실패");
    }

    @Test
    @DisplayName("같은 transmissionId의 webhook이 다시 오면 중복 처리하지 않는다")
    void handle_should_ignore_when_same_transmission_id_already_processed() {
        // given
        PaymentWebhookRequest request = new PaymentWebhookRequest();
        setField(request, "eventType", "PAYMENT_COMPLETED");
        setField(request, "orderId", "ORDER_123");
        setField(request, "paymentKey", "paymentKey_123");
        setField(request, "rawResponse", "{\"status\":\"DONE\"}");

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

        // when
        paymentWebhookService.handle("tx-123", null, request);

        // then
        // 중복이므로 예외 없이 종료되면 성공
    }

    private Payment createReadyPayment() {
        return Payment.create(
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