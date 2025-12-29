package com.audit.comments.mapper;

import com.audit.comments.dto.request.CreateCommentRequest;
import com.audit.comments.dto.request.UpdateCommentRequest;
import com.audit.comments.dto.response.CommentHistoryResponse;
import com.audit.comments.dto.response.CommentResponse;
import com.audit.comments.entity.CommentEntity;
import com.audit.comments.entity.CommentHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "resolved", constant = "false")
    @Mapping(target = "resolvedAt", ignore = true)
    @Mapping(target = "resolvedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    CommentEntity toEntity(CreateCommentRequest request);

    CommentResponse toResponse(CommentEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "auditId", ignore = true)
    @Mapping(target = "requirementId", ignore = true)
    @Mapping(target = "proofId", ignore = true)
    @Mapping(target = "resolved", ignore = true)
    @Mapping(target = "resolvedAt", ignore = true)
    @Mapping(target = "resolvedBy", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntity(UpdateCommentRequest request, @MappingTarget CommentEntity entity);

    CommentHistoryResponse toHistoryResponse(CommentHistoryEntity entity);
}