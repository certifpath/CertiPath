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

    private Long extractUserId(String rawJson) {
        return 1L; // TODO: replace with real parsing
    }
}
