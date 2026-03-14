package com.lessonring.api.schedule.domain;

import com.lessonring.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studioId;

    @Column(nullable = false)
    private Long instructorId;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleType type;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer bookedCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleStatus status;

    private Schedule(
            Long studioId,
            Long instructorId,
            String title,
            ScheduleType type,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Integer capacity,
            Integer bookedCount,
            ScheduleStatus status
    ) {
        this.studioId = studioId;
        this.instructorId = instructorId;
        this.title = title;
        this.type = type;
        this.startAt = startAt;
        this.endAt = endAt;
        this.capacity = capacity;
        this.bookedCount = bookedCount;
        this.status = status;
    }

    public static Schedule create(
            Long studioId,
            Long instructorId,
            String title,
            ScheduleType type,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Integer capacity
    ) {
        return new Schedule(
                studioId,
                instructorId,
                title,
                type,
                startAt,
                endAt,
                capacity,
                0,
                ScheduleStatus.OPEN
        );
    }
}