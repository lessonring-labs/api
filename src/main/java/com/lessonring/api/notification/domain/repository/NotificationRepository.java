package com.lessonring.api.notification.domain.repository;

import com.lessonring.api.notification.domain.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    default List<Notification> findAllByMemberId(Long memberId) {
        return findAllByMemberIdOrderByIdDesc(memberId);
    }

    List<Notification> findAllByMemberIdOrderByIdDesc(Long memberId);
}
