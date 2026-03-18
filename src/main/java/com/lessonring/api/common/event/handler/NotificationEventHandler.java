package com.lessonring.api.common.event.handler;

import com.lessonring.api.booking.domain.event.BookingCanceledEvent;
import com.lessonring.api.booking.domain.event.BookingCreatedEvent;
import com.lessonring.api.membership.domain.event.MembershipUsedEvent;
import com.lessonring.api.notification.domain.Notification;
import com.lessonring.api.notification.domain.repository.NotificationRepository;
import com.lessonring.api.payment.domain.event.PaymentCanceledEvent;
import com.lessonring.api.payment.domain.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationRepository notificationRepository;

    @EventListener
    public void handle(BookingCreatedEvent event) {
        notificationRepository.save(
                Notification.create(
                        event.getStudioId(),
                        event.getMemberId(),
                        "예약 완료",
                        "예약이 완료되었습니다. bookingId=" + event.getBookingId(),
                        "BOOKING_CREATED"
                )
        );
    }

    @EventListener
    public void handle(BookingCanceledEvent event) {
        notificationRepository.save(
                Notification.create(
                        event.getStudioId(),
                        event.getMemberId(),
                        "예약 취소",
                        "예약이 취소되었습니다. bookingId=" + event.getBookingId(),
                        "BOOKING_CANCELED"
                )
        );
    }

    @EventListener
    public void handle(PaymentCompletedEvent event) {
        notificationRepository.save(
                Notification.create(
                        event.getStudioId(),
                        event.getMemberId(),
                        "결제 완료",
                        "결제가 완료되었습니다. paymentId=" + event.getPaymentId(),
                        "PAYMENT_COMPLETED"
                )
        );
    }

    @EventListener
    public void handle(PaymentCanceledEvent event) {
        notificationRepository.save(
                Notification.create(
                        event.getStudioId(),
                        event.getMemberId(),
                        "결제 환불 완료",
                        "결제가 환불되었습니다. paymentId=" + event.getPaymentId(),
                        "PAYMENT_CANCELED"
                )
        );
    }

    @EventListener
    public void handle(MembershipUsedEvent event) {
        notificationRepository.save(
                Notification.create(
                        event.getStudioId(),
                        event.getMemberId(),
                        "이용권 사용",
                        "이용권이 사용되었습니다. remainingCount=" + event.getRemainingCount(),
                        "MEMBERSHIP_USED"
                )
        );
    }
}