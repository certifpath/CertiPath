package com.audit.comments.dto.response;

import com.audit.comments.entity.CommentHistoryEntity.ActionType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentHistoryResponse {

    private String id;
    private String commentId;
    private String oldContent;
    private String newContent;
    private Boolean oldResolved;
    private Boolean newResolved;
    private String modifiedBy;
    private ActionType actionType;
    private LocalDateTime modifiedAt;
}