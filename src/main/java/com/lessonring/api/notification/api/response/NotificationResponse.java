package com.lessonring.api.notification.api.response;

import com.lessonring.api.notification.domain.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@Schema(description = "알림 응답")
public class NotificationResponse {

    @Schema(description = "알림 ID", example = "1")
    private final Long id;

    @Schema(description = "회원 ID", example = "1")
    private final Long memberId;

    @Schema(description = "알림 제목", example = "예약 완료")
    private final String title;

    @Schema(description = "알림 내용", example = "필라테스 입문 수업 예약이 완료되었습니다.")
    private final String content;

    @Schema(description = "알림 유형", example = "BOOKING_CREATED")
    private final String type;

    @Schema(description = "읽음 시각", example = "2026-03-18T13:10:00", nullable = true)
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