package com.lessonring.api.common.lock;

public final class LockConstants {

    private LockConstants() {
    }

    public static final long BOOKING_WAIT_TIME_SECONDS = 3L;
    public static final long BOOKING_LEASE_TIME_SECONDS = 5L;

    public static String bookingScheduleKey(Long scheduleId) {
        return "booking:schedule:" + scheduleId;
    }
}