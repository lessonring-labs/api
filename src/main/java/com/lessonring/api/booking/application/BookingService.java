package com.lessonring.api.booking.application;

import com.lessonring.api.attendance.domain.repository.AttendanceRepository;
import com.lessonring.api.booking.api.request.BookingCreateRequest;
import com.lessonring.api.booking.domain.Booking;
import com.lessonring.api.booking.domain.BookingStatus;
import com.lessonring.api.booking.domain.event.BookingCanceledEvent;
import com.lessonring.api.booking.domain.event.BookingCreatedEvent;
import com.lessonring.api.booking.domain.repository.BookingRepository;
import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.common.event.DomainEventPublisher;
import com.lessonring.api.member.domain.repository.MemberRepository;
import com.lessonring.api.membership.domain.Membership;
import com.lessonring.api.membership.domain.MembershipType;
import com.lessonring.api.membership.domain.event.MembershipUsedEvent;
import com.lessonring.api.membership.domain.repository.MembershipRepository;
import com.lessonring.api.schedule.domain.Schedule;
import com.lessonring.api.schedule.domain.ScheduleStatus;
import com.lessonring.api.schedule.domain.repository.ScheduleRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final MembershipRepository membershipRepository;
    private final AttendanceRepository attendanceRepository;

    private final DomainEventPublisher domainEventPublisher;

    @Transactional
    public Booking createWithLock(BookingCreateRequest request) {
        memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (request.getMembershipId() == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        Membership membership = membershipRepository.findById(request.getMembershipId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (!membership.getMemberId().equals(request.getMemberId())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (!membership.getStudioId().equals(request.getStudioId())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (!membership.isAvailable()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (schedule.getStatus() != ScheduleStatus.OPEN) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (!schedule.getStartAt().isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (schedule.getBookedCount() >= schedule.getCapacity()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (bookingRepository.existsActiveBooking(
                request.getMemberId(),
                request.getScheduleId()
        )) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        schedule.increaseBookedCount();

        Booking booking = Booking.create(
                request.getStudioId(),
                request.getMemberId(),
                request.getScheduleId(),
                request.getMembershipId()
        );

        Booking savedBooking = bookingRepository.save(booking);

        domainEventPublisher.publish(
                new BookingCreatedEvent(
                        savedBooking.getId(),
                        savedBooking.getStudioId(),
                        savedBooking.getMemberId(),
                        savedBooking.getScheduleId(),
                        savedBooking.getMembershipId()
                )
        );

        return savedBooking;
    }

    @Transactional(readOnly = true)
    public Booking get(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    @Transactional
    public Booking cancelWithLock(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        Schedule schedule = scheduleRepository.findById(booking.getScheduleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        booking.cancel("user canceled");
        schedule.decreaseBookedCount();

        domainEventPublisher.publish(
                new BookingCanceledEvent(
                        booking.getId(),
                        booking.getStudioId(),
                        booking.getMemberId(),
                        booking.getScheduleId(),
                        booking.getMembershipId(),
                        booking.getCancelReason()
                )
        );

        return booking;
    }

    @Transactional(readOnly = true)
    public Long getScheduleIdForLock(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        return booking.getScheduleId();
    }

    @Transactional
    public Booking markNoShow(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (booking.getStatus() != BookingStatus.RESERVED) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (attendanceRepository.existsByBookingId(bookingId)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        Schedule schedule = scheduleRepository.findById(booking.getScheduleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (schedule.getEndAt().isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        Membership membership = membershipRepository.findById(booking.getMembershipId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        booking.markNoShow();

        if (membership.getType() == MembershipType.COUNT) {
            membership.useOnce(LocalDate.now());

            domainEventPublisher.publish(
                    new MembershipUsedEvent(
                            membership.getId(),
                            membership.getStudioId(),
                            membership.getMemberId(),
                            membership.getRemainingCount()
                    )
            );
        }

        return booking;
    }

    @Transactional
    public int markNoShowTargets() {
        List<Booking> targets = bookingRepository.findNoShowTargets(LocalDateTime.now());
        int processedCount = 0;

        for (Booking booking : targets) {
            try {
                markNoShow(booking.getId());
                processedCount++;
            } catch (Exception e) {
                // 1차 구현에서는 개별 실패가 전체 배치를 중단시키지 않도록 계속 진행
            }
        }

        return processedCount;
    }
}