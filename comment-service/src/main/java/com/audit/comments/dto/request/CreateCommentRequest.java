package com.audit.comments.dto.request;

import com.audit.comments.entity.CommentEntity.CommentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommentRequest {

    @NotBlank(message = "Le contenu du commentaire est obligatoire")
    @Size(max = 1000, message = "Le commentaire ne doit pas dépasser 1000 caractères")
    private String content;

    @NotBlank(message = "L'ID de l'audit est obligatoire")
    private String auditId;

    @NotBlank(message = "L'ID de l'exigence est obligatoire")
    private String requirementId;

    private String proofId;

    @NotNull(message = "Le type de commentaire est obligatoire")
    private CommentType type;
}