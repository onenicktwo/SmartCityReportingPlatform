package org.citywatcher.dto;

import org.citywatcher.model.IssueStatus;

import java.util.Map;

public class LocationStats {
    private String address;
    private int issueCount;
    private Map<IssueStatus, Integer> statusDistribution;
    private double latitude;
    private double longitude;
    private double averageResolutionTime;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getIssueCount() {
        return issueCount;
    }

    public void setIssueCount(int issueCount) {
        this.issueCount = issueCount;
    }

    public Map<IssueStatus, Integer> getStatusDistribution() {
        return statusDistribution;
    }

    public void setStatusDistribution(Map<IssueStatus, Integer> statusDistribution) {
        this.statusDistribution = statusDistribution;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAverageResolutionTime() {
        return averageResolutionTime;
    }

    public void setAverageResolutionTime(double averageResolutionTime) {
        this.averageResolutionTime = averageResolutionTime;
    }
}
