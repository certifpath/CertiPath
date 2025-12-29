package com.audit.comments.controller;

import com.audit.comments.dto.response.CommentHistoryResponse;
import com.audit.comments.repository.CommentHistoryRepository;
import com.audit.comments.mapper.CommentMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/comment-history")
@RequiredArgsConstructor
@Tag(name = "Comment History", description = "API de consultation de l'historique des commentaires")
public class CommentHistoryController {

    private final CommentHistoryRepository commentHistoryRepository;
    private final CommentMapper commentMapper;

    @GetMapping("/comment/{commentId}")
    @Operation(summary = "Récupérer l'historique d'un commentaire")
    @PreAuthorize("hasAnyRole('AUDITOR', 'ADMIN', 'REVIEWER')")
    public ResponseEntity<List<CommentHistoryResponse>> getCommentHistory(
            @PathVariable String commentId) {
        List<CommentHistoryResponse> history = commentHistoryRepository
                .findByCommentIdOrderByModifiedAtDesc(commentId)
                .stream()
                .map(commentMapper::toHistoryResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(history);
    }
}