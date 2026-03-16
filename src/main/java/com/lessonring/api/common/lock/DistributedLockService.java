package com.lessonring.api.common.lock;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DistributedLockService {

    private final RedissonClient redissonClient;

    public <T> T executeWithLock(
            String key,
            long waitTime,
            long leaseTime,
            TimeUnit timeUnit,
            LockCallback<T> callback
    ) {
        RLock lock = redissonClient.getLock(key);

        boolean available = false;
        try {
            available = lock.tryLock(waitTime, leaseTime, timeUnit);

            if (!available) {
                throw new IllegalStateException("락 획득에 실패했습니다. key=" + key);
            }

            return callback.execute();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("락 대기 중 인터럽트가 발생했습니다. key=" + key, e);
        } finally {
            if (available && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @FunctionalInterface
    public interface LockCallback<T> {
        T execute();
    }
}