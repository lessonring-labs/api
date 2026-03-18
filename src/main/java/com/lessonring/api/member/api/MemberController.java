package com.lessonring.api.member.api;

import com.lessonring.api.common.response.ApiResponse;
import com.lessonring.api.member.api.request.MemberCreateRequest;
import com.lessonring.api.member.api.response.MemberResponse;
import com.lessonring.api.member.application.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member", description = "회원 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "회원 등록",
            description = "새 회원을 등록한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ApiResponse<MemberResponse> create(@Valid @RequestBody MemberCreateRequest request) {
        return ApiResponse.success(new MemberResponse(memberService.create(request)));
    }

    @Operation(
            summary = "회원 단건 조회",
            description = "회원 ID로 회원 정보를 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원 없음")
    })
    @GetMapping("/{id}")
    public ApiResponse<MemberResponse> get(
            @Parameter(description = "회원 ID", example = "1")
            @PathVariable Long id
    ) {
        return ApiResponse.success(new MemberResponse(memberService.get(id)));
    }

    @Operation(
            summary = "회원 목록 조회",
            description = "전체 회원 목록을 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 목록 조회 성공")
    })
    @GetMapping
    public ApiResponse<List<MemberResponse>> getAll() {
        List<MemberResponse> responses = memberService.getAll()
                .stream()
                .map(MemberResponse::new)
                .toList();

        return ApiResponse.success(responses);
    }
}