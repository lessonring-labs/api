package com.lessonring.api.booking.infrastructure.query;

import static com.lessonring.api.booking.domain.QBooking.booking;
import static com.lessonring.api.schedule.domain.QSchedule.schedule;

import com.lessonring.api.booking.domain.Booking;
import com.lessonring.api.booking.domain.BookingStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookingQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<Booking> findNoShowTargets(LocalDateTime now) {
        return queryFactory
                .selectFrom(booking)
                .join(schedule).on(booking.scheduleId.eq(schedule.id))
                .where(
                        booking.status.eq(BookingStatus.RESERVED),
                        schedule.startAt.before(now)
                )
                .fetch();
    }

    public List<Booking> findRefundTargetBookings(Long membershipId, LocalDateTime now) {
        return queryFactory
                .selectFrom(booking)
                .join(schedule).on(booking.scheduleId.eq(schedule.id))
                .where(
                        booking.membershipId.eq(membershipId),
                        booking.status.eq(BookingStatus.RESERVED),
                        schedule.startAt.after(now)
                )
                .fetch();
    }
}