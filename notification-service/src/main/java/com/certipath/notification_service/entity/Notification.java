package com.certipath.notification_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    private String message;

    private String type;   // INFO, SUCCESS, WARNING, ERROR

    private String status; // UNREAD, READ

    private LocalDateTime createdAt = LocalDateTime.now();
}
