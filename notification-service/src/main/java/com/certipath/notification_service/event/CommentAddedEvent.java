package com.certipath.notification_service.event;

public class CommentAddedEvent {

    private Long commentId;
    private Long evidenceId;
    private Long userId;      // who should be notified
    private String author;    // who wrote the comment
    private String content;

    public CommentAddedEvent() {
    }

    public CommentAddedEvent(Long commentId, Long evidenceId, Long userId, String author, String content) {
        this.commentId = commentId;
        this.evidenceId = evidenceId;
        this.userId = userId;
        this.author = author;
        this.content = content;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(Long evidenceId) {
        this.evidenceId = evidenceId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
