package com.certifpath.storage.service;

import com.certifpath.storage.entity.Evidence;
import com.certifpath.storage.entity.EvidenceStatus;
import com.certifpath.storage.entity.FileMetadata;
import com.certifpath.storage.repository.EvidenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.certifpath.storage.client.NotificationClient;
import java.util.List;
import com.certifpath.storage.client.AuthClient;
@Service
@RequiredArgsConstructor
@Transactional
public class EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final StorageService storageService;
    private final NotificationClient notificationClient;
    private final AuthClient authClient;
    /**
     * Create a new evidence with file upload
     */

    public Evidence createEvidence(String title, String description, String controlId, 
                                   MultipartFile file, String username, String companyName) throws Exception {
        
        // Upload file to MinIO
        FileMetadata fileMetadata = storageService.upload(file);
        
        // Create evidence entity
        Evidence evidence = new Evidence();
        evidence.setTitle(title);
        evidence.setDescription(description);
        evidence.setControlId(controlId);
        evidence.setFileId(fileMetadata.getStoredName());
        evidence.setStatus(EvidenceStatus.BROUILLON);
        evidence.setCreatedBy(username);
        evidence.setCompanyName(companyName);
        
        return evidenceRepository.save(evidence);
    }

    public List<Evidence> getEvidencesForUser(String username, boolean isAuditorOrAdmin) {
        if (isAuditorOrAdmin) {
            // L'auditeur voit tout
            return evidenceRepository.findAll();
        } else {
            // Le RSSI ne voit que ce qu'il a créé
            return evidenceRepository.findByCreatedBy(username);
        }
    }

    public List<String> getAllCompanies() {
        return evidenceRepository.findDistinctCompanyNames();
    }

    /**
     * Récupérer les preuves d'une entreprise spécifique
     */
    public List<Evidence> getEvidencesByCompany(String companyName) {
        return evidenceRepository.findByCompanyName(companyName);
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
        evidence.setUpdatedBy("RSSI"); // TODO: Get from security context
        try {
            // 1. Demander la liste des emails au Auth Service
            List<String> auditorEmails = authClient.getAuditorEmails();

            String message = "Nouvelle preuve soumise : " + evidence.getTitle();

            // 2. Envoyer une notification à CHAQUE auditeur
            for (String email : auditorEmails) {
                notificationClient.sendNotification(email, message, "INFO");
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de la notification des auditeurs: " + e.getMessage());
            // On log l'erreur mais on ne bloque pas le processus
        }
        return evidenceRepository.save(evidence);

    }

    /**
     * Validate evidence (auditor action)
     */
    public Evidence validateEvidence(Long id, String username) {
        Evidence evidence = getEvidenceById(id);
        evidence.setStatus(EvidenceStatus.VALIDEE);
        evidence.setUpdatedBy(username); //

        try {
            // userId = l'email du créateur (RSSI)
            String message = "Votre preuve '" + evidence.getTitle() + "' a été validée par l'auditeur.";
            notificationClient.sendNotification(evidence.getCreatedBy(), message, "SUCCESS");
        } catch (Exception e) {
            System.err.println("Erreur envoi notification: " + e.getMessage());
            // On ne bloque pas la validation si la notif échoue
        }
        return evidenceRepository.save(evidence);
    }

    /**
     * Reject evidence (auditor action)
     */
    public Evidence rejectEvidence(Long id) {
        Evidence evidence = getEvidenceById(id);
        evidence.setStatus(EvidenceStatus.REFUSEE);
        evidence.setUpdatedBy("Auditor"); // TODO: Get from security context

        try {
            String message = "Votre preuve '" + evidence.getTitle() + "' a été refusée. Veuillez vérifier les commentaires.";
            notificationClient.sendNotification(evidence.getCreatedBy(), message, "WARNING");
        } catch (Exception e) {
            System.err.println("Erreur envoi notification: " + e.getMessage());
        }
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
