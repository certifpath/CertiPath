package com.certifpath.storage.controller;

import com.certifpath.storage.entity.Evidence;
import com.certifpath.storage.entity.EvidenceStatus;
import com.certifpath.storage.service.EvidenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import jakarta.servlet.http.HttpServletRequest;
import com.certifpath.storage.security.JwtService;

import java.util.List;

@RestController
@RequestMapping("/evidence")
@RequiredArgsConstructor
public class EvidenceController {

    private final EvidenceService evidenceService;
    private final JwtService jwtService;

    /**
     * Create a new evidence with file upload
     * POST /evidence/create
     */
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('RSSI', 'USER')")
    public ResponseEntity<?> createEvidence(
            @RequestParam("title") String title,
            @RequestParam("controlId") String controlId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("file") MultipartFile file,
            Authentication authentication,
            HttpServletRequest request) { // Ajouter HttpServletRequest pour lire le header brut

        try {
            String userEmail = authentication.getName();

            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String token = authHeader.substring(7);

            String companyName = jwtService.extractCompany(token);

            // 3. Passer companyName au service
            Evidence evidence = evidenceService.createEvidence(title, description, controlId, file, userEmail, companyName);
            return ResponseEntity.status(HttpStatus.CREATED).body(evidence);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    /**
     * List all evidences
     * GET /evidence/list
     */
    @GetMapping("/list")
    public ResponseEntity<List<Evidence>> listAllEvidences(Authentication authentication) {
        String currentUser = authentication.getName();

        boolean hasFullAccess = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AUDITEUR")
                        || a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_AUDITOR")); // Au cas où
        List<Evidence> evidences = evidenceService.getEvidencesForUser(currentUser, hasFullAccess);

        return ResponseEntity.ok(evidences);
    }

    /**
     * Get evidence by ID
     * GET /evidence/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getEvidenceById(@PathVariable Long id) {
        try {
            Evidence evidence = evidenceService.getEvidenceById(id);
            return ResponseEntity.ok(evidence);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Get evidences by status
     * GET /evidence/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Evidence>> getEvidencesByStatus(@PathVariable String status) {
        try {
            EvidenceStatus evidenceStatus = EvidenceStatus.valueOf(status.toUpperCase());
            List<Evidence> evidences = evidenceService.getEvidencesByStatus(evidenceStatus);
            return ResponseEntity.ok(evidences);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get evidences by control ID
     * GET /evidence/control/{controlId}
     */
    @GetMapping("/control/{controlId}")
    public ResponseEntity<List<Evidence>> getEvidencesByControlId(@PathVariable String controlId) {
        List<Evidence> evidences = evidenceService.getEvidencesByControlId(controlId);
        return ResponseEntity.ok(evidences);
    }

    /**
     * Send evidence to auditor (change status to EN_ATTENTE)
     * PUT /evidence/{id}/send
     */
    @PutMapping("/{id}/send")
    @PreAuthorize("hasAnyRole('RSSI', 'USER')")
    public ResponseEntity<?> sendToAuditor(@PathVariable Long id) {
        try {
            Evidence evidence = evidenceService.sendToAuditor(id);
            return ResponseEntity.ok(evidence);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Validate evidence (auditor action)
     * PUT /evidence/{id}/validate
     */

    @PutMapping("/{id}/validate")
    @PreAuthorize("hasRole('AUDITEUR')")
    public ResponseEntity<?> validateEvidence(@PathVariable Long id, Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            Evidence evidence = evidenceService.validateEvidence(id, userEmail);
            return ResponseEntity.ok(evidence);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Reject evidence (auditor action)
     * PUT /evidence/{id}/reject
     */
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('AUDITEUR')")
    public ResponseEntity<?> rejectEvidence(@PathVariable Long id) {
        try {
            Evidence evidence = evidenceService.rejectEvidence(id);
            return ResponseEntity.ok(evidence);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Download evidence file
     * GET /evidence/{id}/download
     */
    @GetMapping("/{id}/download")
    @PreAuthorize("hasRole('AUDITEUR')")
    public ResponseEntity<?> downloadEvidence(@PathVariable Long id) {
        try {
            Evidence evidence = evidenceService.getEvidenceById(id);
            byte[] fileData = evidenceService.downloadEvidenceFile(id);
            
            // Extract file extension from fileId
            String fileName = evidence.getTitle() + getFileExtension(evidence.getFileId());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error downloading file: " + e.getMessage());
        }
    }

    /**
     * Delete evidence
     * DELETE /evidence/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvidence(@PathVariable Long id) {
        try {
            evidenceService.deleteEvidence(id);
            return ResponseEntity.ok("Evidence deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting evidence: " + e.getMessage());
        }
    }

    /**
     * Get statistics
     * GET /evidence/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<EvidenceService.EvidenceStatistics> getStatistics() {
        EvidenceService.EvidenceStatistics stats = evidenceService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Helper method to extract file extension
     */
    private String getFileExtension(String fileId) {
        int lastIndexOf = fileId.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // No extension found
        }
        return fileId.substring(lastIndexOf);
    }
    @GetMapping("/companies")
    @PreAuthorize("hasRole('AUDITEUR')")
    public ResponseEntity<List<String>> getCompanies() {
        return ResponseEntity.ok(evidenceService.getAllCompanies());
    }

    // Filtrer par entreprise
    @GetMapping("/company/{companyName}")
    @PreAuthorize("hasRole('AUDITEUR')")
    public ResponseEntity<List<Evidence>> getEvidencesByCompany(@PathVariable String companyName) {
        return ResponseEntity.ok(evidenceService.getEvidencesByCompany(companyName));
    }
}
