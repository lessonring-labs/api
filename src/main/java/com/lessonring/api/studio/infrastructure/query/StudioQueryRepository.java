package com.lessonring.api.studio.infrastructure.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StudioQueryRepository {

    private final JPAQueryFactory queryFactory;
}
