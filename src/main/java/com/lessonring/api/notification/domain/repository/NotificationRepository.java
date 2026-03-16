package com.lessonring.api.notification.domain.repository;

import com.lessonring.api.notification.domain.Notification;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

    Notification save(Notification notification);

    Optional<Notification> findById(Long id);

    List<Notification> findAll();

    List<Notification> findAllByMemberId(Long memberId);
}