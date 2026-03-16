package com.lessonring.api.attendance.infrastructure.persistence;

import com.lessonring.api.attendance.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceJpaRepository extends JpaRepository<Attendance, Long> {

    boolean existsByBookingId(Long bookingId);
}