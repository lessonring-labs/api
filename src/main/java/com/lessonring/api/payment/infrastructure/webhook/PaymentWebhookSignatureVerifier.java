package com.lessonring.api.payment.infrastructure.webhook;

import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentWebhookSignatureVerifier {

    private static final long ALLOWED_SKEW_SECONDS = 300L;
    private static final String ALGORITHM = "HmacSHA256";

    private final PaymentWebhookSecretProvider paymentWebhookSecretProvider;

    public void verify(String signature, String timestamp, String rawBody) {
        validateHeaders(signature, timestamp, rawBody);
        validateTimestamp(timestamp);

        String expectedSignature = generateSignature(timestamp, rawBody);

        if (!safeEquals(expectedSignature, signature)) {
            throw new BusinessException(ErrorCode.PAYMENT_WEBHOOK_INVALID_SIGNATURE, "유효하지 않은 webhook signature 입니다.");
        }
    }

    private void validateHeaders(String signature, String timestamp, String rawBody) {
        if (signature == null || signature.isBlank()) {
            throw new BusinessException(ErrorCode.PAYMENT_WEBHOOK_INVALID_SIGNATURE, "webhook signature 헤더가 없습니다.");
        }

        if (timestamp == null || timestamp.isBlank()) {
            throw new BusinessException(ErrorCode.PAYMENT_WEBHOOK_INVALID_SIGNATURE, "webhook timestamp 헤더가 없습니다.");
        }

        if (rawBody == null || rawBody.isBlank()) {
            throw new BusinessException(ErrorCode.PAYMENT_WEBHOOK_INVALID_SIGNATURE, "webhook body가 비어 있습니다.");
        }
    }

    private void validateTimestamp(String timestamp) {
        long requestEpochSecond;

        try {
            requestEpochSecond = Long.parseLong(timestamp);
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.PAYMENT_WEBHOOK_INVALID_SIGNATURE, "webhook timestamp 형식이 올바르지 않습니다.");
        }

        long now = Instant.now().getEpochSecond();
        long diff = Math.abs(now - requestEpochSecond);

        if (diff > ALLOWED_SKEW_SECONDS) {
            throw new BusinessException(ErrorCode.PAYMENT_WEBHOOK_INVALID_SIGNATURE, "허용 범위를 초과한 webhook timestamp 입니다.");
        }
    }

    private String generateSignature(String timestamp, String rawBody) {
        try {
            String payload = timestamp + "." + rawBody;

            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    paymentWebhookSecretProvider.getSecret().getBytes(StandardCharsets.UTF_8),
                    ALGORITHM
            );

            mac.init(secretKeySpec);
            byte[] result = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            throw new IllegalStateException("webhook signature 생성에 실패했습니다.", e);
        }
    }

    private boolean safeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }

        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);

        if (aBytes.length != bBytes.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < aBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }

        return result == 0;
    }
}
