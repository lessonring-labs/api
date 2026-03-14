package com.lessonring.api.payment.infrastructure.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentQueryRepository {

    private final JPAQueryFactory queryFactory;
}
