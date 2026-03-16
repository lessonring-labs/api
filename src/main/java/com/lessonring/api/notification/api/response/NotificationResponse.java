package com.lessonring.api.notification.api.response;

import com.lessonring.api.notification.domain.Notification;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationResponse {

    private final Long id;
    private final Long memberId;
    private final String title;
    private final String content;
    private final String type;
    private final LocalDateTime readAt;

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.memberId = notification.getMemberId();
        this.title = notification.getTitle();
        this.content = notification.getContent();
        this.type = notification.getType();
        this.readAt = notification.getReadAt();
    }
}