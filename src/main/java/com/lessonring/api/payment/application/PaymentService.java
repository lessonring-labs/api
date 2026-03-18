package com.lessonring.api.payment.application;

import com.lessonring.api.booking.domain.Booking;
import com.lessonring.api.booking.domain.repository.BookingRepository;
import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.common.event.DomainEventPublisher;
import com.lessonring.api.member.domain.repository.MemberRepository;
import com.lessonring.api.membership.domain.Membership;
import com.lessonring.api.membership.domain.repository.MembershipRepository;
import com.lessonring.api.payment.api.request.PaymentCreateRequest;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.PaymentStatus;
import com.lessonring.api.payment.domain.event.PaymentCanceledEvent;
import com.lessonring.api.payment.domain.event.PaymentCompletedEvent;
import com.lessonring.api.payment.domain.repository.PaymentRepository;
import java.time.LocalDateTime;
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
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
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
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
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
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "완료된 결제는 cancel API로 취소할 수 없습니다. refund API를 사용하세요.");
        }

        if (payment.getStatus() == PaymentStatus.CANCELED) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_CANCELED);
        }

        payment.cancel();
        return payment;
    }

    @Transactional
    public Payment refund(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "완료된 결제만 환불할 수 있습니다.");
        }

        if (payment.getMembershipId() == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "연결된 이용권이 없는 결제입니다.");
        }

        Membership membership = membershipRepository.findById(payment.getMembershipId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBERSHIP_NOT_FOUND));

        List<Booking> refundTargetBookings = bookingRepository.findRefundTargetBookings(
                payment.getMembershipId(),
                LocalDateTime.now()
        );

        for (Booking booking : refundTargetBookings) {
            booking.cancel("payment refunded");
        }

        membership.refund();
        payment.cancel();

        domainEventPublisher.publish(
                new PaymentCanceledEvent(
                        payment.getId(),
                        payment.getStudioId(),
                        payment.getMemberId(),
                        payment.getMembershipId(),
                        payment.getAmount()
                )
        );

        return payment;
    }
}