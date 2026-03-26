package com.lessonring.api.payment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.lessonring.api.membership.domain.MembershipType;
import com.lessonring.api.membership.domain.repository.MembershipRepository;
import com.lessonring.api.payment.api.request.PaymentApproveRequest;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.PaymentMethod;
import com.lessonring.api.payment.domain.PaymentOperation;
import com.lessonring.api.payment.domain.PaymentOperationStatus;
import com.lessonring.api.payment.domain.PaymentOperationType;
import com.lessonring.api.payment.domain.PaymentStatus;
import com.lessonring.api.payment.domain.repository.PaymentOperationRepository;
import com.lessonring.api.payment.domain.repository.PaymentRepository;
import com.lessonring.api.payment.infrastructure.pg.PgApproveResponse;
import com.lessonring.api.payment.infrastructure.pg.PgClient;
import com.lessonring.api.support.TestExternalMockConfig;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestExternalMockConfig.class)
class PaymentPgServiceConcurrencyTest {

    @Autowired
    private PaymentPgService paymentPgService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private PaymentOperationRepository paymentOperationRepository;

    @Autowired
    private PgClient pgClient;

    @BeforeEach
    void setUp() {
        paymentOperationRepository.deleteAllInBatch();
        membershipRepository.deleteAllInBatch();
        paymentRepository.deleteAllInBatch();
    }

    @AfterEach
    void tearDown() {
        paymentOperationRepository.deleteAllInBatch();
        membershipRepository.deleteAllInBatch();
        paymentRepository.deleteAllInBatch();
        Mockito.reset(pgClient);
    }

    @Test
    @DisplayName("서로 다른 idempotencyKey로 동일 payment 동시 approve 시 1건만 완료된다")
    void approve_concurrent_with_different_idempotency_keys_only_one_succeeds() throws Exception {
        Payment payment = saveReadyPayment();

        Mockito.when(pgClient.approve(any()))
                .thenAnswer(invocation -> {
                    Thread.sleep(300);
                    return successPgResponse("paymentKey_123", "ORDER_123", 100_000L);
                });

        PaymentApproveRequest request = approveRequest("paymentKey_123", "ORDER_123", 100_000L);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Callable<ResultHolder> task1 = concurrentApproveTask(
                payment.getId(),
                request,
                "approve-key-1",
                readyLatch,
                startLatch
        );

        Callable<ResultHolder> task2 = concurrentApproveTask(
                payment.getId(),
                request,
                "approve-key-2",
                readyLatch,
                startLatch
        );

        Future<ResultHolder> future1 = executor.submit(task1);
        Future<ResultHolder> future2 = executor.submit(task2);

        readyLatch.await();
        startLatch.countDown();

        ResultHolder result1 = future1.get();
        ResultHolder result2 = future2.get();

        executor.shutdown();

        long successCount = List.of(result1, result2).stream()
                .filter(ResultHolder::isSuccess)
                .count();

        long failCount = List.of(result1, result2).stream()
                .filter(ResultHolder::isFailure)
                .count();

        assertThat(successCount).isEqualTo(1);
        assertThat(failCount).isEqualTo(1);

        Payment savedPayment = paymentRepository.findById(payment.getId()).orElseThrow();
        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);

        assertThat(membershipRepository.findAll()).hasSize(1);

        List<PaymentOperation> operations = paymentOperationRepository.findAll();
        assertThat(operations).hasSize(2);

        long succeededOps = operations.stream()
                .filter(op -> op.getStatus() == PaymentOperationStatus.SUCCEEDED)
                .count();

        long failedOps = operations.stream()
                .filter(op -> op.getStatus() == PaymentOperationStatus.FAILED)
                .count();

        assertThat(succeededOps).isEqualTo(1);
        assertThat(failedOps).isEqualTo(1);

        verify(pgClient, times(1)).approve(any());
    }

    @Test
    @DisplayName("동일 idempotencyKey로 동일 payment 동시 approve 시 최종 1건만 처리되고 PG 호출은 1회다")
    void approve_concurrent_with_same_idempotency_key_calls_pg_once() throws Exception {
        Payment payment = saveReadyPayment();

        Mockito.when(pgClient.approve(any()))
                .thenAnswer(invocation -> {
                    Thread.sleep(300);
                    return successPgResponse("paymentKey_123", "ORDER_123", 100_000L);
                });

        PaymentApproveRequest request = approveRequest("paymentKey_123", "ORDER_123", 100_000L);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Callable<ResultHolder> task1 = concurrentApproveTask(
                payment.getId(),
                request,
                "approve-key-1",
                readyLatch,
                startLatch
        );

        Callable<ResultHolder> task2 = concurrentApproveTask(
                payment.getId(),
                request,
                "approve-key-1",
                readyLatch,
                startLatch
        );

        Future<ResultHolder> future1 = executor.submit(task1);
        Future<ResultHolder> future2 = executor.submit(task2);

        readyLatch.await();
        startLatch.countDown();

        ResultHolder result1 = future1.get();
        ResultHolder result2 = future2.get();

        executor.shutdown();

        long successOrReusedCount = List.of(result1, result2).stream()
                .filter(ResultHolder::isSuccess)
                .count();

        long failureCount = List.of(result1, result2).stream()
                .filter(ResultHolder::isFailure)
                .count();

        assertThat(successOrReusedCount).isGreaterThanOrEqualTo(1);
        assertThat(successOrReusedCount + failureCount).isEqualTo(2);

        Payment savedPayment = paymentRepository.findById(payment.getId()).orElseThrow();
        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);

        assertThat(membershipRepository.findAll()).hasSize(1);

        Optional<PaymentOperation> operationOpt =
                paymentOperationRepository.findByPaymentIdAndOperationTypeAndIdempotencyKey(
                        payment.getId(),
                        PaymentOperationType.APPROVE,
                        "approve-key-1"
                );

        PaymentOperation operation = operationOpt.orElseThrow();
        assertThat(operation.getStatus()).isEqualTo(PaymentOperationStatus.SUCCEEDED);
        assertThat(operation.getResponsePayload()).isNotBlank();

        verify(pgClient, times(1)).approve(any());
    }

    private Callable<ResultHolder> concurrentApproveTask(
            Long paymentId,
            PaymentApproveRequest request,
            String idempotencyKey,
            CountDownLatch readyLatch,
            CountDownLatch startLatch
    ) {
        return () -> {
            readyLatch.countDown();
            startLatch.await();

            try {
                paymentPgService.approve(paymentId, request, idempotencyKey);
                return ResultHolder.success();
            } catch (Exception e) {
                return ResultHolder.failure(e);
            }
        };
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
        private final Exception exception;

        private ResultHolder(boolean success, Exception exception) {
            this.success = success;
            this.exception = exception;
        }

        public static ResultHolder success() {
            return new ResultHolder(true, null);
        }

        public static ResultHolder failure(Exception e) {
            return new ResultHolder(false, e);
        }

        public boolean isSuccess() {
            return success;
        }

        public boolean isFailure() {
            return !success;
        }
    }
}
