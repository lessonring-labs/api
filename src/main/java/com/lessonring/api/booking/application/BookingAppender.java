package com.lessonring.api.booking.application;

import com.lessonring.api.booking.domain.Booking;
import com.lessonring.api.booking.domain.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingAppender {

    private final BookingRepository bookingRepository;

    public Booking append(Booking booking) {
        return bookingRepository.save(booking);
    }
}
