package com.lessonring.api.membership.api;

import com.lessonring.api.common.response.ApiResponse;
import com.lessonring.api.membership.api.request.MembershipCreateRequest;
import com.lessonring.api.membership.api.response.MembershipResponse;
import com.lessonring.api.membership.application.MembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Membership", description = "이용권 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @Operation(
            summary = "이용권 생성",
            description = "새 이용권을 생성한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이용권 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/api/v1/memberships")
    public ApiResponse<MembershipResponse> create(@Valid @RequestBody MembershipCreateRequest request) {
        return ApiResponse.success(new MembershipResponse(membershipService.create(request)));
    }

    @Operation(
            summary = "이용권 단건 조회",
            description = "이용권 ID로 이용권 정보를 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이용권 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "이용권 없음")
    })
    @GetMapping("/api/v1/memberships/{id}")
    public ApiResponse<MembershipResponse> get(
            @Parameter(description = "이용권 ID", example = "1")
            @PathVariable Long id
    ) {
        return ApiResponse.success(new MembershipResponse(membershipService.get(id)));
    }

    @Operation(
            summary = "회원별 이용권 목록 조회",
            description = "회원 ID로 해당 회원의 이용권 목록을 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이용권 목록 조회 성공")
    })
    @GetMapping("/api/v1/members/{memberId}/memberships")
    public ApiResponse<List<MembershipResponse>> getByMemberId(
            @Parameter(description = "회원 ID", example = "1")
            @PathVariable Long memberId
    ) {
        List<MembershipResponse> responses = membershipService.getAllByMemberId(memberId)
                .stream()
                .map(MembershipResponse::new)
                .toList();

        return ApiResponse.success(responses);
    }
}