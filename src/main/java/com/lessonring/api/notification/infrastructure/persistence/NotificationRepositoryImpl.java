package com.lessonring.api.notification.infrastructure.persistence;

import com.lessonring.api.notification.domain.Notification;
import com.lessonring.api.notification.domain.repository.NotificationRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationJpaRepository notificationJpaRepository;

    @Override
    public Notification save(Notification notification) {
        return notificationJpaRepository.save(notification);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return notificationJpaRepository.findById(id);
    }

    @Override
    public List<Notification> findAll() {
        return notificationJpaRepository.findAll();
    }

    @Override
    public List<Notification> findAllByMemberId(Long memberId) {
        return notificationJpaRepository.findAllByMemberIdOrderByIdDesc(memberId);
    }
}