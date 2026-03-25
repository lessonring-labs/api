package com.lessonring.api.payment.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.payment.api.response.RefundResponse;
import com.lessonring.api.payment.domain.PaymentOperationStatus;
import com.lessonring.api.payment.domain.PaymentOperationType;
import com.lessonring.api.payment.domain.PaymentOperation;
import com.lessonring.api.payment.domain.repository.PaymentOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentOperationService {

    private final PaymentOperationRepository paymentOperationRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public PaymentOperationStartResult startOrGet(
            Long paymentId,
            PaymentOperationType operationType,
            String idempotencyKey,
            String requestHash
    ) {
        return paymentOperationRepository
                .findByPaymentIdAndOperationTypeAndIdempotencyKey(paymentId, operationType, idempotencyKey)
                .map(existing -> {
                    if (!existing.getRequestHash().equals(requestHash)) {
                        throw new BusinessException(
                                ErrorCode.INVALID_REQUEST,
                                "동일 idempotency key로 다른 요청을 보낼 수 없습니다."
                        );
                    }
                    return new PaymentOperationStartResult(existing, existing.getStatus(), false);
                })
                .orElseGet(() -> {
                    PaymentOperation created = PaymentOperation.create(
                            paymentId,
                            operationType,
                            idempotencyKey,
                            requestHash
                    );
                    return new PaymentOperationStartResult(
                            paymentOperationRepository.save(created),
                            PaymentOperationStatus.PROCESSING,
                            true
                    );
                });
    }

    public RefundResponse restoreRefundResponse(PaymentOperation operation) {
        if (operation.getResponsePayload() == null || operation.getResponsePayload().isBlank()) {
            throw new IllegalStateException("저장된 환불 응답이 없습니다.");
        }

        try {
            return objectMapper.readValue(operation.getResponsePayload(), RefundResponse.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("저장된 환불 응답 복원에 실패했습니다.", e);
        }
    }

    public String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("응답 직렬화에 실패했습니다.", e);
        }
    }
}