package com.lessonring.api.booking.infrastructure.persistence;

import com.lessonring.api.booking.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingJpaRepository extends JpaRepository<Booking, Long> {

    boolean existsByMemberIdAndScheduleId(Long memberId, Long scheduleId);
}