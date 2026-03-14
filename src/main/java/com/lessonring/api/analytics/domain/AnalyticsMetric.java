package com.lessonring.api.analytics.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnalyticsMetric {

    private final String metricName;
    private final Long value;
    private final LocalDateTime recordedAt;
}
