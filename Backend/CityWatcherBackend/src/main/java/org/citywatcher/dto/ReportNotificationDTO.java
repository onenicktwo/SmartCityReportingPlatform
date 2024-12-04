package org.citywatcher.dto;

import org.citywatcher.model.Report;
import org.citywatcher.model.User;

import java.util.Date;

public class ReportNotificationDTO {
    private Long reportId;
    private Long commentId;
    private User reporter;
    private String reason;
    private Date timestamp;

    public ReportNotificationDTO(Report report) {
        this.reportId = report.getId();
        this.commentId = report.getComment().getId();
        this.reporter = report.getReporter();
        this.reason = report.getReason();
        this.timestamp = report.getReportTime();
    }

    // Getters and setters
    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getTimeStamp() {
        return timestamp;
    }

    public void setTimeStamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}