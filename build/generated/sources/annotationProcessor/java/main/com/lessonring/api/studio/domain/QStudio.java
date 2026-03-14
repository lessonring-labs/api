package com.lessonring.api.studio.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStudio is a Querydsl query type for Studio
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStudio extends EntityPathBase<Studio> {

    private static final long serialVersionUID = 388760949L;

    public static final QStudio studio = new QStudio("studio");

    public final com.lessonring.api.common.entity.QBaseEntity _super = new com.lessonring.api.common.entity.QBaseEntity(this);

    public final StringPath address = createString("address");

    public final StringPath businessNumber = createString("businessNumber");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    public final StringPath detailAddress = createString("detailAddress");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath phone = createString("phone");

    public final EnumPath<StudioStatus> status = createEnum("status", StudioStatus.class);

    public final StringPath timezone = createString("timezone");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> updatedBy = _super.updatedBy;

    public QStudio(String variable) {
        super(Studio.class, forVariable(variable));
    }

    public QStudio(Path<? extends Studio> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStudio(PathMetadata metadata) {
        super(Studio.class, metadata);
    }

}

