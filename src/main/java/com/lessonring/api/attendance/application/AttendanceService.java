package com.lessonring.api.attendance.application;

import com.lessonring.api.attendance.api.request.AttendanceCreateRequest;
import com.lessonring.api.attendance.domain.Attendance;
import com.lessonring.api.attendance.domain.AttendanceStatus;
import com.lessonring.api.attendance.domain.repository.AttendanceRepository;
import com.lessonring.api.booking.domain.Booking;
import com.lessonring.api.booking.domain.BookingStatus;
import com.lessonring.api.booking.domain.repository.BookingRepository;
import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.common.event.DomainEventPublisher;
import com.lessonring.api.membership.domain.Membership;
import com.lessonring.api.membership.domain.MembershipType;
import com.lessonring.api.membership.domain.event.MembershipUsedEvent;
import com.lessonring.api.membership.domain.repository.MembershipRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final BookingRepository bookingRepository;
    private final MembershipRepository membershipRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Transactional
    public Attendance create(AttendanceCreateRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (booking.getStatus() == BookingStatus.ATTENDED) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (booking.getStatus() == BookingStatus.NO_SHOW) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (attendanceRepository.existsByBookingId(request.getBookingId())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        Membership membership = membershipRepository.findById(booking.getMembershipId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        membership.useOnce(LocalDate.now());
        booking.attend();

        Attendance attendance = Attendance.create(
                booking.getId(),
                booking.getMemberId(),
                booking.getScheduleId(),
                AttendanceStatus.ATTENDED,
                request.getNote()
        );

        Attendance savedAttendance = attendanceRepository.save(attendance);

        domainEventPublisher.publish(
                new MembershipUsedEvent(
                        membership.getId(),
                        membership.getStudioId(),
                        membership.getMemberId(),
                        membership.getRemainingCount()
                )
        );

        return savedAttendance;
    }

    @Transactional(readOnly = true)
    public Attendance get(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Attendance> getAll() {
        return attendanceRepository.findAll();
    }

    @Transactional
    public void cancel(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        Booking booking = bookingRepository.findById(attendance.getBookingId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (booking.getStatus() != BookingStatus.ATTENDED) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        Membership membership = membershipRepository.findById(booking.getMembershipId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        booking.revertToReserved();

        if (membership.getType() == MembershipType.COUNT) {
            membership.restoreOnce();
        }

        attendanceRepository.delete(attendance);
    }
}