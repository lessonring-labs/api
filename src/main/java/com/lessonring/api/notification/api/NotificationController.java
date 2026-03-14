package com.lessonring.api.notification.api;

import com.lessonring.api.common.response.ApiResponse;
import com.lessonring.api.notification.application.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<Void> getNotifications() {
        // TODO: 알림 목록 조회 구현
        return ApiResponse.success();
    }
}
