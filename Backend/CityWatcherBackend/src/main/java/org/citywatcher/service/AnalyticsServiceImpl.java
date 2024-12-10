package org.citywatcher.service;

import org.citywatcher.dto.*;
import org.citywatcher.model.Issue;
import org.citywatcher.model.IssueStatus;
import org.citywatcher.model.User;
import org.citywatcher.model.UserRole;
import org.citywatcher.repository.IssueRepository;
import org.citywatcher.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService{
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    @Autowired
    public AnalyticsServiceImpl(IssueRepository issueRepository, UserRepository userRepository) {
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
    }

    @Override
    public StatusStats getStatusStats(LocalDate startDate, LocalDate endDate) {
        Date start = startDate != null ? Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
        Date end = endDate != null ? Date.from(endDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()) : null;

        List<Issue> issues = start != null && end != null ?
                issueRepository.findByReportedDateBetween(start, end) :
                issueRepository.findAll();

        StatusStats stats = new StatusStats();
        stats.setTotalIssues(issues.size());

        Map<IssueStatus, Integer> statusCount = new HashMap<>();
        Map<IssueStatus, Double> percentages = new HashMap<>();
        long totalResolutionTime = 0;
        int resolvedCount = 0;

        for (Issue issue : issues) {
            statusCount.merge(issue.getStatus(), 1, Integer::sum);

            if (issue.getStatus() == IssueStatus.COMPLETED) {
                resolvedCount++;
                totalResolutionTime += (issue.getLastUpdatedDate().getTime() - issue.getReportedDate().getTime());
            }
        }

        for (Map.Entry<IssueStatus, Integer> entry : statusCount.entrySet()) {
            percentages.put(entry.getKey(),
                    (double) entry.getValue() * 100 / issues.size());
        }

        stats.setIssuesByStatus(statusCount);
        stats.setPercentageByStatus(percentages);

        if (resolvedCount > 0) {
            stats.setAverageResolutionTime((double) totalResolutionTime / (resolvedCount * 3600000)); // Convert to hours
        }

        return stats;
    }

    @Override
    public List<OfficialWorkloadStats> getOfficialWorkloadStats() {
        List<User> officials = userRepository.findByRole(UserRole.CITY_OFFICIAL);
        List<OfficialWorkloadStats> workloadStats = new ArrayList<>();

        for (User official : officials) {
            OfficialWorkloadStats stats = new OfficialWorkloadStats();
            stats.setOfficialId(official.getId());
            stats.setOfficialUsername(official.getUsername());

            List<Issue> assignedIssues = official.getAssignedIssues();

            int activeCount = 0;
            int resolvedCount = 0;
            long totalResolutionTime = 0;
            Map<String, Integer> addressFrequency = new HashMap<>();

            for (Issue issue : assignedIssues) {
                if (issue.getStatus() == IssueStatus.COMPLETED) {
                    resolvedCount++;
                    totalResolutionTime += (issue.getLastUpdatedDate().getTime() - issue.getReportedDate().getTime());
                } else {
                    activeCount++;
                }

                addressFrequency.merge(issue.getAddress(), 1, Integer::sum);
            }

            stats.setActiveIssues(activeCount);
            stats.setResolvedIssues(resolvedCount);

            if (resolvedCount > 0) {
                stats.setAverageResolutionTime((double) totalResolutionTime / (resolvedCount * 3600000));
            }

            stats.setMostCommonAddresses(
                    addressFrequency.entrySet().stream()
                            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                            .limit(5)
                            .map(Map.Entry::getKey).collect(Collectors.toList())
            );

            workloadStats.add(stats);
        }

        return workloadStats;
    }

    @Override
    public List<LocationStats> getLocationStats(Double radius, Double centerLat, Double centerLng) {
        List<Issue> issues;
        if (radius != null && centerLat != null && centerLng != null) {
            issues = issueRepository.findByLocationWithinRadius(centerLat, centerLng, radius);
        } else {
            issues = issueRepository.findAll();
        }

        // Group issues by address
        Map<String, List<Issue>> issuesByLocation = issues.stream()
                .collect(Collectors.groupingBy(Issue::getAddress));

        List<LocationStats> locationStats = new ArrayList<>();

        for (Map.Entry<String, List<Issue>> entry : issuesByLocation.entrySet()) {
            LocationStats stats = new LocationStats();
            List<Issue> locationIssues = entry.getValue();
            Issue firstIssue = locationIssues.get(0);

            stats.setAddress(entry.getKey());
            stats.setIssueCount(locationIssues.size());
            stats.setLatitude(firstIssue.getLatitude());
            stats.setLongitude(firstIssue.getLongitude());

            Map<IssueStatus, Integer> statusDist = new HashMap<>();
            long totalResolutionTime = 0;
            int resolvedCount = 0;

            for (Issue issue : locationIssues) {
                statusDist.merge(issue.getStatus(), 1, Integer::sum);
                if (issue.getStatus() == IssueStatus.COMPLETED) {
                    resolvedCount++;
                    totalResolutionTime += (issue.getLastUpdatedDate().getTime() - issue.getReportedDate().getTime());
                }
            }

            stats.setStatusDistribution(statusDist);
            if (resolvedCount > 0) {
                stats.setAverageResolutionTime((double) totalResolutionTime / (resolvedCount * 3600000));
            }

            locationStats.add(stats);
        }

        return locationStats;
    }

    @Override
    public List<VolunteerStats> getVolunteerStats() {
        List<User> volunteers = userRepository.findByRole(UserRole.VOLUNTEER);
        List<VolunteerStats> volunteerStatsList = new ArrayList<>();

        for (User volunteer : volunteers) {
            VolunteerStats stats = new VolunteerStats();
            stats.setVolunteerId(volunteer.getId());
            stats.setVolunteerUsername(volunteer.getUsername());

            List<Issue> volunteerIssues = volunteer.getVolunteerIssues();

            stats.setTotalParticipations(volunteerIssues.size());
            stats.setActiveParticipations((int) volunteerIssues.stream()
                    .filter(issue -> issue.getStatus() != IssueStatus.COMPLETED)
                    .count());

            Map<String, Long> areaFrequency = volunteerIssues.stream()
                    .collect(Collectors.groupingBy(Issue::getAddress, Collectors.counting()));

            stats.setMostActiveAreas(areaFrequency.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(5)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList()));

            volunteerStatsList.add(stats);
        }

        return volunteerStatsList;
    }

    @Override
    public ResponseTimeStats getResponseTimeStats() {
        List<Issue> allIssues = issueRepository.findAll();
        ResponseTimeStats stats = new ResponseTimeStats();

        Map<String, List<Long>> assignmentTimesByArea = new HashMap<>();
        Map<String, List<Long>> resolutionTimesByArea = new HashMap<>();

        long totalAssignmentTime = 0;
        long totalResolutionTime = 0;
        int assignedCount = 0;
        int resolvedCount = 0;

        for (Issue issue : allIssues) {
            if (issue.getAssignedOfficial() != null) {
                long assignmentTime = issue.getLastUpdatedDate().getTime() - issue.getReportedDate().getTime();
                totalAssignmentTime += assignmentTime;
                assignedCount++;

                assignmentTimesByArea
                        .computeIfAbsent(issue.getAddress(), k -> new ArrayList<>())
                        .add(assignmentTime);

                if (issue.getStatus() == IssueStatus.COMPLETED) {
                    long resolutionTime = issue.getLastUpdatedDate().getTime() - issue.getReportedDate().getTime();
                    totalResolutionTime += resolutionTime;
                    resolvedCount++;

                    resolutionTimesByArea
                            .computeIfAbsent(issue.getAddress(), k -> new ArrayList<>())
                            .add(resolutionTime);
                }
            }
        }

        if (assignedCount > 0) {
            stats.setAverageTimeToAssignment((double) totalAssignmentTime / (assignedCount * 3600000)); // Convert to hours
        }
        if (resolvedCount > 0) {
            stats.setAverageTimeToResolution((double) totalResolutionTime / (resolvedCount * 3600000));
        }

        Map<String, Double> responseTimeByAddress = new HashMap<>();
        for (Map.Entry<String, List<Long>> entry : resolutionTimesByArea.entrySet()) {
            double averageTime = entry.getValue().stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0) / 3600000; // Convert to hours
            responseTimeByAddress.put(entry.getKey(), averageTime);
        }
        stats.setResponseTimeByAddress(responseTimeByAddress);

        List<Map.Entry<String, Double>> sortedAreas = new ArrayList<>(responseTimeByAddress.entrySet());

        stats.setSlowestResponseAreas(sortedAreas.stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()));

        stats.setFastestResponseAreas(sortedAreas.stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()));

        return stats;
    }
}
