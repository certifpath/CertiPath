package com.appAuditza.Auditza.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String company;
    private String role;
    private String status;
    private String token;//still ba9i mazdtsh jwt
    private String message;
}