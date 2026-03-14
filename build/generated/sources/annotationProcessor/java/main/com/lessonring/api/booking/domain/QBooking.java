package com.lessonring.api.booking.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBooking is a Querydsl query type for Booking
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBooking extends EntityPathBase<Booking> {

    private static final long serialVersionUID = -1141053135L;

    public static final QBooking booking = new QBooking("booking");

    public final com.lessonring.api.common.entity.QBaseEntity _super = new com.lessonring.api.common.entity.QBaseEntity(this);

    public final DateTimePath<java.time.LocalDateTime> bookedAt = createDateTime("bookedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> canceledAt = createDateTime("canceledAt", java.time.LocalDateTime.class);

    public final StringPath cancelReason = createString("cancelReason");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    public final NumberPath<Long> membershipId = createNumber("membershipId", Long.class);

    public final NumberPath<Long> scheduleId = createNumber("scheduleId", Long.class);

    public final EnumPath<BookingStatus> status = createEnum("status", BookingStatus.class);

    public final NumberPath<Long> studioId = createNumber("studioId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> updatedBy = _super.updatedBy;

    public QBooking(String variable) {
        super(Booking.class, forVariable(variable));
    }

    public QBooking(Path<? extends Booking> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBooking(PathMetadata metadata) {
        super(Booking.class, metadata);
    }

}

