package com.lessonring.api.notification.infrastructure.persistence;

import com.lessonring.api.notification.domain.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByMemberIdOrderByIdDesc(Long memberId);
}