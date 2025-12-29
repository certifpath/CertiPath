package com.certipath.notification_service.controller;

import com.certipath.notification_service.entity.Notification;
import com.certipath.notification_service.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    // Autoriser les services internes (si on utilise un token système) ou Admin
    @PreAuthorize("isAuthenticated()")
    public Notification send(@RequestParam String userId,
                             @RequestParam String message,
                             @RequestParam String type) {
        return service.send(userId, message, type);
    }

    @GetMapping("/my-notifications")
    @PreAuthorize("isAuthenticated()")
    public List<Notification> getMyNotifications(org.springframework.security.core.Authentication auth) {
        // On suppose que l'ID utilisateur est le "principal" ou qu'on le cherche par email
        // TODO: Adapter selon si vous stockez l'ID ou l'email dans la table notification
        // Pour l'instant, on peut utiliser un ID passé en paramètre temporairement si besoin
        // Mais l'idéal est d'utiliser l'email
        return service.getUserNotificationsByEmail(auth.getName());
    }
    @PutMapping("/read/{id}")
    @PreAuthorize("isAuthenticated()") // Ajout de l'autorisation explicite
    public Notification read(@PathVariable Long id) {
        return service.markAsRead(id);
    }

    // Autoriser Admin ou l'utilisateur lui-même (à améliorer plus tard)
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(@PathVariable Long id) {
        service.deleteNotification(id);
        return "Notification deleted successfully.";
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')") // Réservé admin
    public List<Notification> getAll() {
        return service.getAllNotifications();
    }
}