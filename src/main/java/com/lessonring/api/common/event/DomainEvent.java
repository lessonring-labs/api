package com.lessonring.api.common.event;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public abstract class DomainEvent {

    private final String eventId;
    private final LocalDateTime occurredAt;
    private final String eventType;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = LocalDateTime.now();
        this.eventType = this.getClass().getSimpleName();
    }
}
