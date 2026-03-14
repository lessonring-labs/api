package com.lessonring.api.attendance.domain.repository;

import com.lessonring.api.attendance.domain.Attendance;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository {

    Optional<Attendance> findById(Long id);

    Attendance save(Attendance attendance);

    List<Attendance> findAllByScheduleId(Long scheduleId);
}
