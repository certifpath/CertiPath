package com.audit.comments.service.event;

import com.audit.comments.entity.CommentEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NatsCommentEventPublisher implements CommentEventPublisher {

    private final Connection natsConnection;
    private final ObjectMapper objectMapper;

    @Override
    public void publishCommentCreated(CommentEntity comment) {
        publishEvent("COMMENT_CREATED", comment, Map.of(
            "auditId", comment.getAuditId(),
            "requirementId", comment.getRequirementId(),
            "type", comment.getType().toString()
        ));
    }

    @Override
    public void publishCommentUpdated(CommentEntity comment) {
        publishEvent("COMMENT_UPDATED", comment, Map.of(
            "auditId", comment.getAuditId(),
            "resolved", comment.isResolved()
        ));
    }

    @Override
    public void publishCommentResolved(CommentEntity comment) {
        publishEvent("COMMENT_RESOLVED", comment, Map.of(
            "auditId", comment.getAuditId(),
            "resolvedBy", comment.getResolvedBy(),
            "resolvedAt", comment.getResolvedAt()
        ));
    }

    @Override
    public void publishCommentReopened(CommentEntity comment) {
        publishEvent("COMMENT_REOPENED", comment, Map.of(
            "auditId", comment.getAuditId()
        ));
    }

    @Override
    public void publishCommentDeleted(CommentEntity comment) {
        publishEvent("COMMENT_DELETED", comment, Map.of(
            "auditId", comment.getAuditId(),
            "requirementId", comment.getRequirementId()
        ));
    }

    private void publishEvent(String eventType, CommentEntity comment, Map<String, Object> additionalData) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", eventType);
            event.put("timestamp", System.currentTimeMillis());
            event.put("commentId", comment.getId());
            event.put("createdBy", comment.getCreatedBy());
            event.putAll(additionalData);

            String eventJson = objectMapper.writeValueAsString(event);
            natsConnection.publish("audit.comments.events", eventJson.getBytes(StandardCharsets.UTF_8));
            
            log.info("Published {} event for comment {}", eventType, comment.getId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event for comment {}", comment.getId(), e);
        }
    }
}