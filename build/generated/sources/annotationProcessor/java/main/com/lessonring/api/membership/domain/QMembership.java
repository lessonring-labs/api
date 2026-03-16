package com.lessonring.api.membership.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMembership is a Querydsl query type for Membership
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMembership extends EntityPathBase<Membership> {

    private static final long serialVersionUID = -189252747L;

    public static final QMembership membership = new QMembership("membership");

    public final com.lessonring.api.common.entity.QBaseEntity _super = new com.lessonring.api.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> remainingCount = createNumber("remainingCount", Integer.class);

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final EnumPath<MembershipStatus> status = createEnum("status", MembershipStatus.class);

    public final NumberPath<Long> studioId = createNumber("studioId", Long.class);

    public final NumberPath<Integer> totalCount = createNumber("totalCount", Integer.class);

    public final EnumPath<MembershipType> type = createEnum("type", MembershipType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> updatedBy = _super.updatedBy;

    public QMembership(String variable) {
        super(Membership.class, forVariable(variable));
    }

    public QMembership(Path<? extends Membership> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMembership(PathMetadata metadata) {
        super(Membership.class, metadata);
    }

}

