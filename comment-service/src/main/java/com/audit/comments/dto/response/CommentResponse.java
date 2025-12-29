package com.audit.comments.dto.response;

import com.audit.comments.entity.CommentEntity.CommentType;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}