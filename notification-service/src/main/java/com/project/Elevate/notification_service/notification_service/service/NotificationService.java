package com.project.Elevate.notification_service.notification_service.service;

import com.project.Elevate.notification_service.notification_service.entity.Notification;
import com.project.Elevate.notification_service.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification addNotification(Notification notification) {
        notification = notificationRepository.save(notification);
        return notification;
    }
}
