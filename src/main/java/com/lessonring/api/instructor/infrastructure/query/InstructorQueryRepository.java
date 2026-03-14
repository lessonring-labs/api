package com.lessonring.api.instructor.infrastructure.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class InstructorQueryRepository {

    private final JPAQueryFactory queryFactory;
}
