package com.lessonring.api.common.lock;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisLockManager {

    private final RedissonClient redissonClient;

    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        RLock lock = redissonClient.getLock(key);

        try {
            return lock.tryLock(timeout, timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("락 획득 중 인터럽트가 발생했습니다. key=" + key, e);
        }
    }

    public void unlock(String key) {
        RLock lock = redissonClient.getLock(key);

        try {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        } catch (IllegalMonitorStateException ignored) {
        }
    }
}