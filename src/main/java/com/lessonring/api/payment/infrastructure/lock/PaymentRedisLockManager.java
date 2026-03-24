package com.lessonring.api.payment.infrastructure.lock;

import com.lessonring.api.common.lock.RedisLockManager;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentRedisLockManager {

    private static final String PAYMENT_REFUND_LOCK_PREFIX = "payment:refund:";
    private static final String PAYMENT_APPROVE_LOCK_PREFIX = "payment:approve:";

    private final RedisLockManager redisLockManager;

    public boolean tryRefundLock(Long paymentId, long timeout, TimeUnit unit) {
        return redisLockManager.tryLock(PAYMENT_REFUND_LOCK_PREFIX + paymentId, timeout, unit);
    }

    public void unlockRefund(Long paymentId) {
        redisLockManager.unlock(PAYMENT_REFUND_LOCK_PREFIX + paymentId);
    }

    public boolean tryApproveLock(Long paymentId, long timeout, TimeUnit unit) {
        return redisLockManager.tryLock(PAYMENT_APPROVE_LOCK_PREFIX + paymentId, timeout, unit);
    }

    public void unlockApprove(Long paymentId) {
        redisLockManager.unlock(PAYMENT_APPROVE_LOCK_PREFIX + paymentId);
    }
}