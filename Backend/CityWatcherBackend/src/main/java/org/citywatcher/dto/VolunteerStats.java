package org.citywatcher.dto;

import java.util.List;

public class VolunteerStats {
    private Long volunteerId;
    private String volunteerUsername;
    private int totalParticipations;
    private int activeParticipations;
    private List<String> mostActiveAreas;

    public Long getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Long volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getVolunteerUsername() {
        return volunteerUsername;
    }

    public void setVolunteerUsername(String volunteerUsername) {
        this.volunteerUsername = volunteerUsername;
    }

    public int getTotalParticipations() {
        return totalParticipations;
    }

    public void setTotalParticipations(int totalParticipations) {
        this.totalParticipations = totalParticipations;
    }

    public int getActiveParticipations() {
        return activeParticipations;
    }

    public void setActiveParticipations(int activeParticipations) {
        this.activeParticipations = activeParticipations;
    }

    public List<String> getMostActiveAreas() {
        return mostActiveAreas;
    }

    public void setMostActiveAreas(List<String> mostActiveAreas) {
        this.mostActiveAreas = mostActiveAreas;
    }
}
