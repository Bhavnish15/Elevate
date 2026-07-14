package com.project.Elevate.notification_service.notification_service.consumer;

import com.project.Elevate.notification_service.notification_service.entity.Notification;
import com.project.Elevate.notification_service.notification_service.repository.NotificationRepository;
import com.project.Elevate.notification_service.notification_service.service.NotificationService;
import com.project.Elevate.notification_service.postService.event.PostCreated;
import com.project.Elevate.notification_service.postService.event.PostLiked;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostsConsumer {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @KafkaListener(topics= "post_created_topic")
    public void hadnlePostCreated(PostCreated postCreated) {
        String message = String .format("Your connection with id: %d has created this post: %d", postCreated.getOwnerUserId(), postCreated.getContent());
        Notification notification = Notification.builder()
                .message(message)
                .userId(postCreated.getUserId())
                .build();
        notificationService.addNotification(notification);
    }

    @KafkaListener(topics = "post_liked_topic")
    public void handlePostLiked(PostLiked postLiked) {
        String message = String.format("User with id: %d has liked your post with id: %d", postLiked.getLikedByUserId(), postLiked.getPostId());
        Notification notification = Notification.builder()
                .message(message)
                .userId(postLiked.getOwnerUserId())
                .build();
        notificationService.addNotification(notification);
    }
}
