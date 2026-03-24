package com.lessonring.api.common.security.payment.application;

import com.lessonring.api.booking.domain.Booking;
import com.lessonring.api.booking.domain.repository.BookingRepository;
import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.member.domain.Gender;
import com.lessonring.api.member.domain.Member;
import com.lessonring.api.member.domain.repository.MemberRepository;
import com.lessonring.api.membership.domain.Membership;
import com.lessonring.api.membership.domain.MembershipType;
import com.lessonring.api.membership.domain.repository.MembershipRepository;
import com.lessonring.api.payment.api.response.RefundResponse;
import com.lessonring.api.payment.application.PaymentService;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.PaymentMethod;
import com.lessonring.api.payment.domain.repository.PaymentRepository;
import com.lessonring.api.payment.infrastructure.pg.PgCancelRequest;
import com.lessonring.api.payment.infrastructure.pg.PgCancelResponse;
import com.lessonring.api.payment.infrastructure.pg.PgClient;
import com.lessonring.api.schedule.domain.Schedule;
import com.lessonring.api.schedule.domain.ScheduleType;
import com.lessonring.api.schedule.domain.repository.ScheduleRepository;
import com.lessonring.api.support.TestExternalMockConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@Transactional
@Import(TestExternalMockConfig.class)
class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private PgClient pgClient;

    @BeforeEach
    void setUp() {
        Mockito.reset(pgClient);
    }

    @Test
    @DisplayName("COMPLETED 상태가 아닌 결제는 환불할 수 없다")
    void refund_should_fail_when_payment_is_not_completed() {
        Long studioId = 1L;
        Member member = createMember(studioId);

        Payment payment = Payment.create(
                studioId,
                member.getId(),
                "10회권 결제",
                PaymentMethod.CARD,
                100_000L,
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(30)
        );
        payment = paymentRepository.save(payment);
        Long paymentId = payment.getId();

        assertThatThrownBy(() -> paymentService.refund(paymentId))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("이미 환불된 이용권이 연결된 결제는 다시 환불할 수 없다")
    void refund_should_fail_when_membership_already_refunded() {
        Long studioId = 1L;
        Member member = createMember(studioId);

        Membership membership = Membership.create(
                studioId,
                member.getId(),
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(30)
        );
        membership = membershipRepository.save(membership);
        membership.refund();

        Payment payment = Payment.create(
                studioId,
                member.getId(),
                "10회권 결제",
                PaymentMethod.CARD,
                100_000L,
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(30)
        );
        payment = paymentRepository.save(payment);
        payment.complete(membership.getId(), "paymentKey_123", "{\"status\":\"DONE\"}");
        Long paymentId = payment.getId();

        assertThatThrownBy(() -> paymentService.refund(paymentId))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("동일 paymentId에 대해 서로 다른 idempotencyKey로 동시에 refund 요청하면 1건만 성공한다")
    void refund_concurrent_different_keys_only_one_success() throws Exception {
        RefundFixture fixture = executeInNewTransaction(this::createCountRefundFixture);

        Mockito.when(pgClient.cancel(any(PgCancelRequest.class)))
                .thenAnswer(invocation -> {
                    Thread.sleep(500);
                    PgCancelResponse response = Mockito.mock(PgCancelResponse.class);
                    Mockito.when(response.isSuccess()).thenReturn(true);
                    Mockito.when(response.getRawResponse()).thenReturn("{\"cancelKey\":\"cancel-key-1\"}");
                    return response;
                });

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        List<ResultHolder> results = new CopyOnWriteArrayList<>();

        try {
            Callable<Void> task1 = () -> {
                readyLatch.countDown();
                startLatch.await();

                try {
                    RefundResponse response = executeInNewTransaction(() ->
                            paymentService.refund(fixture.paymentId(), "refund-key-1")
                    );
                    results.add(ResultHolder.success(response));
                } catch (Exception e) {
                    results.add(ResultHolder.failure(e));
                }
                return null;
            };

            Callable<Void> task2 = () -> {
                readyLatch.countDown();
                startLatch.await();

                try {
                    RefundResponse response = executeInNewTransaction(() ->
                            paymentService.refund(fixture.paymentId(), "refund-key-2")
                    );
                    results.add(ResultHolder.success(response));
                } catch (Exception e) {
                    results.add(ResultHolder.failure(e));
                }
                return null;
            };

            Future<Void> future1 = executorService.submit(task1);
            Future<Void> future2 = executorService.submit(task2);

            readyLatch.await();
            startLatch.countDown();

            future1.get();
            future2.get();
        } finally {
            executorService.shutdownNow();
        }

        long successCount = results.stream().filter(ResultHolder::isSuccess).count();
        long failureCount = results.stream().filter(ResultHolder::isFailure).count();

        assertThat(successCount).isEqualTo(1);
        assertThat(failureCount).isEqualTo(1);

        Payment payment = paymentRepository.findById(fixture.paymentId()).orElseThrow();
        Membership membership = membershipRepository.findById(fixture.membershipId()).orElseThrow();
        Booking futureBooking1 = bookingRepository.findById(fixture.futureBookingId1()).orElseThrow();
        Booking futureBooking2 = bookingRepository.findById(fixture.futureBookingId2()).orElseThrow();
        Booking pastAttendedBooking = bookingRepository.findById(fixture.pastAttendedBookingId()).orElseThrow();

        assertThat(payment.getStatus().name()).isEqualTo("CANCELED");
        assertThat(membership.getStatus().name()).isEqualTo("REFUNDED");
        assertThat(futureBooking1.getStatus().name()).isEqualTo("CANCELED");
        assertThat(futureBooking2.getStatus().name()).isEqualTo("CANCELED");
        assertThat(pastAttendedBooking.getStatus().name()).isEqualTo("ATTENDED");

        Mockito.verify(pgClient, Mockito.times(1)).cancel(any(PgCancelRequest.class));
    }

    @Test
    @DisplayName("동일 paymentId에 대해 동일 idempotencyKey로 동시에 refund 요청하면 1건만 실제 처리된다")
    void refund_concurrent_same_key_only_one_real_execution() throws Exception {
        RefundFixture fixture = executeInNewTransaction(this::createCountRefundFixture);

        Mockito.when(pgClient.cancel(any(PgCancelRequest.class)))
                .thenAnswer(invocation -> {
                    Thread.sleep(500);
                    PgCancelResponse response = Mockito.mock(PgCancelResponse.class);
                    Mockito.when(response.isSuccess()).thenReturn(true);
                    Mockito.when(response.getRawResponse()).thenReturn("{\"cancelKey\":\"cancel-key-same\"}");
                    return response;
                });

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        List<ResultHolder> results = new CopyOnWriteArrayList<>();
        String sameKey = "refund-same-key";

        try {
            Callable<Void> task = () -> {
                readyLatch.countDown();
                startLatch.await();

                try {
                    RefundResponse response = executeInNewTransaction(() ->
                            paymentService.refund(fixture.paymentId(), sameKey)
                    );
                    results.add(ResultHolder.success(response));
                } catch (Exception e) {
                    results.add(ResultHolder.failure(e));
                }
                return null;
            };

            Future<Void> future1 = executorService.submit(task);
            Future<Void> future2 = executorService.submit(task);

            readyLatch.await();
            startLatch.countDown();

            future1.get();
            future2.get();
        } finally {
            executorService.shutdownNow();
        }

        long successCount = results.stream().filter(ResultHolder::isSuccess).count();
        long failureCount = results.stream().filter(ResultHolder::isFailure).count();

        assertThat(successCount).isEqualTo(1);
        assertThat(failureCount).isEqualTo(1);

        Payment payment = paymentRepository.findById(fixture.paymentId()).orElseThrow();
        Membership membership = membershipRepository.findById(fixture.membershipId()).orElseThrow();
        Booking futureBooking1 = bookingRepository.findById(fixture.futureBookingId1()).orElseThrow();
        Booking futureBooking2 = bookingRepository.findById(fixture.futureBookingId2()).orElseThrow();
        Booking pastAttendedBooking = bookingRepository.findById(fixture.pastAttendedBookingId()).orElseThrow();

        assertThat(payment.getStatus().name()).isEqualTo("CANCELED");
        assertThat(membership.getStatus().name()).isEqualTo("REFUNDED");
        assertThat(futureBooking1.getStatus().name()).isEqualTo("CANCELED");
        assertThat(futureBooking2.getStatus().name()).isEqualTo("CANCELED");
        assertThat(pastAttendedBooking.getStatus().name()).isEqualTo("ATTENDED");

        Mockito.verify(pgClient, Mockito.times(1)).cancel(any(PgCancelRequest.class));
    }

    @Test
    @Disabled("PG cancel 연동 이후 Mockito 기반 PaymentServiceRefundWithPgTest로 대체")
    @DisplayName("완료된 결제를 환불하면 결제는 취소되고 이용권은 환불 상태가 되며 미래 예약은 취소된다")
    void refund_completed_payment_success() {
        RefundFixture fixture = createCountRefundFixture();
        RefundResponse response = paymentService.refund(fixture.paymentId());
    }

    @Test
    @Disabled("PG cancel 연동 이후 Mockito 기반 PaymentServiceRefundWithPgTest로 대체")
    @DisplayName("COUNT 이용권 환불 금액 계산이 정확해야 한다")
    void refund_count_membership_amount_calculation() {
        RefundFixture fixture = createCountRefundFixture();
        RefundResponse response = paymentService.refund(fixture.paymentId());
    }

    @Test
    @Disabled("PG cancel 연동 이후 Mockito 기반 PaymentServiceRefundWithPgTest로 대체")
    @DisplayName("PERIOD 이용권 환불 금액 계산이 정확해야 한다")
    void refund_period_membership_amount_calculation() {
        RefundFixture fixture = createPeriodRefundFixture();
        RefundResponse response = paymentService.refund(fixture.paymentId());
    }

    @Test
    @Disabled("PG cancel 연동 이후 Mockito 기반 PaymentServiceRefundWithPgTest로 대체")
    @DisplayName("환불 시 미래 RESERVED 예약은 자동 취소되고 과거 ATTENDED 예약은 유지된다")
    void refund_should_cancel_future_reserved_bookings_only() {
        RefundFixture fixture = createCountRefundFixture();
        paymentService.refund(fixture.paymentId());
    }

    @Test
    @Disabled("PG cancel 연동 이후 Mockito 기반 PaymentServiceRefundWithPgTest로 대체")
    @DisplayName("환불 시 PaymentCanceledEvent가 발행되어 환불 완료 알림이 생성된다")
    void refund_should_create_notification() {
        RefundFixture fixture = createCountRefundFixture();
        paymentService.refund(fixture.paymentId());
    }

    @Test
    @Disabled("PG cancel 연동 이후 Mockito 기반 PaymentServiceRefundWithPgTest로 대체")
    @DisplayName("잔여 횟수가 없는 COUNT 이용권은 환불할 수 없다")
    void refund_should_fail_when_count_membership_has_no_remaining_count() {
        Long studioId = 1L;
        Member member = createMember(studioId);

        Membership membership = Membership.create(
                studioId,
                member.getId(),
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(30)
        );
        membership = membershipRepository.save(membership);

        for (int i = 0; i < 10; i++) {
            membership.useOnce(LocalDate.now());
        }

        Payment payment = Payment.create(
                studioId,
                member.getId(),
                "10회권 결제",
                PaymentMethod.CARD,
                100_000L,
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(30)
        );
        payment = paymentRepository.save(payment);
        payment.complete(membership.getId(), "paymentKey_123", "{\"status\":\"DONE\"}");
        Long paymentId = payment.getId();

        assertThatThrownBy(() -> paymentService.refund(paymentId))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @Disabled("PG cancel 연동 이후 Mockito 기반 PaymentServiceRefundWithPgTest로 대체")
    @DisplayName("만료된 PERIOD 이용권은 환불할 수 없다")
    void refund_should_fail_when_period_membership_is_expired() {
        Long studioId = 1L;
        Member member = createMember(studioId);

        Membership membership = Membership.create(
                studioId,
                member.getId(),
                "기간권",
                MembershipType.PERIOD,
                30,
                LocalDate.now().minusDays(30),
                LocalDate.now().minusDays(1)
        );
        membership = membershipRepository.save(membership);

        Payment payment = Payment.create(
                studioId,
                member.getId(),
                "기간권 결제",
                PaymentMethod.CARD,
                100_000L,
                "기간권",
                MembershipType.PERIOD,
                30,
                LocalDate.now().minusDays(30),
                LocalDate.now().minusDays(1)
        );
        payment = paymentRepository.save(payment);
        payment.complete(membership.getId(), "paymentKey_123", "{\"status\":\"DONE\"}");
        Long paymentId = payment.getId();

        assertThatThrownBy(() -> paymentService.refund(paymentId))
                .isInstanceOf(BusinessException.class);
    }

    private <T> T executeInNewTransaction(Callable<T> callable) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return template.execute(status -> {
            try {
                return callable.call();
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private RefundFixture createCountRefundFixture() {
        Long studioId = 1L;
        Long instructorId = 1L;

        Member member = createMember(studioId);
        Membership membership = createCountMembership(studioId, member.getId());

        for (int i = 0; i < 4; i++) {
            membership.useOnce(LocalDate.now());
        }

        Payment payment = createCompletedCountPayment(
                studioId,
                member.getId(),
                membership.getId()
        );

        Schedule futureSchedule1 = createSchedule(
                studioId,
                instructorId,
                "미래 수업 1",
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(3).plusHours(1)
        );

        Schedule futureSchedule2 = createSchedule(
                studioId,
                instructorId,
                "미래 수업 2",
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(5).plusHours(1)
        );

        Schedule pastSchedule = createSchedule(
                studioId,
                instructorId,
                "과거 수업",
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(5).plusHours(1)
        );

        Booking futureBooking1 = createReservedBooking(
                studioId,
                member.getId(),
                futureSchedule1.getId(),
                membership.getId()
        );

        Booking futureBooking2 = createReservedBooking(
                studioId,
                member.getId(),
                futureSchedule2.getId(),
                membership.getId()
        );

        Booking pastAttendedBooking = createAttendedBooking(
                studioId,
                member.getId(),
                pastSchedule.getId(),
                membership.getId()
        );

        return new RefundFixture(
                payment.getId(),
                member.getId(),
                membership.getId(),
                futureBooking1.getId(),
                futureBooking2.getId(),
                pastAttendedBooking.getId()
        );
    }

    private RefundFixture createPeriodRefundFixture() {
        Long studioId = 1L;
        Long instructorId = 1L;

        Member member = createMember(studioId);

        Membership membership = Membership.create(
                studioId,
                member.getId(),
                "기간권",
                MembershipType.PERIOD,
                30,
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(5)
        );
        membership = membershipRepository.save(membership);

        Payment payment = Payment.create(
                studioId,
                member.getId(),
                "기간권 결제",
                PaymentMethod.CARD,
                100_000L,
                "기간권",
                MembershipType.PERIOD,
                30,
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(5)
        );
        payment = paymentRepository.save(payment);
        payment.complete(membership.getId(), "paymentKey_123", "{\"status\":\"DONE\"}");

        Schedule futureSchedule = createSchedule(
                studioId,
                instructorId,
                "미래 기간권 수업",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(1)
        );

        Booking futureBooking = createReservedBooking(
                studioId,
                member.getId(),
                futureSchedule.getId(),
                membership.getId()
        );

        return new RefundFixture(
                payment.getId(),
                member.getId(),
                membership.getId(),
                futureBooking.getId(),
                null,
                null
        );
    }

    private Member createMember(Long studioId) {
        Member member = Member.create(
                studioId,
                "테스트회원",
                "01012345678",
                "test@example.com",
                Gender.MALE,
                LocalDate.of(1990, 1, 1),
                "테스트용 회원"
        );

        return memberRepository.save(member);
    }

    private Membership createCountMembership(Long studioId, Long memberId) {
        Membership membership = Membership.create(
                studioId,
                memberId,
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(30)
        );

        return membershipRepository.save(membership);
    }

    private Schedule createSchedule(
            Long studioId,
            Long instructorId,
            String title,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        Schedule schedule = Schedule.create(
                studioId,
                instructorId,
                title,
                ScheduleType.GROUP,
                startAt,
                endAt,
                10
        );

        return scheduleRepository.save(schedule);
    }

    private Booking createReservedBooking(
            Long studioId,
            Long memberId,
            Long scheduleId,
            Long membershipId
    ) {
        Booking booking = Booking.create(
                studioId,
                memberId,
                scheduleId,
                membershipId
        );

        return bookingRepository.save(booking);
    }

    private Booking createAttendedBooking(
            Long studioId,
            Long memberId,
            Long scheduleId,
            Long membershipId
    ) {
        Booking booking = Booking.create(
                studioId,
                memberId,
                scheduleId,
                membershipId
        );
        booking.attend();

        return bookingRepository.save(booking);
    }

    private Payment createCompletedCountPayment(
            Long studioId,
            Long memberId,
            Long membershipId
    ) {
        Payment payment = Payment.create(
                studioId,
                memberId,
                "10회권 결제",
                PaymentMethod.CARD,
                100_000L,
                "10회권",
                MembershipType.COUNT,
                10,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(30)
        );

        payment = paymentRepository.save(payment);
        payment.complete(membershipId, "paymentKey_123", "{\"status\":\"DONE\"}");

        return payment;
    }

    private static class ResultHolder {
        private final RefundResponse response;
        private final Exception exception;

        private ResultHolder(RefundResponse response, Exception exception) {
            this.response = response;
            this.exception = exception;
        }

        static ResultHolder success(RefundResponse response) {
            return new ResultHolder(response, null);
        }

        static ResultHolder failure(Exception exception) {
            return new ResultHolder(null, exception);
        }

        boolean isSuccess() {
            return response != null;
        }

        boolean isFailure() {
            return exception != null;
        }
    }

    private record RefundFixture(
            Long paymentId,
            Long memberId,
            Long membershipId,
            Long futureBookingId1,
            Long futureBookingId2,
            Long pastAttendedBookingId
    ) {
    }
}
