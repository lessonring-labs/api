package com.lessonring.api.payment.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPayment is a Querydsl query type for Payment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayment extends EntityPathBase<Payment> {

    private static final long serialVersionUID = 1039710609L;

    public static final QPayment payment = new QPayment("payment");

    public final com.lessonring.api.common.entity.QBaseEntity _super = new com.lessonring.api.common.entity.QBaseEntity(this);

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final DateTimePath<java.time.LocalDateTime> canceledAt = createDateTime("canceledAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    public final DatePath<java.time.LocalDate> membershipEndDate = createDate("membershipEndDate", java.time.LocalDate.class);

    public final NumberPath<Long> membershipId = createNumber("membershipId", Long.class);

    public final StringPath membershipName = createString("membershipName");

    public final DatePath<java.time.LocalDate> membershipStartDate = createDate("membershipStartDate", java.time.LocalDate.class);

    public final NumberPath<Integer> membershipTotalCount = createNumber("membershipTotalCount", Integer.class);

    public final EnumPath<com.lessonring.api.membership.domain.MembershipType> membershipType = createEnum("membershipType", com.lessonring.api.membership.domain.MembershipType.class);

    public final StringPath orderName = createString("orderName");

    public final DateTimePath<java.time.LocalDateTime> paidAt = createDateTime("paidAt", java.time.LocalDateTime.class);

    public final EnumPath<PaymentMethod> paymentMethod = createEnum("paymentMethod", PaymentMethod.class);

    public final EnumPath<PaymentStatus> status = createEnum("status", PaymentStatus.class);

    public final NumberPath<Long> studioId = createNumber("studioId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> updatedBy = _super.updatedBy;

    public QPayment(String variable) {
        super(Payment.class, forVariable(variable));
    }

    public QPayment(Path<? extends Payment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPayment(PathMetadata metadata) {
        super(Payment.class, metadata);
    }

}

