package com.certifpath.storage.repository;

import com.certifpath.storage.entity.Evidence;
import com.certifpath.storage.entity.EvidenceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    
    List<Evidence> findByStatus(EvidenceStatus status);
    List<Evidence> findByCompanyName(String companyName);

    // Pour avoir la liste des entreprises uniques (pour le menu déroulant)
    @Query("SELECT DISTINCT e.companyName FROM Evidence e WHERE e.companyName IS NOT NULL")
    List<String> findDistinctCompanyNames();
    List<Evidence> findByControlId(String controlId);
    List<Evidence> findByCreatedBy(String createdBy);
    Long countByStatus(EvidenceStatus status);
}
