package com.lessonring.api.payment.domain;

import com.lessonring.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "payment_webhook_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentWebhookLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "transmission_id")
    private String transmissionId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "payment_key")
    private String paymentKey;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    private PaymentWebhookLog(
            String provider,
            String transmissionId,
            String eventType,
            String orderId,
            String paymentKey,
            String payload
    ) {
        this.provider = provider;
        this.transmissionId = transmissionId;
        this.eventType = eventType;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.payload = payload;
    }

    public static PaymentWebhookLog create(
            String provider,
            String transmissionId,
            String eventType,
            String orderId,
            String paymentKey,
            String payload
    ) {
        return new PaymentWebhookLog(
                provider,
                transmissionId,
                eventType,
                orderId,
                paymentKey,
                payload
        );
    }
}