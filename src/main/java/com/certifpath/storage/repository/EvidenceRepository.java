package com.certifpath.storage.repository;

import com.certifpath.storage.entity.Evidence;
import com.certifpath.storage.entity.EvidenceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    
    List<Evidence> findByStatus(EvidenceStatus status);
    
    List<Evidence> findByControlId(String controlId);
    
    Long countByStatus(EvidenceStatus status);
}
