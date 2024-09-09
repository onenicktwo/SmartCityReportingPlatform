package coms309.issues;

import java.time.LocalDateTime;

public class Issue {
    private String id;
    private String description;
    private String category;
    private IssueStatus status;
    private String reporterName;
    private String location;
    private LocalDateTime reportedTime;

    public Issue() {
    }

    public Issue(String description, String category, String reporterName,
                 String location) {
        this.description = description;
        this.category = category;
        this.reporterName = reporterName;
        this.location = location;
        this.reportedTime = LocalDateTime.now();
    }

    public Issue(String id, String description, String category, IssueStatus status,
                 String reporterName, String location, LocalDateTime reportedTime) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.status = status;
        this.reporterName = reporterName;
        this.location = location;
        this.reportedTime = reportedTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getReportedTime() {
        return reportedTime;
    }

    public void setReportedTime(LocalDateTime reportedTime) {
        this.reportedTime = reportedTime;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", reporterName='" + reporterName + '\'' +
                ", location='" + location + '\'' +
                ", reportedTime=" + reportedTime +
                '}';
    }
}