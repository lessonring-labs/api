package com.lessonring.api.studio.api;

import com.lessonring.api.common.response.ApiResponse;
import com.lessonring.api.studio.application.StudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/studios")
@RequiredArgsConstructor
public class StudioController {

    private final StudioService studioService;

    @GetMapping("/{id}")
    public ApiResponse<Void> getStudio(@PathVariable Long id) {
        // TODO: 스튜디오 조회 구현
        return ApiResponse.success();
    }
}
