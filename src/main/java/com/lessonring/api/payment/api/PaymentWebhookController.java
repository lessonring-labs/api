package com.lessonring.api.payment.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lessonring.api.common.response.ApiResponse;
import com.lessonring.api.payment.api.request.PaymentWebhookRequest;
import com.lessonring.api.payment.application.PaymentWebhookService;
import com.lessonring.api.payment.infrastructure.webhook.PaymentWebhookSignatureVerifier;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "PaymentWebhook", description = "결제 웹훅 API")
@RestController
@RequestMapping("/api/v1/payments/webhook")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentWebhookService paymentWebhookService;
    private final PaymentWebhookSignatureVerifier paymentWebhookSignatureVerifier;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ApiResponse<Void> handleWebhook(
            @RequestHeader(value = "Tosspayments-Webhook-Transmission-Id", required = false) String transmissionId,
            @RequestHeader(value = "Tosspayments-Signature", required = false) String signature,
            @RequestHeader(value = "Tosspayments-Webhook-Timestamp", required = false) String timestamp,
            @RequestBody String rawBody
    ) throws Exception {
        paymentWebhookSignatureVerifier.verify(signature, timestamp, rawBody);

        PaymentWebhookRequest request = objectMapper.readValue(rawBody, PaymentWebhookRequest.class);

        paymentWebhookService.handle(
                transmissionId,
                signature,
                rawBody,
                request
        );

        return ApiResponse.success();
    }
}