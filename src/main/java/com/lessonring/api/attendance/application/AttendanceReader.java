package com.lessonring.api.attendance.application;

import com.lessonring.api.attendance.domain.Attendance;
import com.lessonring.api.attendance.domain.repository.AttendanceRepository;
import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttendanceReader {

    private final AttendanceRepository attendanceRepository;

    public Attendance getAttendance(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
    }
}
