package com.lessonring.api.attendance.api;

import com.lessonring.api.attendance.api.request.AttendanceCreateRequest;
import com.lessonring.api.attendance.api.response.AttendanceResponse;
import com.lessonring.api.attendance.application.AttendanceService;
import com.lessonring.api.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ApiResponse<AttendanceResponse> create(@RequestBody @Valid AttendanceCreateRequest request) {
        return ApiResponse.success(new AttendanceResponse(attendanceService.create(request)));
    }

    @GetMapping("/{id}")
    public ApiResponse<AttendanceResponse> get(@PathVariable Long id) {
        return ApiResponse.success(new AttendanceResponse(attendanceService.get(id)));
    }

    @GetMapping
    public ApiResponse<List<AttendanceResponse>> getAll() {
        return ApiResponse.success(
                attendanceService.getAll()
                        .stream()
                        .map(AttendanceResponse::new)
                        .toList()
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        attendanceService.cancel(id);
        return ApiResponse.success(null);
    }
}