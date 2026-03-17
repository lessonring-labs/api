package com.lessonring.api.attendance.domain.repository;

import com.lessonring.api.attendance.domain.Attendance;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository {

    Attendance save(Attendance attendance);

    Optional<Attendance> findById(Long id);

    List<Attendance> findAll();

    boolean existsByBookingId(Long bookingId);

    void delete(Attendance attendance);
}