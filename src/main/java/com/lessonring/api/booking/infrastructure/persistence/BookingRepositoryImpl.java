package com.lessonring.api.booking.infrastructure.persistence;

import com.lessonring.api.booking.domain.Booking;
import com.lessonring.api.booking.domain.BookingStatus;
import com.lessonring.api.booking.domain.repository.BookingRepository;
import com.lessonring.api.booking.infrastructure.query.BookingQueryRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookingRepositoryImpl implements BookingRepository {

    private final BookingJpaRepository bookingJpaRepository;
    private final BookingQueryRepository bookingQueryRepository;

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
    public boolean existsActiveBooking(Long memberId, Long scheduleId) {
        return bookingJpaRepository.existsByMemberIdAndScheduleIdAndStatusNot(
                memberId,
                scheduleId,
                BookingStatus.CANCELED
        );
    }

    @Override
    public List<Booking> findNoShowTargets(LocalDateTime now) {
        return bookingQueryRepository.findNoShowTargets(now);
    }
}
