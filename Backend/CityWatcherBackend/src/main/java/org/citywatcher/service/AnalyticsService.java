package org.citywatcher.service;

import org.citywatcher.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface AnalyticsService {
    StatusStats getStatusStats(LocalDate startDate, LocalDate endDate);
    List<OfficialWorkloadStats> getOfficialWorkloadStats();
    List<LocationStats> getLocationStats(Double radius, Double centerLat, Double centerLng);
    List<VolunteerStats> getVolunteerStats();
    ResponseTimeStats getResponseTimeStats();
}
