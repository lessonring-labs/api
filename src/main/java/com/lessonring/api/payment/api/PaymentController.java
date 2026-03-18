package com.lessonring.api.payment.api;

import com.lessonring.api.common.response.ApiResponse;
import com.lessonring.api.payment.api.request.PaymentCreateRequest;
import com.lessonring.api.payment.api.response.PaymentResponse;
import com.lessonring.api.payment.application.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payment", description = "결제 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "결제 생성",
            description = "결제 요청을 생성한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ApiResponse<PaymentResponse> create(@Valid @RequestBody PaymentCreateRequest request) {
        return ApiResponse.success(new PaymentResponse(paymentService.create(request)));
    }

    @Operation(
            summary = "결제 단건 조회",
            description = "결제 ID로 결제 정보를 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "결제 없음")
    })
    @GetMapping("/{id}")
    public ApiResponse<PaymentResponse> get(
            @Parameter(description = "결제 ID", example = "1")
            @PathVariable Long id
    ) {
        return ApiResponse.success(new PaymentResponse(paymentService.get(id)));
    }

    @Operation(
            summary = "결제 목록 조회",
            description = "전체 결제 목록을 조회한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 목록 조회 성공")
    })
    @GetMapping
    public ApiResponse<List<PaymentResponse>> getAll() {
        List<PaymentResponse> responses = paymentService.getAll()
                .stream()
                .map(PaymentResponse::new)
                .toList();

        return ApiResponse.success(responses);
    }

    @Operation(
            summary = "결제 완료 처리",
            description = "결제를 완료 처리하고 이용권 생성 흐름을 연결한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 완료 처리 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "결제 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 완료되었거나 완료 처리할 수 없는 상태")
    })
    @PatchMapping("/{id}/complete")
    public ApiResponse<PaymentResponse> complete(
            @Parameter(description = "결제 ID", example = "1")
            @PathVariable Long id
    ) {
        return ApiResponse.success(new PaymentResponse(paymentService.complete(id)));
    }

    @Operation(
            summary = "결제 취소 처리",
            description = "READY 상태 결제를 취소한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 취소 처리 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "결제 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 취소되었거나 취소할 수 없는 상태")
    })
    @PatchMapping("/{id}/cancel")
    public ApiResponse<PaymentResponse> cancel(
            @Parameter(description = "결제 ID", example = "1")
            @PathVariable Long id
    ) {
        return ApiResponse.success(new PaymentResponse(paymentService.cancel(id)));
    }

    @Operation(
            summary = "결제 환불 처리",
            description = "완료된 결제를 환불 처리하고, 연결된 이용권을 REFUNDED 상태로 변경하며 미래 예약을 취소한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 환불 처리 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "결제 또는 이용권 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "환불할 수 없는 상태")
    })
    @PatchMapping("/{id}/refund")
    public ApiResponse<PaymentResponse> refund(
            @Parameter(description = "결제 ID", example = "1")
            @PathVariable Long id
    ) {
        return ApiResponse.success(new PaymentResponse(paymentService.refund(id)));
    }
}