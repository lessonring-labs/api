package com.lessonring.api.notification.application;

import com.lessonring.api.common.error.BusinessException;
import com.lessonring.api.common.error.ErrorCode;
import com.lessonring.api.notification.domain.Notification;
import com.lessonring.api.notification.domain.repository.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public List<Notification> getAllByMemberId(Long memberId) {
        return notificationRepository.findAllByMemberId(memberId);
    }

    @Transactional
    public Notification read(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        notification.read();
        return notification;
    }
}