package com.lessonring.api.attendance.infrastructure.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AttendanceQueryRepository {

    private final JPAQueryFactory queryFactory;
}
