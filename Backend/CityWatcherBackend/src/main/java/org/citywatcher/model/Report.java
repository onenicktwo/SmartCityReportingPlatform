package org.citywatcher.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private Date timestamp;

    public Report() {
    }

    public Report(Comment comment, User reporter, String reason) {
        this.comment = comment;
        this.reporter = reporter;
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporterId) {
        this.reporter = reporterId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getReportTime() {
        return  timestamp;
    }

    @PrePersist
    protected void onCreate() {
        timestamp = new Date();
    }
}