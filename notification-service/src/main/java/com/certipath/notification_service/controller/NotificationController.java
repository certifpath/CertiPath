package com.certipath.notification_service.controller;

import com.certipath.notification_service.entity.Notification;
import com.certipath.notification_service.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping("/send")
    public Notification send(@RequestParam Long userId,
                             @RequestParam String message,
                             @RequestParam String type) {
        return service.send(userId, message, type);
    }

    @GetMapping("/user/{id}")
    public List<Notification> getByUser(@PathVariable Long id) {
        return service.getUserNotifications(id);
    }

    @PutMapping("/read/{id}")
    public Notification read(@PathVariable Long id) {
        return service.markAsRead(id);
    }
}
