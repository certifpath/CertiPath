package com.audit.comments.dto.response;

import com.audit.comments.entity.CommentEntity.CommentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {

    private String id;
    private String content;
    private String auditId;
    private String requirementId;
    private String proofId;
    private boolean resolved;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private CommentType type;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}