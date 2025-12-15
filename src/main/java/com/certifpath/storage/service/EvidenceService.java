package com.certifpath.storage.service;

import com.certifpath.storage.entity.Evidence;
import com.certifpath.storage.entity.EvidenceStatus;
import com.certifpath.storage.entity.FileMetadata;
import com.certifpath.storage.repository.EvidenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final StorageService storageService;

    /**
     * Create a new evidence with file upload
     */
    public Evidence createEvidence(String title, String description, String controlId, 
                                   MultipartFile file) throws Exception {
        
        // Upload file to MinIO
        FileMetadata fileMetadata = storageService.upload(file);
        
        // Create evidence entity
        Evidence evidence = new Evidence();
        evidence.setTitle(title);
        evidence.setDescription(description);
        evidence.setControlId(controlId);
        evidence.setFileId(fileMetadata.getStoredName());
        evidence.setStatus(EvidenceStatus.BROUILLON);
        evidence.setCreatedBy("RSSI Admin"); // TODO: Get from security context
        
        return evidenceRepository.save(evidence);
    }

    /**
     * List all evidences
     */
    public List<Evidence> listAllEvidences() {
        return evidenceRepository.findAll();
    }

    /**
     * Get evidence by ID
     */
    public Evidence getEvidenceById(Long id) {
        return evidenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evidence not found with id: " + id));
    }

    /**
     * Get evidences by status
     */
    public List<Evidence> getEvidencesByStatus(EvidenceStatus status) {
        return evidenceRepository.findByStatus(status);
    }

    /**
     * Get evidences by control ID
     */
    public List<Evidence> getEvidencesByControlId(String controlId) {
        return evidenceRepository.findByControlId(controlId);
    }

    /**
     * Send evidence to auditor (change status to EN_ATTENTE)
     */
    public Evidence sendToAuditor(Long id) {
        Evidence evidence = getEvidenceById(id);
        
        if (evidence.getStatus() != EvidenceStatus.BROUILLON) {
            throw new RuntimeException("Only draft evidences can be sent to auditor");
        }
        
        evidence.setStatus(EvidenceStatus.EN_ATTENTE);
        evidence.setUpdatedBy("RSSI Admin"); // TODO: Get from security context
        
        return evidenceRepository.save(evidence);
    }

    /**
     * Validate evidence (auditor action)
     */
    public Evidence validateEvidence(Long id) {
        Evidence evidence = getEvidenceById(id);
        evidence.setStatus(EvidenceStatus.VALIDEE);
        evidence.setUpdatedBy("Auditor"); // TODO: Get from security context
        
        return evidenceRepository.save(evidence);
    }

    /**
     * Reject evidence (auditor action)
     */
    public Evidence rejectEvidence(Long id) {
        Evidence evidence = getEvidenceById(id);
        evidence.setStatus(EvidenceStatus.REFUSEE);
        evidence.setUpdatedBy("Auditor"); // TODO: Get from security context
        
        return evidenceRepository.save(evidence);
    }

    /**
     * Delete evidence and associated file
     */
    public void deleteEvidence(Long id) throws Exception {
        Evidence evidence = getEvidenceById(id);
        
        // Delete file from MinIO
        try {
            storageService.deleteFile(evidence.getFileId());
        } catch (Exception e) {
            // Log error but continue with evidence deletion
            System.err.println("Error deleting file: " + e.getMessage());
        }
        
        // Delete evidence metadata
        evidenceRepository.delete(evidence);
    }

    /**
     * Download evidence file
     */
    public byte[] downloadEvidenceFile(Long id) throws Exception {
        Evidence evidence = getEvidenceById(id);
        return storageService.downloadFile(evidence.getFileId());
    }

    /**
     * Get statistics
     */
    public EvidenceStatistics getStatistics() {
        long total = evidenceRepository.count();
        long draft = evidenceRepository.countByStatus(EvidenceStatus.BROUILLON);
        long pending = evidenceRepository.countByStatus(EvidenceStatus.EN_ATTENTE);
        long validated = evidenceRepository.countByStatus(EvidenceStatus.VALIDEE);
        long rejected = evidenceRepository.countByStatus(EvidenceStatus.REFUSEE);
        
        return new EvidenceStatistics(total, draft, pending, validated, rejected);
    }

    /**
     * Inner class for statistics
     */
    public record EvidenceStatistics(
            long total,
            long draft,
            long pending,
            long validated,
            long rejected
    ) {}
}
