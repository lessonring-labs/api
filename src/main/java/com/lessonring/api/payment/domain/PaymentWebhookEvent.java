package com.lessonring.api.payment.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "payment_webhook_event",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_payment_webhook_event_event_id",
                        columnNames = {"event_id"}
                )
        },
        indexes = {
                @Index(name = "idx_payment_webhook_event_payment_key", columnList = "payment_key"),
                @Index(name = "idx_payment_webhook_event_event_type", columnList = "event_type"),
                @Index(name = "idx_payment_webhook_event_status", columnList = "status")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentWebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, length = 100)
    private String eventId;

    @Column(name = "payment_key", length = 100)
    private String paymentKey;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "payload_hash", nullable = false, length = 64)
    private String payloadHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentWebhookEventStatus status;

    @Column(name = "raw_payload", columnDefinition = "TEXT")
    private String rawPayload;

    @Column(name = "error_code", length = 100)
    private String errorCode;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public static PaymentWebhookEvent create(
            String eventId,
            String paymentKey,
            String eventType,
            String payloadHash,
            String rawPayload
    ) {
        PaymentWebhookEvent event = new PaymentWebhookEvent();
        event.eventId = eventId;
        event.paymentKey = paymentKey;
        event.eventType = eventType;
        event.payloadHash = payloadHash;
        event.rawPayload = rawPayload;
        event.status = PaymentWebhookEventStatus.RECEIVED;
        event.receivedAt = LocalDateTime.now();
        return event;
    }

    public void markProcessing() {
        this.status = PaymentWebhookEventStatus.PROCESSING;
        this.errorCode = null;
        this.errorMessage = null;
    }

    public void markSucceeded() {
        this.status = PaymentWebhookEventStatus.SUCCEEDED;
        this.processedAt = LocalDateTime.now();
        this.errorCode = null;
        this.errorMessage = null;
    }

    public void markFailed(String errorCode, String errorMessage) {
        this.status = PaymentWebhookEventStatus.FAILED;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public boolean isReceived() {
        return this.status == PaymentWebhookEventStatus.RECEIVED;
    }

    public boolean isProcessing() {
        return this.status == PaymentWebhookEventStatus.PROCESSING;
    }

    public boolean isSucceeded() {
        return this.status == PaymentWebhookEventStatus.SUCCEEDED;
    }

    public boolean isFailed() {
        return this.status == PaymentWebhookEventStatus.FAILED;
    }
}