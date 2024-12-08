package org.citywatcher.dto;

import java.util.List;
import java.util.Map;

public class ResponseTimeStats {
    private double averageTimeToAssignment;
    private double averageTimeToResolution;
    private Map<String, Double> responseTimeByAddress; // average response time by area
    private List<String> slowestResponseAreas;
    private List<String> fastestResponseAreas;

    public double getAverageTimeToAssignment() {
        return averageTimeToAssignment;
    }

    public void setAverageTimeToAssignment(double averageTimeToAssignment) {
        this.averageTimeToAssignment = averageTimeToAssignment;
    }

    public double getAverageTimeToResolution() {
        return averageTimeToResolution;
    }

    public void setAverageTimeToResolution(double averageTimeToResolution) {
        this.averageTimeToResolution = averageTimeToResolution;
    }

    public Map<String, Double> getResponseTimeByAddress() {
        return responseTimeByAddress;
    }

    public void setResponseTimeByAddress(Map<String, Double> responseTimeByAddress) {
        this.responseTimeByAddress = responseTimeByAddress;
    }

    public List<String> getSlowestResponseAreas() {
        return slowestResponseAreas;
    }

    public void setSlowestResponseAreas(List<String> slowestResponseAreas) {
        this.slowestResponseAreas = slowestResponseAreas;
    }

    public List<String> getFastestResponseAreas() {
        return fastestResponseAreas;
    }

    public void setFastestResponseAreas(List<String> fastestResponseAreas) {
        this.fastestResponseAreas = fastestResponseAreas;
    }
}
