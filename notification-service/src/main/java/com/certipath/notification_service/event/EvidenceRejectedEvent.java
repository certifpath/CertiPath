package com.certipath.notification_service.event;

public class EvidenceRejectedEvent {

    private Long evidenceId;
    private Long userId;
    private String reason;

    public EvidenceRejectedEvent() {
    }

    public EvidenceRejectedEvent(Long evidenceId, Long userId, String reason) {
        this.evidenceId = evidenceId;
        this.userId = userId;
        this.reason = reason;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
