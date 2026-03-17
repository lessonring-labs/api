package com.lessonring.api.booking.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingNoShowScheduler {

    private final BookingService bookingService;

    @Scheduled(cron = "0 */5 * * * *")
    public void runNoShowBatch() {
        int processed = bookingService.markNoShowTargets();
        log.info("[NoShowBatch] processed={}", processed);
    }
}