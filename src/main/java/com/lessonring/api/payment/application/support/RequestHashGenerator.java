package com.lessonring.api.payment.application.support;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Component
public class RequestHashGenerator {

    public String generateRefundHash(Long paymentId, Long membershipId, Long memberId) {
        String source = String.join(":",
                String.valueOf(paymentId),
                String.valueOf(membershipId),
                String.valueOf(memberId)
        );

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(source.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("request hash 생성에 실패했습니다.", e);
        }
    }
}