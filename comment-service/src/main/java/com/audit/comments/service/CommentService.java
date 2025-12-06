package com.audit.comments.service;

import com.audit.comments.dto.request.CreateCommentRequest;
import com.audit.comments.dto.request.UpdateCommentRequest;
import com.audit.comments.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    
    CommentResponse createComment(CreateCommentRequest request);
    
    CommentResponse updateComment(String id, UpdateCommentRequest request);
    
    void deleteComment(String id);
    
    CommentResponse getCommentById(String id);
    
    List<CommentResponse> getCommentsByAuditId(String auditId);
    
    List<CommentResponse> getCommentsByAuditIdAndRequirementId(String auditId, String requirementId);
    
    List<CommentResponse> getCommentsByProofId(String proofId);
    
    Page<CommentResponse> getCommentsByAuditId(String auditId, Pageable pageable);
    
    CommentResponse resolveComment(String id);
    
    CommentResponse reopenComment(String id);
    
    List<CommentResponse> getUnresolvedCommentsByAuditId(String auditId);
    
    long countCommentsByAuditId(String auditId);
    
    long countResolvedCommentsByAuditId(String auditId);
}