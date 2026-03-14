package com.lessonring.api.booking.application;

import com.lessonring.api.booking.api.request.BookingCreateRequest;
import com.lessonring.api.booking.domain.Booking;
import com.lessonring.api.booking.domain.repository.BookingRepository;
import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.member.domain.repository.MemberRepository;
import com.lessonring.api.membership.domain.Membership;
import com.lessonring.api.membership.domain.repository.MembershipRepository;
import com.lessonring.api.schedule.domain.Schedule;
import com.lessonring.api.schedule.domain.ScheduleStatus;
import com.lessonring.api.schedule.domain.repository.ScheduleRepository;
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

    @Transactional
    public Booking create(BookingCreateRequest request) {
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

        if (bookingRepository.existsByMemberIdAndScheduleId(
                request.getMemberId(),
                request.getScheduleId()
        )) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        Booking booking = Booking.create(
                request.getStudioId(),
                request.getMemberId(),
                request.getScheduleId(),
                request.getMembershipId()
        );

        return bookingRepository.save(booking);
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
    public Booking cancel(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        booking.cancel("user canceled");

        return booking;
    }
}