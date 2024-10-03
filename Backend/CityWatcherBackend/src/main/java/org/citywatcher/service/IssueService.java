package org.citywatcher.service;

import org.citywatcher.model.Issue;
import java.util.List;

public interface IssueService {
    Issue createIssue(Issue issue);
    Issue getIssueById(Long id);
    List<Issue> getAllIssues();
    Issue updateIssue(Long id, Issue issue);
    boolean deleteIssue(Long id);
}