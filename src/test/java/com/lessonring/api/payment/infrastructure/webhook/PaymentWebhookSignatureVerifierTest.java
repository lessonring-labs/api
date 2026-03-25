package com.lessonring.api.payment.infrastructure.webhook;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentWebhookSignatureVerifierTest {

    private static final String SECRET = "test-webhook-secret";
    private static final String ALGORITHM = "HmacSHA256";

    private PaymentWebhookSignatureVerifier verifier;

    @BeforeEach
    void setUp() {
        PaymentWebhookSecretProvider secretProvider = () -> SECRET;
        verifier = new PaymentWebhookSignatureVerifier(secretProvider);
    }

    @Test
    @DisplayName("정상 signature면 검증에 성공한다")
    void verify_success() {
        String rawBody = "{\"eventType\":\"PAYMENT_COMPLETED\",\"orderId\":\"ORDER_123\",\"paymentKey\":\"paymentKey_123\"}";
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String signature = generateSignature(timestamp, rawBody);

        assertThatCode(() -> verifier.verify(signature, timestamp, rawBody))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("signature 헤더가 없으면 예외가 발생한다")
    void verify_fail_when_signature_missing() {
        String rawBody = "{\"eventType\":\"PAYMENT_COMPLETED\"}";
        String timestamp = String.valueOf(Instant.now().getEpochSecond());

        assertThatThrownBy(() -> verifier.verify(null, timestamp, rawBody))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST);
                });
    }

    @Test
    @DisplayName("timestamp 헤더가 없으면 예외가 발생한다")
    void verify_fail_when_timestamp_missing() {
        String rawBody = "{\"eventType\":\"PAYMENT_COMPLETED\"}";
        String signature = "dummy-signature";

        assertThatThrownBy(() -> verifier.verify(signature, null, rawBody))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST);
                });
    }

    @Test
    @DisplayName("raw body가 비어 있으면 예외가 발생한다")
    void verify_fail_when_body_missing() {
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String signature = "dummy-signature";

        assertThatThrownBy(() -> verifier.verify(signature, timestamp, ""))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST);
                });
    }

    @Test
    @DisplayName("timestamp 형식이 잘못되면 예외가 발생한다")
    void verify_fail_when_timestamp_invalid() {
        String rawBody = "{\"eventType\":\"PAYMENT_COMPLETED\"}";
        String signature = "dummy-signature";

        assertThatThrownBy(() -> verifier.verify(signature, "invalid-timestamp", rawBody))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST);
                });
    }

    @Test
    @DisplayName("허용 시간 범위를 초과하면 예외가 발생한다")
    void verify_fail_when_timestamp_expired() {
        String rawBody = "{\"eventType\":\"PAYMENT_COMPLETED\"}";
        String timestamp = String.valueOf(Instant.now().minusSeconds(1000).getEpochSecond());
        String signature = generateSignature(timestamp, rawBody);

        assertThatThrownBy(() -> verifier.verify(signature, timestamp, rawBody))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST);
                });
    }

    @Test
    @DisplayName("body가 변조되면 예외가 발생한다")
    void verify_fail_when_body_tampered() {
        String originalBody = "{\"eventType\":\"PAYMENT_COMPLETED\",\"orderId\":\"ORDER_123\"}";
        String tamperedBody = "{\"eventType\":\"PAYMENT_COMPLETED\",\"orderId\":\"ORDER_999\"}";
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String signature = generateSignature(timestamp, originalBody);

        assertThatThrownBy(() -> verifier.verify(signature, timestamp, tamperedBody))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST);
                });
    }

    @Test
    @DisplayName("secret이 다르면 예외가 발생한다")
    void verify_fail_when_secret_mismatch() {
        String rawBody = "{\"eventType\":\"PAYMENT_COMPLETED\",\"orderId\":\"ORDER_123\"}";
        String timestamp = String.valueOf(Instant.now().getEpochSecond());

        String wrongSecretSignature = generateSignature("wrong-secret", timestamp, rawBody);

        assertThatThrownBy(() -> verifier.verify(wrongSecretSignature, timestamp, rawBody))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST);
                });
    }

    @Test
    @DisplayName("signature가 다르면 예외가 발생한다")
    void verify_fail_when_signature_mismatch() {
        String rawBody = "{\"eventType\":\"PAYMENT_COMPLETED\",\"orderId\":\"ORDER_123\"}";
        String timestamp = String.valueOf(Instant.now().getEpochSecond());

        assertThatThrownBy(() -> verifier.verify("invalid-signature", timestamp, rawBody))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException be = (BusinessException) ex;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INVALID_REQUEST);
                });
    }

    private String generateSignature(String timestamp, String rawBody) {
        return generateSignature(SECRET, timestamp, rawBody);
    }

    private String generateSignature(String secret, String timestamp, String rawBody) {
        try {
            String payload = timestamp + "." + rawBody;

            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8),
                    ALGORITHM
            );
            mac.init(keySpec);

            byte[] result = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}