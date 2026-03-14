package com.lessonring.api.booking.infrastructure.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookingQueryRepository {

    private final JPAQueryFactory queryFactory;
}
