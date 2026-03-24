package com.lessonring.api.payment.infrastructure.pg;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class TossPaymentsClient implements PgClient {

    private final RestClient restClient;

    @Value("${pg.toss.secret-key}")
    private String secretKey;

    @Value("${pg.toss.base-url}")
    private String baseUrl;

    @Override
    public PgApproveResponse approve(PgApproveRequest request) {
        try {
            String rawResponse = restClient.post()
                    .uri(baseUrl + "/v1/payments/confirm")
                    .header(HttpHeaders.AUTHORIZATION, basicAuth(secretKey))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new TossApproveBody(
                            request.getPaymentKey(),
                            request.getOrderId(),
                            request.getAmount()
                    ))
                    .retrieve()
                    .body(String.class);

            return PgApproveResponse.builder()
                    .provider("TOSS")
                    .paymentKey(request.getPaymentKey())
                    .orderId(request.getOrderId())
                    .amount(request.getAmount())
                    .success(true)
                    .rawResponse(rawResponse)
                    .build();

        } catch (Exception e) {
            return PgApproveResponse.builder()
                    .provider("TOSS")
                    .paymentKey(request.getPaymentKey())
                    .orderId(request.getOrderId())
                    .amount(request.getAmount())
                    .success(false)
                    .failureReason(e.getMessage())
                    .rawResponse("{}")
                    .build();
        }
    }

    @Override
    public PgCancelResponse cancel(PgCancelRequest request) {
        try {
            String rawResponse = restClient.post()
                    .uri(baseUrl + "/v1/payments/" + request.getPaymentKey() + "/cancel")
                    .header(HttpHeaders.AUTHORIZATION, basicAuth(secretKey))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new TossCancelBody(
                            request.getCancelAmount(),
                            request.getReason()
                    ))
                    .retrieve()
                    .body(String.class);

            return PgCancelResponse.builder()
                    .provider("TOSS")
                    .paymentKey(request.getPaymentKey())
                    .success(true)
                    .rawResponse(rawResponse)
                    .build();

        } catch (Exception e) {
            return PgCancelResponse.builder()
                    .provider("TOSS")
                    .paymentKey(request.getPaymentKey())
                    .success(false)
                    .failureReason(e.getMessage())
                    .rawResponse("{}")
                    .build();
        }
    }

    private String basicAuth(String secretKey) {
        String value = secretKey + ":";
        return "Basic " + Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private record TossApproveBody(
            String paymentKey,
            String orderId,
            Long amount
    ) {
    }

    private record TossCancelBody(
            Long cancelAmount,
            String cancelReason
    ) {
    }
}