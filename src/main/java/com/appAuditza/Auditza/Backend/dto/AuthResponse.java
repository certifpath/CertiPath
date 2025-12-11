package com.appAuditza.Auditza.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String company;
    private String role;
    private String status;
    private String token;
    private String message;
    private boolean otpSetupRequired;
    private String nextStep;
    private String qrCodeImage;
}