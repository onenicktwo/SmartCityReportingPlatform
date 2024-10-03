package org.citywatcher.service;

import org.citywatcher.model.Issue;
import org.citywatcher.model.User;
import org.citywatcher.repository.IssueRepository;
import org.citywatcher.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    @Autowired
    public IssueServiceImpl(IssueRepository issueRepository, UserRepository userRepository) {
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Issue createIssue(Issue issue) {
        User reporter = userRepository.findById(issue.getReporter().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid reporter ID"));
        issue.setReporter(reporter);

        if (issue.getAssignedOfficial() != null) {
            User assignedOfficial = userRepository.findById(issue.getAssignedOfficial().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid assigned official ID"));
            issue.setAssignedOfficial(assignedOfficial);
        }

        issue.setReportedDate(new Date());
        issue.setLastUpdatedDate(new Date());
        return issueRepository.save(issue);
    }

    @Override
    public Issue getIssueById(Long id) {
        return issueRepository.findById(id).orElse(null);
    }

    @Override
    public List<Issue> getAllIssues() {
        return issueRepository.findAll();
    }

    @Override
    public Issue updateIssue(Long id, Issue issueDetails) {
        Optional<Issue> optionalIssue = issueRepository.findById(id);
        if (optionalIssue.isPresent()) {
            Issue existingIssue = optionalIssue.get();
            existingIssue.setTitle(issueDetails.getTitle());
            existingIssue.setDescription(issueDetails.getDescription());
            existingIssue.setCategory(issueDetails.getCategory());
            existingIssue.setStatus(issueDetails.getStatus());
            existingIssue.setLatitude(issueDetails.getLatitude());
            existingIssue.setLongitude(issueDetails.getLongitude());
            existingIssue.setImagePath(issueDetails.getImagePath());

            if (issueDetails.getAssignedOfficial() != null) {
                User assignedOfficial = userRepository.findById(issueDetails.getAssignedOfficial().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid assigned official ID"));
                existingIssue.setAssignedOfficial(assignedOfficial);
            } else {
                existingIssue.setAssignedOfficial(null);
            }

            existingIssue.setLastUpdatedDate(new Date());
            return issueRepository.save(existingIssue);
        }
        return null;
    }

    @Override
    public boolean deleteIssue(Long id) {
        if (issueRepository.existsById(id)) {
            issueRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
