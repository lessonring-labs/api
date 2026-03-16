package com.lessonring.api.booking.domain.repository;

import com.lessonring.api.booking.domain.Booking;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {

    Booking save(Booking booking);

    Optional<Booking> findById(Long id);

    List<Booking> findAll();

    boolean existsActiveBooking(Long memberId, Long scheduleId);
}