package com.lessonring.api.membership.api;

import com.lessonring.api.common.response.ApiResponse;
import com.lessonring.api.membership.api.request.MembershipCreateRequest;
import com.lessonring.api.membership.api.response.MembershipResponse;
import com.lessonring.api.membership.application.MembershipService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/api/v1/memberships")
    public ApiResponse<MembershipResponse> create(@Valid @RequestBody MembershipCreateRequest request) {
        return ApiResponse.success(new MembershipResponse(membershipService.create(request)));
    }

    @GetMapping("/api/v1/memberships/{id}")
    public ApiResponse<MembershipResponse> get(@PathVariable Long id) {
        return ApiResponse.success(new MembershipResponse(membershipService.get(id)));
    }

    @GetMapping("/api/v1/members/{memberId}/memberships")
    public ApiResponse<List<MembershipResponse>> getByMemberId(@PathVariable Long memberId) {
        List<MembershipResponse> responses = membershipService.getByMemberId(memberId)
                .stream()
                .map(MembershipResponse::new)
                .toList();

        return ApiResponse.success(responses);
    }
}