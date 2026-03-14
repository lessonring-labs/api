package com.lessonring.api.attendance.api;

import com.lessonring.api.attendance.application.AttendanceService;
import com.lessonring.api.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/{id}")
    public ApiResponse<Void> getAttendance(@PathVariable Long id) {
        // TODO: 출석 조회 구현
        return ApiResponse.success();
    }
}
