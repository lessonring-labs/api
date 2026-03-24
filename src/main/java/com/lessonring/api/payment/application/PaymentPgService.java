package com.lessonring.api.payment.application;

import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.common.event.DomainEventPublisher;
import com.lessonring.api.membership.domain.Membership;
import com.lessonring.api.membership.domain.repository.MembershipRepository;
import com.lessonring.api.payment.api.request.PaymentApproveRequest;
import com.lessonring.api.payment.api.response.PaymentApproveResponse;
import com.lessonring.api.payment.domain.Payment;
import com.lessonring.api.payment.domain.PaymentStatus;
import com.lessonring.api.payment.domain.event.PaymentCompletedEvent;
import com.lessonring.api.payment.domain.repository.PaymentRepository;
import com.lessonring.api.payment.infrastructure.pg.PgApproveRequest;
import com.lessonring.api.payment.infrastructure.pg.PgApproveResponse;
import com.lessonring.api.payment.infrastructure.pg.PgClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentPgService {

    private final PaymentRepository paymentRepository;
    private final MembershipRepository membershipRepository;
    private final PgClient pgClient;
    private final DomainEventPublisher domainEventPublisher;

    @Transactional
    public PaymentApproveResponse approve(Long paymentId, PaymentApproveRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.READY) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "READY 상태의 결제만 승인할 수 있습니다.");
        }

        PgApproveResponse pgResponse = pgClient.approve(
                new PgApproveRequest(
                        request.getPaymentKey(),
                        request.getOrderId(),
                        request.getAmount()
                )
        );

        if (!pgResponse.isSuccess()) {
            payment.fail(pgResponse.getFailureReason(), pgResponse.getRawResponse());
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "PG 승인에 실패했습니다.");
        }

        Membership membership = Membership.create(
                payment.getStudioId(),
                payment.getMemberId(),
                payment.getMembershipName(),
                payment.getMembershipType(),
                payment.getMembershipTotalCount(),
                payment.getMembershipStartDate(),
                payment.getMembershipEndDate()
        );
        Membership savedMembership = membershipRepository.save(membership);

        payment.markApproveRequested(pgResponse.getProvider(), pgResponse.getOrderId());
        payment.complete(
                savedMembership.getId(),
                pgResponse.getPaymentKey(),
                pgResponse.getRawResponse()
        );

        domainEventPublisher.publish(
                new PaymentCompletedEvent(
                        payment.getId(),
                        payment.getStudioId(),
                        payment.getMemberId(),
                        savedMembership.getId(),
                        payment.getAmount()
                )
        );

        return PaymentApproveResponse.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus().name())
                .paymentKey(pgResponse.getPaymentKey())
                .amount(payment.getAmount())
                .build();
    }
}