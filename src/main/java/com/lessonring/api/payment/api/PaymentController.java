package com.lessonring.api.payment.api;

import com.lessonring.api.common.response.ApiResponse;
import com.lessonring.api.payment.api.request.PaymentCreateRequest;
import com.lessonring.api.payment.api.response.PaymentResponse;
import com.lessonring.api.payment.application.PaymentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}