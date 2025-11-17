package com.certipath.notification_service.service;

import com.certipath.notification_service.entity.Notification;
import com.certipath.notification_service.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    public Notification send(Long userId, String message, String type) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setMessage(message);
        n.setType(type);
        n.setStatus("UNREAD");
        return repo.save(n);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Notification markAsRead(Long id) {
        Notification n = repo.findById(id).orElseThrow();
        n.setStatus("READ");
        return repo.save(n);
    }
}
