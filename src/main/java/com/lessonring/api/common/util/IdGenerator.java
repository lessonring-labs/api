package com.lessonring.api.common.util;

import java.util.UUID;

public final class IdGenerator {

    private IdGenerator() {
    }

    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
