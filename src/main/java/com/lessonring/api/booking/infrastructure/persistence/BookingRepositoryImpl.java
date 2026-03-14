package com.lessonring.api.booking.infrastructure.persistence;

import com.lessonring.api.booking.domain.Booking;
import com.lessonring.api.booking.domain.repository.BookingRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookingRepositoryImpl implements BookingRepository {

    private final BookingJpaRepository bookingJpaRepository;

    @Override
    public Booking save(Booking booking) {
        return bookingJpaRepository.save(booking);
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return bookingJpaRepository.findById(id);
    }

    @Override
    public List<Booking> findAll() {
        return bookingJpaRepository.findAll();
    }

    @Override
    public boolean existsByMemberIdAndScheduleId(Long memberId, Long scheduleId) {
        return bookingJpaRepository.existsByMemberIdAndScheduleId(memberId, scheduleId);
    }
}