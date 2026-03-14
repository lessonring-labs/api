package com.lessonring.api.attendance.infrastructure.persistence;

import com.lessonring.api.attendance.domain.Attendance;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceJpaRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findAllByScheduleId(Long scheduleId);
}
