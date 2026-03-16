package com.lessonring.api.notification.api;

import com.lessonring.api.common.response.ApiResponse;
import com.lessonring.api.notification.api.response.NotificationResponse;
import com.lessonring.api.notification.application.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getAll(@RequestParam Long memberId) {
        List<NotificationResponse> responses = notificationService.getAllByMemberId(memberId)
                .stream()
                .map(NotificationResponse::new)
                .toList();

        return ApiResponse.success(responses);
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<NotificationResponse> read(@PathVariable Long id) {
        return ApiResponse.success(new NotificationResponse(notificationService.read(id)));
    }
}