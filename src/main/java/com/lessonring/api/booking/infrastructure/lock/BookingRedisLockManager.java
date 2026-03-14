package com.lessonring.api.booking.infrastructure.lock;

import com.lessonring.api.common.lock.RedisLockManager;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingRedisLockManager {

    private static final String BOOKING_LOCK_PREFIX = "booking:lock:";

    private final RedisLockManager redisLockManager;

    public boolean tryLock(Long scheduleId, long timeout, TimeUnit unit) {
        return redisLockManager.tryLock(BOOKING_LOCK_PREFIX + scheduleId, timeout, unit);
    }

    public void unlock(Long scheduleId) {
        redisLockManager.unlock(BOOKING_LOCK_PREFIX + scheduleId);
    }
}
