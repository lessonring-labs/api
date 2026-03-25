package com.lessonring.api.schedule.domain.repository;

import com.lessonring.api.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
