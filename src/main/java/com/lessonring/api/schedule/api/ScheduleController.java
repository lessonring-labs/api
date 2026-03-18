package com.lessonring.api.schedule.api;

import com.lessonring.api.common.response.ApiResponse;
import com.lessonring.api.schedule.api.request.ScheduleCreateRequest;
import com.lessonring.api.schedule.api.response.ScheduleResponse;
import com.lessonring.api.schedule.application.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Schedule", description = "수업 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(
            summary = "수업 생성",
            description = "새 수업 일정을 생성한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수업 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ApiResponse<ScheduleResponse> create(@Valid @RequestBody ScheduleCreateRequest request) {
        return ApiResponse.success(new ScheduleResponse(scheduleService.create(request)));
    }

    @Operation(
            summary = "수업 단건 조회",
            description = "수업 ID로 수업 정보를 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수업 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "수업 없음")
    })
    @GetMapping("/{id}")
    public ApiResponse<ScheduleResponse> get(
            @Parameter(description = "수업 ID", example = "1")
            @PathVariable Long id
    ) {
        return ApiResponse.success(new ScheduleResponse(scheduleService.get(id)));
    }

    @Operation(
            summary = "수업 목록 조회",
            description = "전체 수업 목록을 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수업 목록 조회 성공")
    })
    @GetMapping
    public ApiResponse<List<ScheduleResponse>> getAll() {
        List<ScheduleResponse> responses = scheduleService.getAll()
                .stream()
                .map(ScheduleResponse::new)
                .toList();

        return ApiResponse.success(responses);
    }
}