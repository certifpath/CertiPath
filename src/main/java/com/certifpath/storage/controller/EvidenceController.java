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

import java.util.List;

@RestController
@RequestMapping("/evidence")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow CORS for frontend
public class EvidenceController {

    private final EvidenceService evidenceService;

    /**
     * Create a new evidence with file upload
     * POST /evidence/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> createEvidence(
            @RequestParam("title") String title,
            @RequestParam("controlId") String controlId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("file") MultipartFile file) {
        
        try {
            Evidence evidence = evidenceService.createEvidence(title, description, controlId, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(evidence);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating evidence: " + e.getMessage());
        }
    }

    /**
     * List all evidences
     * GET /evidence/list
     */
    @GetMapping("/list")
    public ResponseEntity<List<Evidence>> listAllEvidences() {
        List<Evidence> evidences = evidenceService.listAllEvidences();
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
    public ResponseEntity<?> validateEvidence(@PathVariable Long id) {
        try {
            Evidence evidence = evidenceService.validateEvidence(id);
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
}
