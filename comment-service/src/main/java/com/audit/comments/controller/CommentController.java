package com.audit.comments.controller;

import com.audit.comments.dto.request.CreateCommentRequest;
import com.audit.comments.dto.request.UpdateCommentRequest;
import com.audit.comments.dto.response.CommentResponse;
import com.audit.comments.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Tag(name = "Comment Management", description = "API de gestion des commentaires d'audit")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Créer un nouveau commentaire")
    @PreAuthorize("hasAnyRole('RSSI', 'USER','AUDITEUR', 'ADMIN', 'REVIEWER')")
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un commentaire")
    @PreAuthorize("hasAnyRole('RSSI', 'USER','AUDITEUR', 'ADMIN', 'REVIEWER')")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable String id,
            @Valid @RequestBody UpdateCommentRequest request) {
        return ResponseEntity.ok(commentService.updateComment(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un commentaire")
    @PreAuthorize("hasAnyRole('RSSI', 'USER','AUDITEUR', 'ADMIN', 'REVIEWER')")
    public ResponseEntity<Void> deleteComment(@PathVariable String id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un commentaire par son ID")
    @PreAuthorize("hasAnyRole('RSSI', 'USER','AUDITEUR', 'ADMIN', 'REVIEWER', 'VIEWER')")
    public ResponseEntity<CommentResponse> getComment(@PathVariable String id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    @GetMapping("/audit/{auditId}")
    @Operation(summary = "Récupérer tous les commentaires d'un audit")
    @PreAuthorize("hasAnyRole('RSSI', 'USER','AUDITEUR', 'ADMIN', 'REVIEWER', 'VIEWER')")
    public ResponseEntity<List<CommentResponse>> getCommentsByAudit(@PathVariable String auditId) {
        return ResponseEntity.ok(commentService.getCommentsByAuditId(auditId));
    }

    @GetMapping("/audit/{auditId}/paged")
    @Operation(summary = "Récupérer les commentaires d'un audit avec pagination")
    @PreAuthorize("hasAnyRole('RSSI', 'USER','AUDITEUR', 'ADMIN', 'REVIEWER', 'VIEWER')")
    public ResponseEntity<Page<CommentResponse>> getCommentsByAuditPaged(
            @PathVariable String auditId,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByAuditId(auditId, pageable));
    }

    @GetMapping("/audit/{auditId}/requirement/{requirementId}")
    @Operation(summary = "Récupérer les commentaires d'une exigence d'audit")
    @PreAuthorize("hasAnyRole('RSSI', 'USER','AUDITEUR', 'ADMIN', 'REVIEWER', 'VIEWER')")
    public ResponseEntity<List<CommentResponse>> getCommentsByRequirement(
            @PathVariable String auditId,
            @PathVariable String requirementId) {
        return ResponseEntity.ok(commentService.getCommentsByAuditIdAndRequirementId(auditId, requirementId));
    }

    @GetMapping("/proof/{proofId}")
    @Operation(summary = "Récupérer les commentaires d'une preuve")
    @PreAuthorize("hasAnyRole('RSSI', 'USER','AUDITEUR', 'ADMIN', 'REVIEWER', 'VIEWER')")
    public ResponseEntity<List<CommentResponse>> getCommentsByProof(@PathVariable String proofId) {
        return ResponseEntity.ok(commentService.getCommentsByProofId(proofId));
    }

    @PatchMapping("/{id}/resolve")
    @Operation(summary = "Marquer un commentaire comme résolu")
    @PreAuthorize("hasAnyRole('AUDITEUR', 'ADMIN', 'REVIEWER')")
    public ResponseEntity<CommentResponse> resolveComment(@PathVariable String id) {
        return ResponseEntity.ok(commentService.resolveComment(id));
    }

    @PatchMapping("/{id}/reopen")
    @Operation(summary = "Réouvrir un commentaire résolu")
    @PreAuthorize("hasAnyRole('AUDITEUR', 'ADMIN', 'REVIEWER')")
    public ResponseEntity<CommentResponse> reopenComment(@PathVariable String id) {
        return ResponseEntity.ok(commentService.reopenComment(id));
    }

    @GetMapping("/audit/{auditId}/unresolved")
    @Operation(summary = "Récupérer les commentaires non résolus d'un audit")
    @PreAuthorize("hasAnyRole('AUDITOR', 'ADMIN', 'REVIEWER', 'VIEWER')")
    public ResponseEntity<List<CommentResponse>> getUnresolvedComments(@PathVariable String auditId) {
        return ResponseEntity.ok(commentService.getUnresolvedCommentsByAuditId(auditId));
    }

    @GetMapping("/audit/{auditId}/stats")
    @Operation(summary = "Récupérer les statistiques des commentaires d'un audit")
    @PreAuthorize("hasAnyRole('AUDITEUR', 'ADMIN', 'REVIEWER', 'VIEWER')")
    public ResponseEntity<CommentStatsResponse> getCommentStats(@PathVariable String auditId) {
        long total = commentService.countCommentsByAuditId(auditId);
        long resolved = commentService.countResolvedCommentsByAuditId(auditId);
        
        CommentStatsResponse stats = new CommentStatsResponse(total, resolved, total - resolved);
        return ResponseEntity.ok(stats);
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    private static class CommentStatsResponse {
        private long totalComments;
        private long resolvedComments;
        private long unresolvedComments;
    }
}