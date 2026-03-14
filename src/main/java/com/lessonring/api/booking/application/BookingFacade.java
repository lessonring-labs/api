package com.lessonring.api.booking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingFacade {

    private final BookingService bookingService;
    private final BookingValidator bookingValidator;
    private final BookingAppender bookingAppender;
    private final BookingCanceler bookingCanceler;
    private final BookingEventPublisher bookingEventPublisher;
}
