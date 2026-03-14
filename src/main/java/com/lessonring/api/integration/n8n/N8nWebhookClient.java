package com.lessonring.api.integration.n8n;

import com.lessonring.api.common.infrastructure.webhook.WebhookClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class N8nWebhookClient {

    private final WebhookClient webhookClient;

    // TODO: n8n 웹훅 호출 구현
}
