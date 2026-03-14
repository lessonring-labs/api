package com.lessonring.api.member.api;

import com.lessonring.api.common.response.ApiResponse;
import com.lessonring.api.member.api.request.MemberCreateRequest;
import com.lessonring.api.member.api.response.MemberResponse;
import com.lessonring.api.member.application.MemberService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ApiResponse<MemberResponse> create(@Valid @RequestBody MemberCreateRequest request) {
        return ApiResponse.success(new MemberResponse(memberService.create(request)));
    }

    @GetMapping("/{id}")
    public ApiResponse<MemberResponse> get(@PathVariable Long id) {
        return ApiResponse.success(new MemberResponse(memberService.get(id)));
    }

    @GetMapping
    public ApiResponse<List<MemberResponse>> getAll() {
        List<MemberResponse> responses = memberService.getAll()
                .stream()
                .map(MemberResponse::new)
                .toList();

        return ApiResponse.success(responses);
    }
}