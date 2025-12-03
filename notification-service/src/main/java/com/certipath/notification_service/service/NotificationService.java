package com.certipath.notification_service.service;

import com.certipath.notification_service.entity.Notification;
import com.certipath.notification_service.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
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
        n.setCreatedAt(LocalDateTime.now());
        return repo.save(n);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return repo.findByUserId(userId);
    }

    public Notification markAsRead(Long id) {
    Notification n = repo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Notification not found with id " + id
        ));
    n.setStatus("READ");
    return repo.save(n);
    }

    // NEW: delete notification
    public void deleteNotification(Long id) {
        repo.deleteById(id);
    }

    // NEW: get all notifications
    public List<Notification> getAllNotifications() {
        return repo.findAll();
    }
}
