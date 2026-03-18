package com.lessonring.api.payment.api;

import com.lessonring.api.common.response.ApiResponse;
import com.lessonring.api.payment.api.request.PaymentCreateRequest;
import com.lessonring.api.payment.api.response.PaymentResponse;
import com.lessonring.api.payment.api.response.RefundResponse;
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

    @PostMapping
    public ApiResponse<PaymentResponse> create(@Valid @RequestBody PaymentCreateRequest request) {
        return ApiResponse.success(new PaymentResponse(paymentService.create(request)));
    }

    @GetMapping("/{id}")
    public ApiResponse<PaymentResponse> get(@PathVariable Long id) {
        return ApiResponse.success(new PaymentResponse(paymentService.get(id)));
    }

    @GetMapping
    public ApiResponse<List<PaymentResponse>> getAll() {
        List<PaymentResponse> responses = paymentService.getAll()
                .stream()
                .map(PaymentResponse::new)
                .toList();

        return ApiResponse.success(responses);
    }

    @PatchMapping("/{id}/complete")
    public ApiResponse<PaymentResponse> complete(@PathVariable Long id) {
        return ApiResponse.success(new PaymentResponse(paymentService.complete(id)));
    }

    @PatchMapping("/{id}/cancel")
    public ApiResponse<PaymentResponse> cancel(@PathVariable Long id) {
        return ApiResponse.success(new PaymentResponse(paymentService.cancel(id)));
    }

    @Operation(
            summary = "결제 환불 처리",
            description = "완료된 결제를 환불 처리하고, 환불 금액과 취소된 미래 예약 수를 반환한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 환불 처리 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "결제 또는 이용권 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "환불할 수 없는 상태")
    })
    @PatchMapping("/{id}/refund")
    public ApiResponse<RefundResponse> refund(
            @Parameter(description = "결제 ID", example = "1")
            @PathVariable Long id
    ) {
        return ApiResponse.success(paymentService.refund(id));
    }
}