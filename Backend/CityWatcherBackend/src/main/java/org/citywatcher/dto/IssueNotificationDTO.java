package org.citywatcher.dto;

import org.citywatcher.model.Issue;
import org.citywatcher.model.IssueStatus;

public class IssueNotificationDTO {
    private Long issueId;
    private String title;
    private String description;
    private String category;
    private IssueStatus status;
    private String notificationType; // Can be "UPDATE", "ASSIGNMENT", etc.
    private String assignedOfficialUsername; // Only populated for assignments

    public IssueNotificationDTO(Issue issue, String notificationType) {
        this.issueId = issue.getId();
        this.title = issue.getTitle();
        this.description = issue.getDescription();
        this.category = issue.getCategory();
        this.status = issue.getStatus();
        this.notificationType = notificationType;
        if (issue.getAssignedOfficial() != null) {
            this.assignedOfficialUsername = issue.getAssignedOfficial().getUsername();
        }
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getAssignedOfficialUsername() {
        return assignedOfficialUsername;
    }

    public void setAssignedOfficialUsername(String assignedOfficialUsername) {
        this.assignedOfficialUsername = assignedOfficialUsername;
    }
}