package com.certipath.notification_service.controller;

import com.certipath.notification_service.entity.Notification;
import com.certipath.notification_service.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "*") // optional
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    // SEND notification (POST)
    @PostMapping("/send")
    public Notification send(@RequestParam Long userId,
                             @RequestParam String message,
                             @RequestParam String type) {
        return service.send(userId, message, type);
    }

    // GET notifications for a user
    @GetMapping("/user/{id}")
    public List<Notification> getByUser(@PathVariable Long id) {
        return service.getUserNotifications(id);
    }

    // MARK notification as read
    @PutMapping("/read/{id}")
    public Notification read(@PathVariable Long id) {
        return service.markAsRead(id);
    }

    // DELETE notification by ID
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteNotification(id);
        return "Notification deleted successfully.";
    }

    // GET all notifications (admin/debug)
    @GetMapping("/all")
    public List<Notification> getAll() {
        return service.getAllNotifications();
    }
}
