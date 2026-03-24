package com.lessonring.api.payment.api;

import com.lessonring.api.common.response.ApiResponse;
import com.lessonring.api.payment.api.request.PaymentWebhookRequest;
import com.lessonring.api.payment.application.PaymentWebhookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "PaymentWebhook", description = "결제 웹훅 API")
@RestController
@RequestMapping("/api/v1/payments/webhook")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentWebhookService paymentWebhookService;

    @PostMapping
    public ApiResponse<Void> handleWebhook(
            @RequestHeader(value = "Tosspayments-Webhook-Transmission-Id", required = false) String transmissionId,
            @RequestHeader(value = "Tosspayments-Signature", required = false) String signature,
            @RequestBody PaymentWebhookRequest request
    ) {
        paymentWebhookService.handle(transmissionId, signature, request);
        return ApiResponse.success();
    }
}