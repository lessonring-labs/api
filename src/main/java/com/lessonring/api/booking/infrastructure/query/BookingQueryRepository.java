package com.lessonring.api.booking.infrastructure.query;

import static com.lessonring.api.attendance.domain.QAttendance.attendance;
import static com.lessonring.api.booking.domain.QBooking.booking;
import static com.lessonring.api.schedule.domain.QSchedule.schedule;

import com.lessonring.api.booking.domain.Booking;
import com.lessonring.api.booking.domain.BookingStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
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
        BooleanBuilder builder = new BooleanBuilder()
                .and(booking.status.eq(BookingStatus.RESERVED))
                .and(schedule.endAt.lt(now))
                .and(
                        JPAExpressions
                                .selectOne()
                                .from(attendance)
                                .where(attendance.bookingId.eq(booking.id))
                                .notExists()
                );

        return queryFactory
                .selectFrom(booking)
                .join(schedule).on(schedule.id.eq(booking.scheduleId))
                .where(builder)
                .fetch();
    }
}
