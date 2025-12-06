package com.audit.comments.mapper;

import com.audit.comments.dto.request.CreateCommentRequest;
import com.audit.comments.dto.request.UpdateCommentRequest;
import com.audit.comments.dto.response.CommentHistoryResponse;
import com.audit.comments.dto.response.CommentResponse;
import com.audit.comments.entity.CommentEntity;
import com.audit.comments.entity.CommentHistoryEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-06T19:37:05+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Ubuntu)"
)
@Component
public class CommentMapperImpl implements CommentMapper {

    @Override
    public CommentEntity toEntity(CreateCommentRequest request) {
        if ( request == null ) {
            return null;
        }

        CommentEntity.CommentEntityBuilder commentEntity = CommentEntity.builder();

        commentEntity.content( request.getContent() );
        commentEntity.auditId( request.getAuditId() );
        commentEntity.requirementId( request.getRequirementId() );
        commentEntity.proofId( request.getProofId() );
        commentEntity.type( request.getType() );

        commentEntity.resolved( false );

        return commentEntity.build();
    }

    @Override
    public CommentResponse toResponse(CommentEntity entity) {
        if ( entity == null ) {
            return null;
        }

        CommentResponse commentResponse = new CommentResponse();

        commentResponse.setId( entity.getId() );
        commentResponse.setContent( entity.getContent() );
        commentResponse.setAuditId( entity.getAuditId() );
        commentResponse.setRequirementId( entity.getRequirementId() );
        commentResponse.setProofId( entity.getProofId() );
        commentResponse.setResolved( entity.isResolved() );
        commentResponse.setResolvedAt( entity.getResolvedAt() );
        commentResponse.setResolvedBy( entity.getResolvedBy() );
        commentResponse.setType( entity.getType() );
        commentResponse.setCreatedBy( entity.getCreatedBy() );
        commentResponse.setCreatedAt( entity.getCreatedAt() );
        commentResponse.setUpdatedAt( entity.getUpdatedAt() );

        return commentResponse;
    }

    @Override
    public void updateEntity(UpdateCommentRequest request, CommentEntity entity) {
        if ( request == null ) {
            return;
        }

        entity.setContent( request.getContent() );
    }

    @Override
    public CommentHistoryResponse toHistoryResponse(CommentHistoryEntity entity) {
        if ( entity == null ) {
            return null;
        }

        CommentHistoryResponse commentHistoryResponse = new CommentHistoryResponse();

        commentHistoryResponse.setId( entity.getId() );
        commentHistoryResponse.setCommentId( entity.getCommentId() );
        commentHistoryResponse.setOldContent( entity.getOldContent() );
        commentHistoryResponse.setNewContent( entity.getNewContent() );
        commentHistoryResponse.setOldResolved( entity.getOldResolved() );
        commentHistoryResponse.setNewResolved( entity.getNewResolved() );
        commentHistoryResponse.setModifiedBy( entity.getModifiedBy() );
        commentHistoryResponse.setActionType( entity.getActionType() );
        commentHistoryResponse.setModifiedAt( entity.getModifiedAt() );

        return commentHistoryResponse;
    }
}
