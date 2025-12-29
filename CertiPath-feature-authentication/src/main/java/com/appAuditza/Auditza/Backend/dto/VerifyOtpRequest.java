package com.appAuditza.Auditza.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequest {
    private String email;
    private int otpCode;
    private String qrCodeFileName;
}