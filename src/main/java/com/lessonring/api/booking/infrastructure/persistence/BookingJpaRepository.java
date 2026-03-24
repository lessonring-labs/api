package com.lessonring.api.booking.infrastructure.persistence;

import com.lessonring.api.booking.domain.Booking;
import com.lessonring.api.booking.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingJpaRepository extends JpaRepository<Booking, Long> {

    boolean existsByMemberIdAndScheduleIdAndStatusNot(Long memberId, Long scheduleId, BookingStatus status);

    @Override
    Optional<Booking> findById(Long aLong);
}
