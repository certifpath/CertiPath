package com.audit.comments.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_comment_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CommentHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "comment_id", nullable = false)
    private String commentId;

    @Column(name = "old_content", length = 1000)
    private String oldContent;

    @Column(name = "new_content", length = 1000)
    private String newContent;

    @Column(name = "old_resolved")
    private Boolean oldResolved;

    @Column(name = "new_resolved")
    private Boolean newResolved;

    @Column(name = "modified_by", nullable = false)
    private String modifiedBy;

    @Column(name = "action_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @CreatedDate
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    public enum ActionType {
        CREATED,
        UPDATED,
        RESOLVED,
        REOPENED,
        DELETED
    }
}