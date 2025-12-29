package com.certipath.notification_service.listener;

import com.certipath.notification_service.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationEventListener {

    private final NotificationService service;

    public NotificationEventListener(NotificationService service) {
        this.service = service;
    }

    @KafkaListener(topics = "EVIDENCE_SUBMITTED")
    public void handleEvidenceSubmitted(String messageJson) {
        service.send(extractUserId(messageJson),
                "A new evidence has been submitted.",
                "INFO");
    }

    private String extractUserId(String rawJson) {
        // FIX: Return a String, not a Long (1L)
        // In the future, use ObjectMapper to parse the JSON and get the real email
        return "rssi@admin.com";
    }
}