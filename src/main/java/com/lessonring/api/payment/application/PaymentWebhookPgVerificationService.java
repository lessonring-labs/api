package com.lessonring.api.payment.application;

import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.payment.api.request.PaymentWebhookRequest;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.infrastructure.pg.PgClient;
import com.lessonring.api.payment.infrastructure.pg.PgPaymentStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentWebhookPgVerificationService {

    private final PgClient pgClient;

    public PgPaymentStatusResponse verifyCompleted(Payment payment, PaymentWebhookRequest request) {
        PgPaymentStatusResponse pgResponse = fetch(request);

        if (!pgResponse.isCompleted()) {
            log.warn("payment webhook verification failed - expected completed. paymentId={}, orderId={}, paymentKey={}, pgStatus={}",
                    payment.getId(), request.getOrderId(), request.getPaymentKey(), pgResponse.getStatus());
            throw new BusinessException(
                    ErrorCode.PAYMENT_WEBHOOK_VERIFICATION_FAILED,
                    "PG 조회 결과 completed 상태가 아닙니다."
            );
        }

        validateCommon(payment, request, pgResponse);
        return pgResponse;
    }

    public PgPaymentStatusResponse verifyFailed(Payment payment, PaymentWebhookRequest request) {
        PgPaymentStatusResponse pgResponse = fetch(request);

        if (!pgResponse.isFailed()) {
            log.warn("payment webhook verification failed - expected failed. paymentId={}, orderId={}, paymentKey={}, pgStatus={}",
                    payment.getId(), request.getOrderId(), request.getPaymentKey(), pgResponse.getStatus());
            throw new BusinessException(
                    ErrorCode.PAYMENT_WEBHOOK_VERIFICATION_FAILED,
                    "PG 조회 결과 failed 상태가 아닙니다."
            );
        }

        validateCommon(payment, request, pgResponse);
        return pgResponse;
    }

    public PgPaymentStatusResponse verifyCanceled(Payment payment, PaymentWebhookRequest request) {
        PgPaymentStatusResponse pgResponse = fetch(request);

        if (!pgResponse.isCanceled()) {
            log.warn("payment webhook verification failed - expected canceled. paymentId={}, orderId={}, paymentKey={}, pgStatus={}",
                    payment.getId(), request.getOrderId(), request.getPaymentKey(), pgResponse.getStatus());
            throw new BusinessException(
                    ErrorCode.PAYMENT_WEBHOOK_VERIFICATION_FAILED,
                    "PG 조회 결과 canceled 상태가 아닙니다."
            );
        }

        validateCommon(payment, request, pgResponse);
        return pgResponse;
    }

    private PgPaymentStatusResponse fetch(PaymentWebhookRequest request) {
        if (request.getPaymentKey() == null || request.getPaymentKey().isBlank()) {
            throw new BusinessException(ErrorCode.PAYMENT_WEBHOOK_VERIFICATION_FAILED, "paymentKey가 없는 webhook 요청입니다.");
        }

        return pgClient.getPayment(request.getPaymentKey());
    }

    private void validateCommon(
            Payment payment,
            PaymentWebhookRequest request,
            PgPaymentStatusResponse pgResponse
    ) {
        if (pgResponse.getPaymentKey() == null || !pgResponse.getPaymentKey().equals(request.getPaymentKey())) {
            log.warn("payment webhook verification failed - paymentKey mismatch. paymentId={}, requestPaymentKey={}, pgPaymentKey={}",
                    payment.getId(), request.getPaymentKey(), pgResponse.getPaymentKey());
            throw new BusinessException(ErrorCode.PAYMENT_WEBHOOK_VERIFICATION_FAILED, "PG paymentKey 검증에 실패했습니다.");
        }

        if (pgResponse.getOrderId() == null || !pgResponse.getOrderId().equals(request.getOrderId())) {
            log.warn("payment webhook verification failed - orderId mismatch. paymentId={}, requestOrderId={}, pgOrderId={}",
                    payment.getId(), request.getOrderId(), pgResponse.getOrderId());
            throw new BusinessException(ErrorCode.PAYMENT_WEBHOOK_VERIFICATION_FAILED, "PG orderId 검증에 실패했습니다.");
        }

        if (pgResponse.getTotalAmount() == null || !pgResponse.getTotalAmount().equals(payment.getAmount())) {
            log.warn("payment webhook verification failed - amount mismatch. paymentId={}, paymentAmount={}, pgAmount={}",
                    payment.getId(), payment.getAmount(), pgResponse.getTotalAmount());
            throw new BusinessException(ErrorCode.PAYMENT_WEBHOOK_VERIFICATION_FAILED, "PG amount 검증에 실패했습니다.");
        }
    }
}
