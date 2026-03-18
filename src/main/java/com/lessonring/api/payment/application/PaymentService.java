package com.lessonring.api.payment.application;

import com.lessonring.api.booking.domain.Booking;
import com.lessonring.api.booking.domain.repository.BookingRepository;
import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.common.event.DomainEventPublisher;
import com.lessonring.api.member.domain.repository.MemberRepository;
import com.lessonring.api.membership.domain.Membership;
import com.lessonring.api.membership.domain.MembershipType;
import com.lessonring.api.membership.domain.repository.MembershipRepository;
import com.lessonring.api.payment.api.request.PaymentCreateRequest;
import com.lessonring.api.payment.api.response.RefundResponse;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.PaymentStatus;
import com.lessonring.api.payment.domain.event.PaymentCanceledEvent;
import com.lessonring.api.payment.domain.event.PaymentCompletedEvent;
import com.lessonring.api.payment.domain.repository.PaymentRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;
    private final BookingRepository bookingRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Transactional
    public Payment create(PaymentCreateRequest request) {
        memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (request.getMembershipStartDate().isAfter(request.getMembershipEndDate())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이용권 시작일은 종료일보다 늦을 수 없습니다.");
        }

        Payment payment = Payment.create(
                request.getStudioId(),
                request.getMemberId(),
                request.getOrderName(),
                request.getPaymentMethod(),
                request.getAmount(),
                request.getMembershipName(),
                request.getMembershipType(),
                request.getMembershipTotalCount(),
                request.getMembershipStartDate(),
                request.getMembershipEndDate()
        );

        return paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public Payment get(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    @Transactional
    public Payment complete(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.READY) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "READY 상태의 결제만 완료 처리할 수 있습니다.");
        }

        Membership membership = Membership.create(
                payment.getStudioId(),
                payment.getMemberId(),
                payment.getMembershipName(),
                payment.getMembershipType(),
                payment.getMembershipTotalCount(),
                payment.getMembershipStartDate(),
                payment.getMembershipEndDate()
        );

        Membership savedMembership = membershipRepository.save(membership);
        payment.complete(savedMembership.getId());

        domainEventPublisher.publish(
                new PaymentCompletedEvent(
                        payment.getId(),
                        payment.getStudioId(),
                        payment.getMemberId(),
                        savedMembership.getId(),
                        payment.getAmount()
                )
        );

        return payment;
    }

    @Transactional
    public Payment cancel(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    "완료된 결제는 cancel API로 취소할 수 없습니다. refund API를 사용하세요."
            );
        }

        if (payment.getStatus() == PaymentStatus.CANCELED) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_CANCELED);
        }

        payment.cancel();
        return payment;
    }

    @Transactional
    public RefundResponse refund(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.PAYMENT_REFUND_NOT_ALLOWED, "완료된 결제만 환불할 수 있습니다.");
        }

        if (payment.getMembershipId() == null) {
            throw new BusinessException(ErrorCode.PAYMENT_REFUND_NOT_ALLOWED, "연결된 이용권이 없는 결제입니다.");
        }

        Membership membership = membershipRepository.findById(payment.getMembershipId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBERSHIP_NOT_FOUND));

        if (membership.isRefunded()) {
            throw new BusinessException(ErrorCode.MEMBERSHIP_REFUNDED);
        }

        long refundAmount = calculateRefundAmount(payment, membership);

        List<Booking> refundTargetBookings = bookingRepository.findRefundTargetBookings(
                payment.getMembershipId(),
                LocalDateTime.now()
        );

        for (Booking booking : refundTargetBookings) {
            booking.cancel("payment refunded");
        }

        int canceledBookingCount = refundTargetBookings.size();

        membership.refund();
        payment.cancel();

        domainEventPublisher.publish(
                new PaymentCanceledEvent(
                        payment.getId(),
                        payment.getStudioId(),
                        payment.getMemberId(),
                        payment.getMembershipId(),
                        refundAmount
                )
        );

        return new RefundResponse(
                payment.getId(),
                payment.getMemberId(),
                payment.getMembershipId(),
                refundAmount,
                canceledBookingCount,
                payment.getStatus().name(),
                membership.getStatus().name()
        );
    }

    private long calculateRefundAmount(Payment payment, Membership membership) {
        if (membership.getType() == MembershipType.COUNT) {
            return calculateCountMembershipRefundAmount(payment, membership);
        }

        if (membership.getType() == MembershipType.PERIOD) {
            return calculatePeriodMembershipRefundAmount(payment, membership);
        }

        throw new BusinessException(ErrorCode.PAYMENT_REFUND_NOT_ALLOWED, "지원하지 않는 이용권 유형입니다.");
    }

    private long calculateCountMembershipRefundAmount(Payment payment, Membership membership) {
        Integer totalCount = membership.getTotalCount();
        Integer remainingCount = membership.getRemainingCount();

        if (totalCount == null || totalCount <= 0) {
            throw new BusinessException(ErrorCode.PAYMENT_REFUND_NOT_ALLOWED, "횟수권 총 횟수가 올바르지 않습니다.");
        }

        if (remainingCount == null || remainingCount <= 0) {
            throw new BusinessException(ErrorCode.PAYMENT_REFUND_NOT_ALLOWED, "잔여 횟수가 없어 환불할 수 없습니다.");
        }

        long unitPrice = payment.getAmount() / totalCount;
        return unitPrice * remainingCount;
    }

    private long calculatePeriodMembershipRefundAmount(Payment payment, Membership membership) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = membership.getStartDate();
        LocalDate endDate = membership.getEndDate();

        if (today.isAfter(endDate)) {
            throw new BusinessException(ErrorCode.PAYMENT_REFUND_NOT_ALLOWED, "만료된 기간권은 환불할 수 없습니다.");
        }

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (totalDays <= 0) {
            throw new BusinessException(ErrorCode.PAYMENT_REFUND_NOT_ALLOWED, "기간권 일수 계산이 올바르지 않습니다.");
        }

        if (today.isBefore(startDate)) {
            return payment.getAmount();
        }

        long remainingDays = ChronoUnit.DAYS.between(today, endDate) + 1;
        if (remainingDays <= 0) {
            throw new BusinessException(ErrorCode.PAYMENT_REFUND_NOT_ALLOWED, "남은 이용 기간이 없어 환불할 수 없습니다.");
        }

        return (payment.getAmount() * remainingDays) / totalDays;
    }
}