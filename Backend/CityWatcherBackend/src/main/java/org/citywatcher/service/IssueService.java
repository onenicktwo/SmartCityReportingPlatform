package org.citywatcher.service;

import org.citywatcher.model.Issue;
import org.citywatcher.model.IssueStatus;
import org.citywatcher.model.User;

import java.util.List;

public interface IssueService {
    Issue createIssue(Long userId, Issue issue);
    Issue getIssueById(Long userId, Long issueId);
    List<Issue> getIssuesByUser(Long userId);
    Issue updateIssue(Long userId, Long issueId, Issue issue);
    boolean deleteIssue(Long userId, Long issueId);
    Issue addVolunteer(Long userId, Long issueId, Long volunteerId);
    Issue removeVolunteer(Long userId, Long issueId, Long volunteerId);
    List<Issue> getIssuesByVolunteer(Long volunteerId);
    List<User> getVolunteersForIssue(Long issueId) ;
    List<Issue> searchIssues(String category, IssueStatus status, String title,
                             String address, Double latitude, Double longitude,
                             Double radius, int page, int size);
}