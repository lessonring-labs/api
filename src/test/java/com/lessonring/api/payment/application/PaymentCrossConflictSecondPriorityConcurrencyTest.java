package com.lessonring.api.payment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.lessonring.api.membership.domain.MembershipType;
import com.lessonring.api.payment.api.request.PaymentApproveRequest;
import com.lessonring.api.payment.api.request.PaymentWebhookRequest;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestExternalMockConfig.class)
class PaymentCrossConflictSecondPriorityConcurrencyTest {

    @Autowired
    private PaymentPgService paymentPgService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentWebhookService paymentWebhookService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentWebhookLogRepository paymentWebhookLogRepository;

    @Autowired
    private PgClient pgClient;

    @Autowired
    private PaymentWebhookPgVerificationService paymentWebhookPgVerificationService;

    @AfterEach
    void tearDown() {
        paymentWebhookLogRepository.deleteAll();
        paymentRepository.deleteAll();
        Mockito.reset(pgClient, paymentWebhookPgVerificationService);
    }

    @Test
    @DisplayName("approve API 와 refund API 동시 진입 시 READY 상태에서는 refund가 이기지 못한다")
    void approve_vs_refund() throws Exception {
        Payment payment = saveReadyPayment("ORDER_APPROVE_REFUND");

        PaymentApproveRequest approveRequest = approveRequest(
                "paymentKey_approve_refund",
                "ORDER_APPROVE_REFUND",
                100_000L
        );

        Mockito.when(pgClient.approve(any()))
                .thenAnswer(invocation -> {
                    Thread.sleep(300);
                    return PgApproveResponse.builder()
                            .provider("TOSS")
                            .paymentKey("paymentKey_approve_refund")
                            .orderId("ORDER_APPROVE_REFUND")
                            .amount(100_000L)
                            .success(true)
                            .rawResponse("{\"status\":\"DONE\"}")
                            .build();
                });

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Callable<ResultHolder> approveTask = () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                paymentPgService.approve(payment.getId(), approveRequest, "approve-key-second-priority-1");
                return ResultHolder.success("approve");
            } catch (Exception e) {
                return ResultHolder.failure("approve", e);
            }
        };

        Callable<ResultHolder> refundTask = () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                paymentService.refund(payment.getId(), "refund-key-second-priority-1");
                return ResultHolder.success("refund");
            } catch (Exception e) {
                return ResultHolder.failure("refund", e);
            }
        };

        Future<ResultHolder> future1 = executor.submit(approveTask);
        Future<ResultHolder> future2 = executor.submit(refundTask);

        readyLatch.await();
        startLatch.countDown();

        ResultHolder result1 = future1.get();
        ResultHolder result2 = future2.get();

        executor.shutdown();

        Payment savedPayment = paymentRepository.findById(payment.getId()).orElseThrow();

        long successCount = List.of(result1, result2).stream()
                .filter(ResultHolder::isSuccess)
                .count();

        assertThat(successCount).isGreaterThanOrEqualTo(1);
        assertThat(savedPayment.getStatus()).isIn(PaymentStatus.READY, PaymentStatus.COMPLETED);
        assertThat(savedPayment.getStatus()).isNotEqualTo(PaymentStatus.CANCELED);

        if (savedPayment.getStatus() == PaymentStatus.COMPLETED) {
            assertThat(savedPayment.getPgPaymentKey()).isEqualTo("paymentKey_approve_refund");
            verify(pgClient, times(1)).approve(any());
        }
    }

    @Test
    @DisplayName("webhook completed 와 webhook canceled 동시 진입 시 PG 조회 결과가 completed면 최종 COMPLETED")
    void webhook_completed_vs_webhook_canceled_pg_completed_wins() throws Exception {
        Payment payment = saveReadyPayment("ORDER_WEBHOOK_CONFLICT_COMPLETED");

        PaymentWebhookRequest completedRequest = webhookRequest(
                "PAYMENT_COMPLETED",
                "ORDER_WEBHOOK_CONFLICT_COMPLETED",
                "paymentKey_webhook_conflict",
                null,
                "{\"status\":\"DONE\"}"
        );

        PaymentWebhookRequest canceledRequest = webhookRequest(
                "PAYMENT_CANCELED",
                "ORDER_WEBHOOK_CONFLICT_COMPLETED",
                "paymentKey_webhook_conflict",
                null,
                "{\"status\":\"CANCELED\"}"
        );

        Mockito.when(paymentWebhookPgVerificationService.verifyCompleted(any(), any()))
                .thenAnswer(invocation -> {
                    Thread.sleep(200);
                    return PgPaymentStatusResponse.builder()
                            .paymentKey("paymentKey_webhook_conflict")
                            .orderId("ORDER_WEBHOOK_CONFLICT_COMPLETED")
                            .totalAmount(100_000L)
                            .status("DONE")
                            .rawResponse("{\"status\":\"DONE\"}")
                            .build();
                });

        Mockito.when(paymentWebhookPgVerificationService.verifyCanceled(any(), any()))
                .thenThrow(new BusinessExceptionWrapper());

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Callable<ResultHolder> completedTask = () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                paymentWebhookService.handle(
                        "tx-second-completed",
                        "dummy-signature",
                        "{\"eventType\":\"PAYMENT_COMPLETED\"}",
                        completedRequest
                );
                return ResultHolder.success("completed");
            } catch (Exception e) {
                return ResultHolder.failure("completed", e);
            }
        };

        Callable<ResultHolder> canceledTask = () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                paymentWebhookService.handle(
                        "tx-second-canceled",
                        "dummy-signature",
                        "{\"eventType\":\"PAYMENT_CANCELED\"}",
                        canceledRequest
                );
                return ResultHolder.success("canceled");
            } catch (Exception e) {
                return ResultHolder.failure("canceled", e);
            }
        };

        Future<ResultHolder> future1 = executor.submit(completedTask);
        Future<ResultHolder> future2 = executor.submit(canceledTask);

        readyLatch.await();
        startLatch.countDown();

        ResultHolder result1 = future1.get();
        ResultHolder result2 = future2.get();

        executor.shutdown();

        Payment savedPayment = paymentRepository.findById(payment.getId()).orElseThrow();

        assertThat(List.of(result1, result2).stream().filter(ResultHolder::isSuccess).count())
                .isGreaterThanOrEqualTo(1);

        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(savedPayment.getPgPaymentKey()).isEqualTo("paymentKey_webhook_conflict");
        assertThat(paymentWebhookLogRepository.findByProviderAndTransmissionId("TOSS", "tx-second-completed"))
                .isPresent();
    }

    @Test
    @DisplayName("webhook completed 와 webhook canceled 동시 진입 시 PG 조회 결과가 canceled면 최종 CANCELED")
    void webhook_completed_vs_webhook_canceled_pg_canceled_wins() throws Exception {
        Payment payment = saveCompletedPayment("ORDER_WEBHOOK_CONFLICT_CANCELED", "paymentKey_webhook_canceled");

        PaymentWebhookRequest completedRequest = webhookRequest(
                "PAYMENT_COMPLETED",
                "ORDER_WEBHOOK_CONFLICT_CANCELED",
                "paymentKey_webhook_canceled",
                null,
                "{\"status\":\"DONE\"}"
        );

        PaymentWebhookRequest canceledRequest = webhookRequest(
                "PAYMENT_CANCELED",
                "ORDER_WEBHOOK_CONFLICT_CANCELED",
                "paymentKey_webhook_canceled",
                null,
                "{\"status\":\"CANCELED\"}"
        );

        Mockito.when(paymentWebhookPgVerificationService.verifyCompleted(any(), any()))
                .thenThrow(new BusinessExceptionWrapper());

        Mockito.when(paymentWebhookPgVerificationService.verifyCanceled(any(), any()))
                .thenAnswer(invocation -> {
                    Thread.sleep(200);
                    return PgPaymentStatusResponse.builder()
                            .paymentKey("paymentKey_webhook_canceled")
                            .orderId("ORDER_WEBHOOK_CONFLICT_CANCELED")
                            .totalAmount(100_000L)
                            .status("CANCELED")
                            .rawResponse("{\"status\":\"CANCELED\"}")
                            .build();
                });

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Callable<ResultHolder> completedTask = () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                paymentWebhookService.handle(
                        "tx-second2-completed",
                        "dummy-signature",
                        "{\"eventType\":\"PAYMENT_COMPLETED\"}",
                        completedRequest
                );
                return ResultHolder.success("completed");
            } catch (Exception e) {
                return ResultHolder.failure("completed", e);
            }
        };

        Callable<ResultHolder> canceledTask = () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                paymentWebhookService.handle(
                        "tx-second2-canceled",
                        "dummy-signature",
                        "{\"eventType\":\"PAYMENT_CANCELED\"}",
                        canceledRequest
                );
                return ResultHolder.success("canceled");
            } catch (Exception e) {
                return ResultHolder.failure("canceled", e);
            }
        };

        Future<ResultHolder> future1 = executor.submit(completedTask);
        Future<ResultHolder> future2 = executor.submit(canceledTask);

        readyLatch.await();
        startLatch.countDown();

        ResultHolder result1 = future1.get();
        ResultHolder result2 = future2.get();

        executor.shutdown();

        Payment savedPayment = paymentRepository.findById(payment.getId()).orElseThrow();

        assertThat(List.of(result1, result2).stream().filter(ResultHolder::isSuccess).count())
                .isGreaterThanOrEqualTo(1);

        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
        assertThat(paymentWebhookLogRepository.findByProviderAndTransmissionId("TOSS", "tx-second2-canceled"))
                .isPresent();
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
        private final String source;
        private final Exception exception;

        private ResultHolder(boolean success, String source, Exception exception) {
            this.success = success;
            this.source = source;
            this.exception = exception;
        }

        public static ResultHolder success(String source) {
            return new ResultHolder(true, source, null);
        }

        public static ResultHolder failure(String source, Exception exception) {
            return new ResultHolder(false, source, exception);
        }

        public boolean isSuccess() {
            return success;
        }

        public boolean isFailure() {
            return !success;
        }

        public String getSource() {
            return source;
        }

        public Exception getException() {
            return exception;
        }
    }

    /**
     * 프로젝트에 맞게 나중에 실제 BusinessException stub 으로 교체.
     * 현재는 Mockito.when(...).thenThrow(...) 자리에 바로 붙여넣기용 placeholder.
     */
    private static class BusinessExceptionWrapper extends RuntimeException {
        private BusinessExceptionWrapper() {
            super("PG verification failed");
        }
    }
}