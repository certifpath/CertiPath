package com.appAuditza.Auditza.Backend.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auditeur")
public class AuditeurController {

    @GetMapping("/data")
    @PreAuthorize("hasRole('AUDITEUR')") 
    public ResponseEntity<String> getAuditeurData() {
        return ResponseEntity.ok("Données confidentielles pour les Auditeurs");
    }
}