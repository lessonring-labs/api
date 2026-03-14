package com.lessonring.api.booking.application;

import com.lessonring.api.common.event.DomainEvent;
import com.lessonring.api.common.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventPublisher {

    private final DomainEventPublisher domainEventPublisher;

    public void publish(DomainEvent event) {
        domainEventPublisher.publish(event);
    }
}
