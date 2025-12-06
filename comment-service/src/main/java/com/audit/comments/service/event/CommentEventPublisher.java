package com.audit.comments.service.event;

import com.audit.comments.entity.CommentEntity;

public interface CommentEventPublisher {
    
    void publishCommentCreated(CommentEntity comment);
    
    void publishCommentUpdated(CommentEntity comment);
    
    void publishCommentResolved(CommentEntity comment);
    
    void publishCommentReopened(CommentEntity comment);
    
    void publishCommentDeleted(CommentEntity comment);
}