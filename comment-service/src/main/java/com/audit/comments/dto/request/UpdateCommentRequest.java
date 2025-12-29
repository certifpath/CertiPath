package com.audit.comments.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCommentRequest {

    @NotBlank(message = "Le contenu du commentaire est obligatoire")
    @Size(max = 1000, message = "Le commentaire ne doit pas dépasser 1000 caractères")
    private String content;
}