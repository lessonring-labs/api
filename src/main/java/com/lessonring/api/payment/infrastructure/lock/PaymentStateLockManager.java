package com.lessonring.api.payment.infrastructure.lock;

import com.lessonring.api.common.lock.RedisLockManager;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStateLockManager {

    private static final String PAYMENT_STATE_LOCK_PREFIX = "payment:state:";
    private static final long DEFAULT_TIMEOUT_SECONDS = 3L;

    private final RedisLockManager redisLockManager;

    public boolean tryLock(Long paymentId) {
        return redisLockManager.tryLock(
                PAYMENT_STATE_LOCK_PREFIX + paymentId,
                DEFAULT_TIMEOUT_SECONDS,
                TimeUnit.SECONDS
        );
    }

    public boolean tryLock(Long paymentId, long timeout, TimeUnit unit) {
        return redisLockManager.tryLock(
                PAYMENT_STATE_LOCK_PREFIX + paymentId,
                timeout,
                unit
        );
    }

    public void unlock(Long paymentId) {
        redisLockManager.unlock(PAYMENT_STATE_LOCK_PREFIX + paymentId);
    }
}