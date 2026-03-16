package com.lessonring.api.notification.domain;

import com.lessonring.api.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studioId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String title;

    private String content;

    private String type;

    private LocalDateTime readAt;

    private Notification(
            Long studioId,
            Long memberId,
            String title,
            String content,
            String type
    ) {
        this.studioId = studioId;
        this.memberId = memberId;
        this.title = title;
        this.content = content;
        this.type = type;
    }

    public static Notification create(
            Long studioId,
            Long memberId,
            String title,
            String content,
            String type
    ) {
        return new Notification(studioId, memberId, title, content, type);
    }

    public void read() {
        this.readAt = LocalDateTime.now();
    }
}