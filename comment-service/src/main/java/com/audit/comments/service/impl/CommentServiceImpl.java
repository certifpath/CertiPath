package com.audit.comments.service.impl;

import com.audit.comments.dto.request.CreateCommentRequest;
import com.audit.comments.dto.request.UpdateCommentRequest;
import com.audit.comments.dto.response.CommentResponse;
import com.audit.comments.entity.CommentEntity;
import com.audit.comments.entity.CommentHistoryEntity;
import com.audit.comments.exception.CommentNotFoundException;
import com.audit.comments.exception.UnauthorizedCommentAccessException;
import com.audit.comments.mapper.CommentMapper;
import com.audit.comments.repository.CommentHistoryRepository;
import com.audit.comments.repository.CommentRepository;
import com.audit.comments.service.CommentService;
import com.audit.comments.service.event.CommentEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentHistoryRepository commentHistoryRepository;
    private final CommentMapper commentMapper;
    private final CommentEventPublisher eventPublisher;

    @Override
    @Transactional
    public CommentResponse createComment(CreateCommentRequest request) {
        String currentUser = getCurrentUser();
        
        CommentEntity comment = commentMapper.toEntity(request);
        comment.setCreatedBy(currentUser);
        
        CommentEntity savedComment = commentRepository.save(comment);
        
        // Sauvegarder l'historique
        saveCommentHistory(savedComment, null, CommentHistoryEntity.ActionType.CREATED);
        
        // Publier l'événement
        eventPublisher.publishCommentCreated(savedComment);
        
        log.info("Comment created by {} for audit {}", currentUser, request.getAuditId());
        
        return commentMapper.toResponse(savedComment);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(String id, UpdateCommentRequest request) {
        String currentUser = getCurrentUser();
        
        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        
        validateCommentOwnership(comment, currentUser);
        
        // Sauvegarder l'ancien contenu pour l'historique
        String oldContent = comment.getContent();
        boolean oldResolved = comment.isResolved();
        
        commentMapper.updateEntity(request, comment);
        CommentEntity updatedComment = commentRepository.save(comment);
        
        // Sauvegarder l'historique
        saveCommentHistory(updatedComment, oldContent, oldResolved, 
                          CommentHistoryEntity.ActionType.UPDATED);
        
        // Publier l'événement
        eventPublisher.publishCommentUpdated(updatedComment);
        
        log.info("Comment {} updated by {}", id, currentUser);
        
        return commentMapper.toResponse(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(String id) {
        String currentUser = getCurrentUser();
        
        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        
        validateCommentOwnership(comment, currentUser);
        
        // Sauvegarder l'historique avant suppression
        saveCommentHistory(comment, null, CommentHistoryEntity.ActionType.DELETED);
        
        commentRepository.delete(comment);
        
        // Publier l'événement
        eventPublisher.publishCommentDeleted(comment);
        
        log.info("Comment {} deleted by {}", id, currentUser);
    }

    @Override
    @Transactional
    public CommentResponse resolveComment(String id) {
        String currentUser = getCurrentUser();
        
        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        
        if (comment.isResolved()) {
            throw new IllegalStateException("Le commentaire est déjà résolu");
        }
        
        boolean oldResolved = comment.isResolved();
        comment.setResolved(true);
        comment.setResolvedAt(LocalDateTime.now());
        comment.setResolvedBy(currentUser);
        
        CommentEntity resolvedComment = commentRepository.save(comment);
        
        // Sauvegarder l'historique
        saveCommentHistory(resolvedComment, null, oldResolved, 
                          CommentHistoryEntity.ActionType.RESOLVED);
        
        // Publier l'événement
        eventPublisher.publishCommentResolved(resolvedComment);
        
        log.info("Comment {} resolved by {}", id, currentUser);
        
        return commentMapper.toResponse(resolvedComment);
    }

    @Override
    @Transactional
    public CommentResponse reopenComment(String id) {
        String currentUser = getCurrentUser();
        
        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        
        if (!comment.isResolved()) {
            throw new IllegalStateException("Le commentaire n'est pas résolu");
        }
        
        boolean oldResolved = comment.isResolved();
        comment.setResolved(false);
        comment.setResolvedAt(null);
        comment.setResolvedBy(null);
        
        CommentEntity reopenedComment = commentRepository.save(comment);
        
        // Sauvegarder l'historique
        saveCommentHistory(reopenedComment, null, oldResolved, 
                          CommentHistoryEntity.ActionType.REOPENED);
        
        // Publier l'événement
        eventPublisher.publishCommentReopened(reopenedComment);
        
        log.info("Comment {} reopened by {}", id, currentUser);
        
        return commentMapper.toResponse(reopenedComment);
    }

    @Override
    public CommentResponse getCommentById(String id) {
        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        return commentMapper.toResponse(comment);
    }

    @Override
    public List<CommentResponse> getCommentsByAuditId(String auditId) {
        return commentRepository.findByAuditId(auditId).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getCommentsByAuditIdAndRequirementId(String auditId, String requirementId) {
        return commentRepository.findByAuditIdAndRequirementId(auditId, requirementId).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getCommentsByProofId(String proofId) {
        return commentRepository.findByProofId(proofId).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CommentResponse> getCommentsByAuditId(String auditId, Pageable pageable) {
        return commentRepository.findByAuditId(auditId, pageable)
                .map(commentMapper::toResponse);
    }

    @Override
    public List<CommentResponse> getUnresolvedCommentsByAuditId(String auditId) {
        return commentRepository.findUnresolvedCommentsByAuditId(auditId).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long countCommentsByAuditId(String auditId) {
        return commentRepository.countByAuditId(auditId);
    }

    @Override
    public long countResolvedCommentsByAuditId(String auditId) {
        return commentRepository.countByAuditIdAndResolved(auditId, true);
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private void validateCommentOwnership(CommentEntity comment, String currentUser) {
        if (!comment.getCreatedBy().equals(currentUser)) {
            throw new UnauthorizedCommentAccessException();
        }
    }

    private void saveCommentHistory(CommentEntity comment, String oldContent, 
                                   CommentHistoryEntity.ActionType actionType) {
        saveCommentHistory(comment, oldContent, null, actionType);
    }

    private void saveCommentHistory(CommentEntity comment, String oldContent, Boolean oldResolved,
                                   CommentHistoryEntity.ActionType actionType) {
        CommentHistoryEntity history = CommentHistoryEntity.builder()
                .commentId(comment.getId())
                .oldContent(oldContent)
                .newContent(comment.getContent())
                .oldResolved(oldResolved)
                .newResolved(comment.isResolved())
                .modifiedBy(getCurrentUser())
                .actionType(actionType)
                .build();
        
        commentHistoryRepository.save(history);
    }
}