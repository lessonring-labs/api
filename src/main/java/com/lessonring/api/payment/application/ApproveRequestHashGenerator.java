package com.lessonring.api.payment.application;

import com.lessonring.api.payment.domain.PaymentOperationType;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.springframework.stereotype.Component;

@Component
public class ApproveRequestHashGenerator {

    public String generate(
            Long paymentId,
            String orderId,
            String paymentKey,
            Long amount
    ) {
        String source = String.join("|",
                normalize(paymentId),
                normalize(orderId),
                normalize(paymentKey),
                normalize(amount),
                PaymentOperationType.APPROVE.name()
        );

        return sha256(source);
    }

    private String normalize(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to generate approve request hash", e);
        }
    }
}