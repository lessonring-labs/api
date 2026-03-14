package com.lessonring.api.membership.infrastructure.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MembershipQueryRepository {

    private final JPAQueryFactory queryFactory;
}
