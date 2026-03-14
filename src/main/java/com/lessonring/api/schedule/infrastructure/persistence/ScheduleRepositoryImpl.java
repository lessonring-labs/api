package com.lessonring.api.schedule.infrastructure.persistence;

import com.lessonring.api.schedule.domain.Schedule;
import com.lessonring.api.schedule.domain.repository.ScheduleRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepository {

    private final ScheduleJpaRepository scheduleJpaRepository;

    @Override
    public Schedule save(Schedule schedule) {
        return scheduleJpaRepository.save(schedule);
    }

    @Override
    public Optional<Schedule> findById(Long id) {
        return scheduleJpaRepository.findById(id);
    }

    @Override
    public List<Schedule> findAll() {
        return scheduleJpaRepository.findAll();
    }
}