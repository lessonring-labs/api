package com.lessonring.api.payment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;

import com.lessonring.api.membership.domain.Membership;
import com.lessonring.api.membership.domain.MembershipType;
import com.lessonring.api.membership.domain.repository.MembershipRepository;
import com.lessonring.api.payment.api.request.PaymentApproveRequest;
import com.lessonring.api.payment.api.request.PaymentWebhookRequest;
import com.lessonring.api.payment.api.response.RefundResponse;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.PaymentMethod;
import com.lessonring.api.payment.domain.PaymentStatus;
import com.lessonring.api.payment.domain.repository.PaymentRepository;
import com.lessonring.api.payment.domain.repository.PaymentWebhookLogRepository;
import com.lessonring.api.payment.infrastructure.pg.PgApproveResponse;
import com.lessonring.api.payment.infrastructure.pg.PgClient;
import com.lessonring.api.payment.infrastructure.pg.PgPaymentStatusResponse;
import com.lessonring.api.support.TestExternalMockConfig;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
class PaymentCrossConflictConcurrencyTest {

    @Autowired
    private PaymentPgService paymentPgService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentWebhookService paymentWebhookService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private PaymentWebhookLogRepository paymentWebhookLogRepository;

    @Autowired
    private PgClient pgClient;

    @Autowired
    @MockBean
    private PaymentWebhookPgVerificationService paymentWebhookPgVerificationService;

    @BeforeEach
    void setUp() {
        paymentWebhookLogRepository.deleteAll();
        membershipRepository.deleteAll();
        paymentRepository.deleteAll();
        Mockito.reset(pgClient, paymentWebhookPgVerificationService);
    }

    @AfterEach
    void tearDown() {
        paymentWebhookLogRepository.deleteAll();
        membershipRepository.deleteAll();
        paymentRepository.deleteAll();
        Mockito.reset(pgClient, paymentWebhookPgVerificationService);
    }

