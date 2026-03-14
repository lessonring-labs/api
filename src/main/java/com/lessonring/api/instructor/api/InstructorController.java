package com.lessonring.api.instructor.api;

import com.lessonring.api.common.response.ApiResponse;
import com.lessonring.api.instructor.application.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/instructors")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    @GetMapping("/{id}")
    public ApiResponse<Void> getInstructor(@PathVariable Long id) {
        // TODO: 강사 조회 구현
        return ApiResponse.success();
    }
}
