package com.lessonring.api.analytics.infrastructure.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AnalyticsQueryRepository {

    private final JPAQueryFactory queryFactory;
}
