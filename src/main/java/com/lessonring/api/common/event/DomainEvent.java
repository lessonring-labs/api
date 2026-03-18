package com.lessonring.api.common.event;

import lombok.Getter;

@Getter
public abstract class DomainEvent {

    private final Long aggregateId;
    private final String eventType;

    protected DomainEvent(Long aggregateId, String eventType) {
        this.aggregateId = aggregateId;
        this.eventType = eventType;
    }
}