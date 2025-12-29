package com.certipath.notification_service.listener;

import com.certipath.notification_service.dto.NotificationEvent;
import com.certipath.notification_service.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationKafkaListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public NotificationKafkaListener(NotificationService notificationService,
                                     ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "notification-events", groupId = "notification-service-group")
    public void handleNotificationMessage(String message) {
        try {
            // JSON -> NotificationEvent
            NotificationEvent event =
                    objectMapper.readValue(message, NotificationEvent.class);

            System.out.println("[Kafka] Received event: " + message);

            // Reuse your existing business logic
            notificationService.send(
                    String.valueOf(event.getUserId()), // FIX: Convert Long to String
                    event.getMessage(),
                    event.getType()
            );
        } catch (Exception e) {
            System.err.println("[Kafka] Failed to process message: " + message);
            e.printStackTrace();
        }
    }
}