    @Test
    @DisplayName("approve API 와 webhook completed 동시 진입 시 최종 COMPLETED 1회만 반영된다")
    void approve_vs_webhook_completed() throws Exception {
        Payment payment = saveReadyPayment("ORDER_APPROVE_WEBHOOK");

        PaymentApproveRequest approveRequest = approveRequest(
                "paymentKey_approve_1",
                "ORDER_APPROVE_WEBHOOK",
                100_000L
        );

        PaymentWebhookRequest webhookRequest = webhookRequest(
                "PAYMENT_COMPLETED",
                "ORDER_APPROVE_WEBHOOK",
                "paymentKey_approve_1",
                null,
                "{\"status\":\"DONE\"}"
        );

        Mockito.when(pgClient.approve(any()))
                .thenAnswer(invocation -> {
                    Thread.sleep(300);
                    return PgApproveResponse.builder()
                            .provider("TOSS")
                            .paymentKey("paymentKey_approve_1")
                            .orderId("ORDER_APPROVE_WEBHOOK")
                            .amount(100_000L)
                            .success(true)
                            .rawResponse("{\"status\":\"DONE\"}")
                            .build();
                });

        Mockito.when(paymentWebhookPgVerificationService.verifyCompleted(any(), any()))
                .thenAnswer(invocation -> PgPaymentStatusResponse.builder()
                        .paymentKey("paymentKey_approve_1")
                        .orderId("ORDER_APPROVE_WEBHOOK")
                        .totalAmount(100_000L)
                        .status("DONE")
                        .rawResponse("{\"status\":\"DONE\"}")
                        .build());

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Callable<ResultHolder> approveTask = () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                paymentPgService.approve(payment.getId(), approveRequest, "approve-key-cross-1");
                return ResultHolder.success();
            } catch (Exception e) {
                return ResultHolder.failure(e);
            }
        };

        Callable<ResultHolder> webhookTask = () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                paymentWebhookService.handle(
                        "tx-cross-approve-completed",
                        "dummy-signature",
                        "{\"eventType\":\"PAYMENT_COMPLETED\"}",
                        webhookRequest
                );
                return ResultHolder.success();
            } catch (Exception e) {
                return ResultHolder.failure(e);
            }
        };

        Future<ResultHolder> future1 = executor.submit(approveTask);
        Future<ResultHolder> future2 = executor.submit(webhookTask);

        readyLatch.await();
        startLatch.countDown();

        ResultHolder result1 = future1.get();
        ResultHolder result2 = future2.get();

        executor.shutdown();

        Payment savedPayment = paymentRepository.findById(payment.getId()).orElseThrow();

        assertThat(List.of(result1, result2).stream().filter(ResultHolder::isSuccess).count())
                .isGreaterThanOrEqualTo(1);

        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(savedPayment.getPgPaymentKey()).isEqualTo("paymentKey_approve_1");

        assertThat(membershipRepository.findAll()).hasSize(1);
        assertThat(paymentWebhookLogRepository.findByProviderAndTransmissionId("TOSS", "tx-cross-approve-completed"))
                .isPresent();

        verify(pgClient, atMost(1)).approve(any());
    }

    @Test
    @DisplayName("refund API 와 webhook canceled 동시 진입 시 최종 CANCELED 1회만 반영된다")
    void refund_vs_webhook_canceled() throws Exception {
        Payment payment = saveCompletedPayment("ORDER_REFUND_WEBHOOK", "paymentKey_cancel_1");
        Membership membership = saveMembershipForCompletedPayment(payment);
        linkMembership(payment, membership.getId());

        PaymentWebhookRequest webhookRequest = webhookRequest(
                "PAYMENT_CANCELED",
                "ORDER_REFUND_WEBHOOK",
                "paymentKey_cancel_1",
                null,
                "{\"status\":\"CANCELED\"}"
        );

        Mockito.when(pgClient.cancel(any()))
                .thenAnswer(invocation -> {
                    Thread.sleep(300);
                    return com.lessonring.api.payment.infrastructure.pg.PgCancelResponse.builder()
                            .success(true)
                            .paymentKey("paymentKey_cancel_1")
                            .rawResponse("{\"status\":\"CANCELED\"}")
                            .build();
                });

        Mockito.when(paymentWebhookPgVerificationService.verifyCanceled(any(), any()))
                .thenAnswer(invocation -> PgPaymentStatusResponse.builder()
                        .paymentKey("paymentKey_cancel_1")
                        .orderId("ORDER_REFUND_WEBHOOK")
                        .totalAmount(100_000L)
                        .status("CANCELED")
                        .rawResponse("{\"status\":\"CANCELED\"}")
                        .build());

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Callable<ResultHolder> refundTask = () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                RefundResponse response = paymentService.refund(payment.getId(), "refund-key-cross-1");
                return ResultHolder.success(response);
            } catch (Exception e) {
                return ResultHolder.failure(e);
            }
        };

        Callable<ResultHolder> webhookTask = () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                paymentWebhookService.handle(
                        "tx-cross-refund-canceled",
                        "dummy-signature",
                        "{\"eventType\":\"PAYMENT_CANCELED\"}",
                        webhookRequest
                );
                return ResultHolder.success();
            } catch (Exception e) {
                return ResultHolder.failure(e);
            }
        };

        Future<ResultHolder> future1 = executor.submit(refundTask);
        Future<ResultHolder> future2 = executor.submit(webhookTask);

        readyLatch.await();
        startLatch.countDown();

        ResultHolder result1 = future1.get();
        ResultHolder result2 = future2.get();

        executor.shutdown();

        Payment savedPayment = paymentRepository.findById(payment.getId()).orElseThrow();
        Membership savedMembership = membershipRepository.findById(membership.getId()).orElseThrow();

        assertThat(List.of(result1, result2).stream().filter(ResultHolder::isSuccess).count())
                .isGreaterThanOrEqualTo(1);

        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
        assertThat(savedMembership.isRefunded()).isTrue();

        assertThat(paymentWebhookLogRepository.findByProviderAndTransmissionId("TOSS", "tx-cross-refund-canceled"))
                .isPresent();

        verify(pgClient, atMost(1)).cancel(any());
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
        return paymentRepository.save(payment);
    }

    private Membership saveMembershipForCompletedPayment(Payment payment) {
        Membership membership = Membership.create(
                payment.getStudioId(),
                payment.getMemberId(),
                payment.getMembershipName(),
                payment.getMembershipType(),
                payment.getMembershipTotalCount(),
                payment.getMembershipStartDate(),
                payment.getMembershipEndDate()
        );
        return membershipRepository.save(membership);
    }

    private void linkMembership(Payment payment, Long membershipId) {
        setField(payment, "membershipId", membershipId);
        paymentRepository.save(payment);
    }

    private PaymentApproveRequest approveRequest(String paymentKey, String orderId, Long amount) {
        PaymentApproveRequest request = new PaymentApproveRequest();
        setField(request, "paymentKey", paymentKey);
        setField(request, "orderId", orderId);
        setField(request, "amount", amount);
        return request;
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

    private static class ResultHolder {

        private final boolean success;
        private final Object value;
        private final Exception exception;

        private ResultHolder(boolean success, Object value, Exception exception) {
            this.success = success;
            this.value = value;
            this.exception = exception;
        }

        public static ResultHolder success() {
            return new ResultHolder(true, null, null);
        }

        public static ResultHolder success(Object value) {
            return new ResultHolder(true, value, null);
        }

        public static ResultHolder failure(Exception exception) {
            return new ResultHolder(false, null, exception);
        }

        public boolean isSuccess() {
            return success;
        }

        public boolean isFailure() {
            return !success;
        }

        public Object getValue() {
            return value;
        }

        public Exception getException() {
            return exception;
        }
    }
}
