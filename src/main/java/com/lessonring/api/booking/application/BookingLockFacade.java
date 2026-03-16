package com.lessonring.api.booking.application;

import com.lessonring.api.booking.api.request.BookingCreateRequest;
import com.lessonring.api.booking.domain.Booking;
import com.lessonring.api.common.lock.DistributedLockService;
import com.lessonring.api.common.lock.LockConstants;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingLockFacade {

    private final DistributedLockService distributedLockService;
    private final BookingService bookingService;

    public Booking create(BookingCreateRequest request) {
        String lockKey = LockConstants.bookingScheduleKey(request.getScheduleId());

        return distributedLockService.executeWithLock(
                lockKey,
                LockConstants.BOOKING_WAIT_TIME_SECONDS,
                LockConstants.BOOKING_LEASE_TIME_SECONDS,
                TimeUnit.SECONDS,
                () -> bookingService.createWithLock(request)
        );
    }

    public Booking cancel(Long bookingId) {
        Long scheduleId = bookingService.getScheduleIdForLock(bookingId);
        String lockKey = LockConstants.bookingScheduleKey(scheduleId);

        return distributedLockService.executeWithLock(
                lockKey,
                LockConstants.BOOKING_WAIT_TIME_SECONDS,
                LockConstants.BOOKING_LEASE_TIME_SECONDS,
                TimeUnit.SECONDS,
                () -> bookingService.cancelWithLock(bookingId)
        );
    }
}