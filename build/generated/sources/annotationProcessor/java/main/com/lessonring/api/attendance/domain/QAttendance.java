package com.lessonring.api.attendance.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAttendance is a Querydsl query type for Attendance
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAttendance extends EntityPathBase<Attendance> {

    private static final long serialVersionUID = -1064209701L;

    public static final QAttendance attendance = new QAttendance("attendance");

    public final com.lessonring.api.common.entity.QBaseEntity _super = new com.lessonring.api.common.entity.QBaseEntity(this);

    public final NumberPath<Long> bookingId = createNumber("bookingId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> checkedAt = createDateTime("checkedAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    public final StringPath note = createString("note");

    public final NumberPath<Long> scheduleId = createNumber("scheduleId", Long.class);

    public final EnumPath<AttendanceStatus> status = createEnum("status", AttendanceStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> updatedBy = _super.updatedBy;

    public QAttendance(String variable) {
        super(Attendance.class, forVariable(variable));
    }

    public QAttendance(Path<? extends Attendance> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAttendance(PathMetadata metadata) {
        super(Attendance.class, metadata);
    }

}

