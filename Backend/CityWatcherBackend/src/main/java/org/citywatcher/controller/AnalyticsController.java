package org.citywatcher.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.citywatcher.dto.*;
import org.citywatcher.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/citywatcher/analytics")
@Tag(name = "Analytics", description = "Analytics APIs for administrative insights")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @Operation(summary = "Get issue statistics by status")
    @GetMapping("/status-stats")
    public ResponseEntity<StatusStats> getStatusStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(analyticsService.getStatusStats(startDate, endDate));
    }

    @Operation(summary = "Get official workload statistics")
    @GetMapping("/official-workload")
    public ResponseEntity<List<OfficialWorkloadStats>> getOfficialWorkloadStats() {
        return ResponseEntity.ok(analyticsService.getOfficialWorkloadStats());
    }

    @Operation(summary = "Get geographical issue distribution")
    @GetMapping("/location-stats")
    public ResponseEntity<List<LocationStats>> getLocationStats(
            @RequestParam(required = false) Double radius,
            @RequestParam(required = false) Double centerLat,
            @RequestParam(required = false) Double centerLng) {
        return ResponseEntity.ok(analyticsService.getLocationStats(radius, centerLat, centerLng));
    }

    @Operation(summary = "Get volunteer participation statistics")
    @GetMapping("/volunteer-stats")
    public ResponseEntity<List<VolunteerStats>> getVolunteerStats() {
        return ResponseEntity.ok(analyticsService.getVolunteerStats());
    }

    @Operation(summary = "Get response time statistics")
    @GetMapping("/response-time-stats")
    public ResponseEntity<ResponseTimeStats> getResponseTimeStats() {
        return ResponseEntity.ok(analyticsService.getResponseTimeStats());
    }
}
