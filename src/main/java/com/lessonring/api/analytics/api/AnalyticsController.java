package com.lessonring.api.analytics.api;

import com.lessonring.api.analytics.application.AnalyticsService;
import com.lessonring.api.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping
    public ApiResponse<Void> getAnalytics() {
        // TODO: 통계 조회 구현
        return ApiResponse.success();
    }
}
