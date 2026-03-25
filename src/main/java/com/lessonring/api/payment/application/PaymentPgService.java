package com.lessonring.api.payment.application;

import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.common.event.DomainEventPublisher;
import com.lessonring.api.membership.domain.Membership;
import com.lessonring.api.membership.domain.repository.MembershipRepository;
import com.lessonring.api.payment.api.request.PaymentApproveRequest;
import com.lessonring.api.payment.api.response.PaymentApproveResponse;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.PaymentOperation;
import com.lessonring.api.payment.domain.PaymentOperationStatus;
import com.lessonring.api.payment.domain.PaymentOperationType;
import com.lessonring.api.payment.domain.PaymentStatus;
import com.lessonring.api.payment.domain.event.PaymentCompletedEvent;
import com.lessonring.api.payment.domain.repository.PaymentRepository;
import com.lessonring.api.payment.infrastructure.lock.PaymentStateLockManager;
import com.lessonring.api.payment.infrastructure.pg.PgApproveRequest;
import com.lessonring.api.payment.infrastructure.pg.PgApproveResponse;
import com.lessonring.api.payment.infrastructure.pg.PgClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentPgService {

    private final PaymentRepository paymentRepository;
    private final MembershipRepository membershipRepository;
    private final PgClient pgClient;
    private final DomainEventPublisher domainEventPublisher;

    private final PaymentOperationService paymentOperationService;
    private final ApproveRequestHashGenerator approveRequestHashGenerator;
    private final PaymentStateLockManager paymentStateLockManager;

    @Transactional
    public PaymentApproveResponse approve(
            Long paymentId,
            PaymentApproveRequest request,
            String idempotencyKey
    ) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        String requestHash = approveRequestHashGenerator.generate(
                paymentId,
                request.getOrderId(),
                request.getPaymentKey(),
                request.getAmount()
        );

        PaymentOperationStartResult operationResult = paymentOperationService.startOrGet(
                paymentId,
                PaymentOperationType.APPROVE,
                idempotencyKey,
                requestHash
        );

        PaymentOperation operation = operationResult.operation();

        if (operationResult.status() == PaymentOperationStatus.SUCCEEDED) {
            log.info("[PAYMENT] idempotent hit - operationId={}, paymentId={}",
                    operation.getId(), operation.getPaymentId());
            return paymentOperationService.restoreResponse(operation, PaymentApproveResponse.class);
        }

        if (operationResult.status() == PaymentOperationStatus.PROCESSING && !operationResult.newlyCreated()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이미 처리 중인 approve 요청입니다.");
        }

        boolean locked = false;

        try {
            locked = paymentStateLockManager.tryLock(paymentId);

            if (!locked) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "결제 승인 처리 락 획득에 실패했습니다.");
            }

            Payment lockedPayment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

            validateApprovable(lockedPayment, request);

            PgApproveResponse pgResponse = pgClient.approve(
                    new PgApproveRequest(
                            request.getPaymentKey(),
                            request.getOrderId(),
                            request.getAmount()
                    )
            );

            if (!pgResponse.isSuccess()) {
                lockedPayment.fail(pgResponse.getFailureReason(), pgResponse.getRawResponse());

                throw new BusinessException(ErrorCode.INVALID_REQUEST, "PG 승인에 실패했습니다.");
            }

            Membership membership = Membership.create(
                    lockedPayment.getStudioId(),
                    lockedPayment.getMemberId(),
                    lockedPayment.getMembershipName(),
                    lockedPayment.getMembershipType(),
                    lockedPayment.getMembershipTotalCount(),
                    lockedPayment.getMembershipStartDate(),
                    lockedPayment.getMembershipEndDate()
            );
            Membership savedMembership = membershipRepository.save(membership);

            lockedPayment.markApproveRequested(pgResponse.getProvider(), pgResponse.getOrderId());
            lockedPayment.complete(
                    savedMembership.getId(),
                    pgResponse.getPaymentKey(),
                    pgResponse.getRawResponse()
            );

            domainEventPublisher.publish(
                    new PaymentCompletedEvent(
                            lockedPayment.getId(),
                            lockedPayment.getStudioId(),
                            lockedPayment.getMemberId(),
                            savedMembership.getId(),
                            lockedPayment.getAmount()
                    )
            );

            PaymentApproveResponse response = PaymentApproveResponse.builder()
                    .paymentId(lockedPayment.getId())
                    .status(lockedPayment.getStatus().name())
                    .paymentKey(pgResponse.getPaymentKey())
                    .amount(lockedPayment.getAmount())
                    .build();

            paymentOperationService.markSuccess(
                    operation,
                    pgResponse.getPaymentKey(),
                    paymentOperationService.toJson(response)
            );

            return response;

        } catch (BusinessException e) {
            paymentOperationService.markFailed(operation, e.getErrorCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            paymentOperationService.markFailed(
                    operation,
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    e.getMessage() == null ? "approve 처리 중 알 수 없는 오류가 발생했습니다." : e.getMessage()
            );
            throw e;
        } finally {
            if (locked) {
                paymentStateLockManager.unlock(paymentId);
            }
        }
    }

    @Transactional
    public PaymentApproveResponse approve(Long paymentId, PaymentApproveRequest request) {
        return approve(paymentId, request, "approve:legacy:" + paymentId);
    }

    private void validateApprovable(Payment payment, PaymentApproveRequest request) {
        if (payment.getStatus() != PaymentStatus.READY) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "READY 상태의 결제만 승인할 수 있습니다.");
        }

        if (payment.getAmount() == null || !payment.getAmount().equals(request.getAmount())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "결제 금액이 일치하지 않습니다.");
        }
    }
}