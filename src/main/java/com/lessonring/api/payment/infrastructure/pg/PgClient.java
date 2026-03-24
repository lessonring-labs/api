package com.lessonring.api.payment.infrastructure.pg;

public interface PgClient {

    PgApproveResponse approve(PgApproveRequest request);

    PgCancelResponse cancel(PgCancelRequest request);
}