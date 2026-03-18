package com.lessonring.api.notification.api;

import com.lessonring.api.common.response.ApiResponse;
import com.lessonring.api.notification.api.response.NotificationResponse;
import com.lessonring.api.notification.application.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notification", description = "알림 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "알림 목록 조회",
            description = "회원 ID로 해당 회원의 알림 목록을 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알림 목록 조회 성공")
    })
    @GetMapping
    public ApiResponse<List<NotificationResponse>> getAll(
            @Parameter(description = "회원 ID", example = "1", required = true)
            @RequestParam Long memberId
    ) {
        List<NotificationResponse> responses = notificationService.getAllByMemberId(memberId)
                .stream()
                .map(NotificationResponse::new)
                .toList();

        return ApiResponse.success(responses);
    }

    @Operation(
            summary = "알림 읽음 처리",
            description = "알림 ID로 특정 알림을 읽음 처리한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "알림 없음")
    })
    @PatchMapping("/{id}/read")
    public ApiResponse<NotificationResponse> read(
            @Parameter(description = "알림 ID", example = "1")
            @PathVariable Long id
    ) {
        return ApiResponse.success(new NotificationResponse(notificationService.read(id)));
    }
}