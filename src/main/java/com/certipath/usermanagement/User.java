package com.certipath.usermanagement.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullname;
    private String email;
    private String role;       // "RSSI" or "AUDITOR"
    private boolean active;
}
