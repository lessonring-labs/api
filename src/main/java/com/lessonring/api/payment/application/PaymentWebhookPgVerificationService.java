package com.lessonring.api.payment.application;

import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.payment.api.request.PaymentWebhookRequest;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.infrastructure.pg.PgClient;
import com.lessonring.api.payment.infrastructure.pg.PgPaymentStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentWebhookPgVerificationService {

    private final PgClient pgClient;

    public PgPaymentStatusResponse verifyCompleted(Payment payment, PaymentWebhookRequest request) {
        PgPaymentStatusResponse pgResponse = fetch(request);

        if (!pgResponse.isCompleted()) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    "PG 조회 결과 completed 상태가 아닙니다."
            );
        }

        validateCommon(payment, request, pgResponse);
        return pgResponse;
    }

    public PgPaymentStatusResponse verifyFailed(Payment payment, PaymentWebhookRequest request) {
        PgPaymentStatusResponse pgResponse = fetch(request);

        if (!pgResponse.isFailed()) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    "PG 조회 결과 failed 상태가 아닙니다."
            );
        }

        validateCommon(payment, request, pgResponse);
        return pgResponse;
    }

    public PgPaymentStatusResponse verifyCanceled(Payment payment, PaymentWebhookRequest request) {
        PgPaymentStatusResponse pgResponse = fetch(request);

        if (!pgResponse.isCanceled()) {
            throw new BusinessException(
                    ErrorCode.INVALID_REQUEST,
                    "PG 조회 결과 canceled 상태가 아닙니다."
            );
        }

        validateCommon(payment, request, pgResponse);
        return pgResponse;
    }

    private PgPaymentStatusResponse fetch(PaymentWebhookRequest request) {
        if (request.getPaymentKey() == null || request.getPaymentKey().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "paymentKey가 없는 webhook 요청입니다.");
        }

        return pgClient.getPayment(request.getPaymentKey());
    }

    private void validateCommon(
            Payment payment,
            PaymentWebhookRequest request,
            PgPaymentStatusResponse pgResponse
    ) {
        if (pgResponse.getPaymentKey() == null || !pgResponse.getPaymentKey().equals(request.getPaymentKey())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "PG paymentKey 검증에 실패했습니다.");
        }

        if (pgResponse.getOrderId() == null || !pgResponse.getOrderId().equals(request.getOrderId())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "PG orderId 검증에 실패했습니다.");
        }

        if (pgResponse.getTotalAmount() == null || !pgResponse.getTotalAmount().equals(payment.getAmount())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "PG amount 검증에 실패했습니다.");
        }
    }
}