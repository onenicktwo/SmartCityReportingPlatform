package org.citywatcher.dto;

import org.citywatcher.model.IssueStatus;

import java.util.Map;

public class StatusStats {
    private int totalIssues;
    private Map<IssueStatus, Integer> issuesByStatus;
    private Map<IssueStatus, Double> percentageByStatus;
    private double averageResolutionTime; // in hours

    public int getTotalIssues() {
        return totalIssues;
    }

    public void setTotalIssues(int totalIssues) {
        this.totalIssues = totalIssues;
    }

    public Map<IssueStatus, Integer> getIssuesByStatus() {
        return issuesByStatus;
    }

    public void setIssuesByStatus(Map<IssueStatus, Integer> issuesByStatus) {
        this.issuesByStatus = issuesByStatus;
    }

    public Map<IssueStatus, Double> getPercentageByStatus() {
        return percentageByStatus;
    }

    public void setPercentageByStatus(Map<IssueStatus, Double> percentageByStatus) {
        this.percentageByStatus = percentageByStatus;
    }

    public double getAverageResolutionTime() {
        return averageResolutionTime;
    }

    public void setAverageResolutionTime(double averageResolutionTime) {
        this.averageResolutionTime = averageResolutionTime;
    }
}
