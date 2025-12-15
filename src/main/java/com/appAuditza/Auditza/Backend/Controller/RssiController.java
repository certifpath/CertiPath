package com.appAuditza.Auditza.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rssi")
public class RssiController {
    @GetMapping("/data")
    @PreAuthorize("hasRole('RSSI')or hasRole('USER')")
    public ResponseEntity<String> getSharedData() {
       return ResponseEntity.ok("Données partagées pour les Auditeurs et Utilisateurs.");
    }
}
