package com.lessonring.api.schedule.application;

import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.schedule.api.request.ScheduleCreateRequest;
import com.lessonring.api.schedule.domain.Schedule;
import com.lessonring.api.schedule.domain.repository.ScheduleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Transactional
    public Schedule create(ScheduleCreateRequest request) {
        if (!request.getStartAt().isBefore(request.getEndAt())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        Schedule schedule = Schedule.create(
                request.getStudioId(),
                request.getInstructorId(),
                request.getTitle(),
                request.getType(),
                request.getStartAt(),
                request.getEndAt(),
                request.getCapacity()
        );

        return scheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    public Schedule get(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Schedule> getAll() {
        return scheduleRepository.findAll();
    }
}