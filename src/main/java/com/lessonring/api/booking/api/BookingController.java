package com.lessonring.api.booking.api;

import com.lessonring.api.booking.api.request.BookingCreateRequest;
import com.lessonring.api.booking.api.response.BookingResponse;
import com.lessonring.api.booking.application.BookingLockFacade;
import com.lessonring.api.booking.application.BookingService;
import com.lessonring.api.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Booking", description = "예약 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingLockFacade bookingLockFacade;
    private final BookingService bookingService;

    @Operation(
            summary = "예약 생성",
            description = "회원이 스케줄에 예약을 생성한다. 예약 생성 시 분산락을 사용해 동시성을 제어한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "예약 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원, 스케줄 또는 이용권 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "중복 예약 또는 정원 초과")
    })
    @PostMapping
    public ApiResponse<BookingResponse> create(@RequestBody @Valid BookingCreateRequest request) {
        return ApiResponse.success(new BookingResponse(bookingLockFacade.create(request)));
    }

    @Operation(
            summary = "예약 단건 조회",
            description = "예약 ID로 예약 상세 정보를 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "예약 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "예약 없음")
    })
    @GetMapping("/{id}")
    public ApiResponse<BookingResponse> get(
            @Parameter(description = "예약 ID", example = "1")
            @PathVariable Long id
    ) {
        return ApiResponse.success(new BookingResponse(bookingService.get(id)));
    }

    @Operation(
            summary = "예약 목록 조회",
            description = "전체 예약 목록을 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "예약 목록 조회 성공")
    })
    @GetMapping
    public ApiResponse<List<BookingResponse>> getAll() {
        return ApiResponse.success(
                bookingService.getAll()
                        .stream()
                        .map(BookingResponse::new)
                        .toList()
        );
    }

    @Operation(
            summary = "예약 취소",
            description = "예약을 취소한다. 취소 시 분산락을 사용해 bookedCount 및 상태를 안전하게 변경한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "예약 취소 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "예약 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 취소되었거나 취소할 수 없는 상태")
    })
    @PatchMapping("/{id}/cancel")
    public ApiResponse<BookingResponse> cancel(
            @Parameter(description = "취소할 예약 ID", example = "1")
            @PathVariable Long id
    ) {
        return ApiResponse.success(new BookingResponse(bookingLockFacade.cancel(id)));
    }

    @Operation(
            summary = "예약 no-show 처리",
            description = "예약을 no-show 상태로 변경한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "no-show 처리 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "예약 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "no-show 처리할 수 없는 상태")
    })
    @PatchMapping("/{id}/no-show")
    public ApiResponse<BookingResponse> markNoShow(
            @Parameter(description = "no-show 처리할 예약 ID", example = "1")
            @PathVariable Long id
    ) {
        return ApiResponse.success(new BookingResponse(bookingService.markNoShow(id)));
    }

    @Operation(
            summary = "no-show 배치 처리",
            description = "no-show 대상 예약을 일괄 처리하고 처리 건수를 반환한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "배치 처리 성공")
    })
    @PatchMapping("/no-show/process")
    public ApiResponse<Integer> processNoShowBatch() {
        return ApiResponse.success(bookingService.markNoShowTargets());
    }
}