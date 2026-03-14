package com.lessonring.api.common.infrastructure.webhook;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WebhookClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public void send(String url, Object payload) {
        restTemplate.postForEntity(url, payload, String.class);
    }
}
