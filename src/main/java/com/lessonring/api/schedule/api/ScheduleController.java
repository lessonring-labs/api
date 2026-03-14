package com.lessonring.api.schedule.api;

import com.lessonring.api.common.response.ApiResponse;
import com.lessonring.api.schedule.api.request.ScheduleCreateRequest;
import com.lessonring.api.schedule.api.response.ScheduleResponse;
import com.lessonring.api.schedule.application.ScheduleService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ApiResponse<ScheduleResponse> create(@Valid @RequestBody ScheduleCreateRequest request) {
        return ApiResponse.success(new ScheduleResponse(scheduleService.create(request)));
    }

    @GetMapping("/{id}")
    public ApiResponse<ScheduleResponse> get(@PathVariable Long id) {
        return ApiResponse.success(new ScheduleResponse(scheduleService.get(id)));
    }

    @GetMapping
    public ApiResponse<List<ScheduleResponse>> getAll() {
        List<ScheduleResponse> responses = scheduleService.getAll()
                .stream()
                .map(ScheduleResponse::new)
                .toList();

        return ApiResponse.success(responses);
    }
}