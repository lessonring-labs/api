package com.lessonring.api.common.lock;

import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class RedisLockManager {

    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        // TODO: Redis 기반 분산 락 구현
        return true;
    }

    public void unlock(String key) {
        // TODO: Redis 기반 분산 락 해제 구현
    }
}
