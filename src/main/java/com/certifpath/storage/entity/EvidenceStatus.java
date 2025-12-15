package com.certifpath.storage.entity;

public enum EvidenceStatus {
    BROUILLON,      // Draft
    EN_ATTENTE,     // Pending (sent to auditor)
    VALIDEE,        // Validated by auditor
    REFUSEE         // Rejected by auditor
}
