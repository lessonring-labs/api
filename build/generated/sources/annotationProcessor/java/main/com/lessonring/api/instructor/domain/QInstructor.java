package com.lessonring.api.instructor.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QInstructor is a Querydsl query type for Instructor
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInstructor extends EntityPathBase<Instructor> {

    private static final long serialVersionUID = 292505923L;

    public static final QInstructor instructor = new QInstructor("instructor");

    public final com.lessonring.api.common.entity.QBaseEntity _super = new com.lessonring.api.common.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final StringPath name = createString("name");

    public final StringPath phone = createString("phone");

    public final StringPath profileImageUrl = createString("profileImageUrl");

    public final EnumPath<InstructorStatus> status = createEnum("status", InstructorStatus.class);

    public final NumberPath<Long> studioId = createNumber("studioId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> updatedBy = _super.updatedBy;

    public QInstructor(String variable) {
        super(Instructor.class, forVariable(variable));
    }

    public QInstructor(Path<? extends Instructor> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInstructor(PathMetadata metadata) {
        super(Instructor.class, metadata);
    }

}

