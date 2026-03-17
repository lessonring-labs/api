package com.lessonring.api.attendance.infrastructure.persistence;

import com.lessonring.api.attendance.domain.Attendance;
import com.lessonring.api.attendance.domain.repository.AttendanceRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AttendanceRepositoryImpl implements AttendanceRepository {

    private final AttendanceJpaRepository attendanceJpaRepository;

    @Override
    public Attendance save(Attendance attendance) {
        return attendanceJpaRepository.save(attendance);
    }

    @Override
    public Optional<Attendance> findById(Long id) {
        return attendanceJpaRepository.findById(id);
    }

    @Override
    public List<Attendance> findAll() {
        return attendanceJpaRepository.findAll();
    }

    @Override
    public boolean existsByBookingId(Long bookingId) {
        return attendanceJpaRepository.existsByBookingId(bookingId);
    }

    @Override
    public void delete(Attendance attendance) {
        attendanceJpaRepository.delete(attendance);
    }
}