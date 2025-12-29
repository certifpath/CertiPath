package com.audit.comments.repository;

import com.audit.comments.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, String> {

    List<CommentEntity> findByAuditId(String auditId);

    List<CommentEntity> findByAuditIdAndRequirementId(String auditId, String requirementId);

    List<CommentEntity> findByProofId(String proofId);

    List<CommentEntity> findByCreatedBy(String userId);

    Page<CommentEntity> findByAuditId(String auditId, Pageable pageable);

    @Query("SELECT c FROM CommentEntity c WHERE c.auditId = :auditId AND c.resolved = false")
    List<CommentEntity> findUnresolvedCommentsByAuditId(@Param("auditId") String auditId);

    @Query("SELECT c FROM CommentEntity c WHERE c.proofId = :proofId AND c.type = 'PROOF_COMMENT'")
    List<CommentEntity> findProofComments(@Param("proofId") String proofId);

    Optional<CommentEntity> findByIdAndCreatedBy(String id, String userId);

    long countByAuditId(String auditId);

    long countByAuditIdAndResolved(String auditId, boolean resolved);
}