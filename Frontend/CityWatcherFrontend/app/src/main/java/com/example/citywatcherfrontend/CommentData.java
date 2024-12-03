package com.example.citywatcherfrontend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentData {
    private int id;
    @JsonProperty("issue_id")
    private int issueId;
    private UserInfo commenter = new UserInfo();
    private String content;
    @JsonProperty("timestamp")
    private Date date;
    @JsonProperty("is_internal_note")
    private boolean isInternalNote;

    public CommentData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserInfo getCommenter() {
        return commenter;
    }

    public void setCommenter(UserInfo commenter) {
        this.commenter = commenter;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }

    public boolean isInternalNote() {
        return isInternalNote;
    }

    public void setInternalNote(boolean internalNote) {
        isInternalNote = internalNote;
    }
}
