package com.certipath.notification_service.event;

public class EvidenceValidatedEvent {

    private Long evidenceId;
    private Long userId;
    private String validatorName; // optional
    private String comment;       // optional

    public EvidenceValidatedEvent() {
    }

    public EvidenceValidatedEvent(Long evidenceId, Long userId, String validatorName, String comment) {
        this.evidenceId = evidenceId;
        this.userId = userId;
        this.validatorName = validatorName;
        this.comment = comment;
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

    public String getValidatorName() {
        return validatorName;
    }

    public void setValidatorName(String validatorName) {
        this.validatorName = validatorName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
