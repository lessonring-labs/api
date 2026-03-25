package com.lessonring.api.booking.domain.repository;

import com.lessonring.api.booking.domain.Booking;
import com.lessonring.api.booking.domain.BookingStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    default boolean existsActiveBooking(Long memberId, Long scheduleId) {
        return existsByMemberIdAndScheduleIdAndStatusNot(memberId, scheduleId, BookingStatus.CANCELED);
    }

    boolean existsByMemberIdAndScheduleIdAndStatusNot(Long memberId, Long scheduleId, BookingStatus status);

    @Query("""
            select b
            from Booking b
            join com.lessonring.api.schedule.domain.Schedule s on b.scheduleId = s.id
            where b.status = com.lessonring.api.booking.domain.BookingStatus.RESERVED
              and s.startAt < :now
            """)
    List<Booking> findNoShowTargets(@Param("now") LocalDateTime now);

    @Query("""
            select b
            from Booking b
            join com.lessonring.api.schedule.domain.Schedule s on b.scheduleId = s.id
            where b.membershipId = :membershipId
              and b.status = com.lessonring.api.booking.domain.BookingStatus.RESERVED
              and s.startAt > :now
            """)
    List<Booking> findRefundTargetBookings(@Param("membershipId") Long membershipId, @Param("now") LocalDateTime now);
}
