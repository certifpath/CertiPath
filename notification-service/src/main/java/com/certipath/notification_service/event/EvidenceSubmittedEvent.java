package com.certipath.notification_service.event;

public class EvidenceSubmittedEvent {

    private Long evidenceId;
    private Long userId;     // owner of the evidence
    private String title;
    private String control;  // e.g. control name / code

    public EvidenceSubmittedEvent() {
    }

    public EvidenceSubmittedEvent(Long evidenceId, Long userId, String title, String control) {
        this.evidenceId = evidenceId;
        this.userId = userId;
        this.title = title;
        this.control = control;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getControl() {
        return control;
    }

    public void setControl(String control) {
        this.control = control;
    }
}
