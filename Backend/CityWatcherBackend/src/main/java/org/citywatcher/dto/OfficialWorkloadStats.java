package org.citywatcher.dto;

import java.util.List;

public class OfficialWorkloadStats {
    private Long officialId;
    private String officialUsername;
    private int activeIssues;
    private int resolvedIssues;
    private double averageResolutionTime;
    private List<String> mostCommonAddresses;

    public Long getOfficialId() {
        return officialId;
    }

    public void setOfficialId(Long officialId) {
        this.officialId = officialId;
    }

    public String getOfficialUsername() {
        return officialUsername;
    }

    public void setOfficialUsername(String officialUsername) {
        this.officialUsername = officialUsername;
    }

    public int getActiveIssues() {
        return activeIssues;
    }

    public void setActiveIssues(int activeIssues) {
        this.activeIssues = activeIssues;
    }

    public int getResolvedIssues() {
        return resolvedIssues;
    }

    public void setResolvedIssues(int resolvedIssues) {
        this.resolvedIssues = resolvedIssues;
    }

    public double getAverageResolutionTime() {
        return averageResolutionTime;
    }

    public void setAverageResolutionTime(double averageResolutionTime) {
        this.averageResolutionTime = averageResolutionTime;
    }

    public List<String> getMostCommonAddresses() {
        return mostCommonAddresses;
    }

    public void setMostCommonAddresses(List<String> mostCommonAddresses) {
        this.mostCommonAddresses = mostCommonAddresses;
    }
}
