package com.lessonring.api.schedule.domain.repository;

import com.lessonring.api.schedule.domain.Schedule;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {

    Schedule save(Schedule schedule);

    Optional<Schedule> findById(Long id);

    List<Schedule> findAll();
}