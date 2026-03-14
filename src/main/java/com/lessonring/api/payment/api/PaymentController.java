package com.lessonring.api.payment.api;

import com.lessonring.api.common.response.ApiResponse;
import com.lessonring.api.payment.application.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{id}")
    public ApiResponse<Void> getPayment(@PathVariable Long id) {
        // TODO: 결제 조회 구현
        return ApiResponse.success();
    }
}
