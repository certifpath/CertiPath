package com.appAuditza.Auditza.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/data")
    @PreAuthorize("hasRole('ADMIN')") // Seuls les utilisateurs avec le rôle ADMIN peuvent appeler cette méthode
    public ResponseEntity<String> getAdminData() {
        return ResponseEntity.ok("Données confidentielles pour les administrateurs.");
    }
}