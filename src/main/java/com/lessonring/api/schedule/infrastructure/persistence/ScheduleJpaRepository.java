package com.lessonring.api.schedule.infrastructure.persistence;

import com.lessonring.api.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleJpaRepository extends JpaRepository<Schedule, Long> {
}