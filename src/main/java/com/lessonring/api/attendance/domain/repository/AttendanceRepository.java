package com.lessonring.api.attendance.domain.repository;

import com.lessonring.api.attendance.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByBookingId(Long bookingId);
}
