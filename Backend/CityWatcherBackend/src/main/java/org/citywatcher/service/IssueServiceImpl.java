package org.citywatcher.service;

import org.citywatcher.controller.WebSocketController;
import org.citywatcher.model.Issue;
import org.citywatcher.model.IssueStatus;
import org.citywatcher.model.User;
import org.citywatcher.repository.IssueRepository;
import org.citywatcher.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Expression;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final WebSocketController webSocketController;

    @Autowired
    public IssueServiceImpl(IssueRepository issueRepository, UserRepository userRepository, WebSocketController webSocketController) {
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.webSocketController = webSocketController;
    }

    @Override
    public Issue createIssue(Long userId, Issue issue) {
        User reporter = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reporter ID"));
        issue.setReporter(reporter);

        if (issue.getAssignedOfficial() != null) {
            User assignedOfficial = userRepository.findById(issue.getAssignedOfficial().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid assigned official ID"));
            issue.setAssignedOfficial(assignedOfficial);
        }

        issue.setReportedDate(new Date());
        issue.setLastUpdatedDate(new Date());
        Issue savedIssue = issueRepository.save(issue);

        // If the issue is immediately assigned, send assignment notification
        if (savedIssue.getAssignedOfficial() != null) {
            webSocketController.sendAssignmentNotification(savedIssue);
        }

        return savedIssue;
    }

    @Override
    public Issue getIssueById(Long userId, Long issueId) {
        User reporter = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid User ID"));
        return issueRepository.findByIdAndReporter(issueId, reporter)
                .orElseThrow(() -> new IllegalArgumentException("Issue does not belong to the specified user."));
    }

    @Override
    public List<Issue> getIssuesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid User ID"));
        return issueRepository.findByReporter(user);
    }

    @Override
    public Issue updateIssue(Long userId, Long issueId, Issue issueDetails) {
        Issue existingIssue = getIssueById(userId, issueId);
        if (existingIssue != null) {
            User reporter = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid reporter ID"));

            boolean statusChanged = !existingIssue.getStatus().equals(issueDetails.getStatus());
            boolean isRelevantStatus = issueDetails.getStatus() == IssueStatus.UNDER_REVIEW
                    || issueDetails.getStatus() == IssueStatus.COMPLETED;
            User previousAssignedOfficial = existingIssue.getAssignedOfficial();
            User newAssignedOfficial = issueDetails.getAssignedOfficial();

            existingIssue.setTitle(issueDetails.getTitle());
            existingIssue.setDescription(issueDetails.getDescription());
            existingIssue.setCategory(issueDetails.getCategory());
            existingIssue.setStatus(issueDetails.getStatus());
            existingIssue.setLatitude(issueDetails.getLatitude());
            existingIssue.setLongitude(issueDetails.getLongitude());
            existingIssue.setImagePath(issueDetails.getImagePath());
            existingIssue.setReporter(reporter);

            if (issueDetails.getAssignedOfficial() != null) {
                User assignedOfficial = userRepository.findById(issueDetails.getAssignedOfficial().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid assigned official ID"));
                existingIssue.setAssignedOfficial(assignedOfficial);
            } else {
                existingIssue.setAssignedOfficial(null);
            }

            existingIssue.setLastUpdatedDate(new Date());
            Issue updatedIssue = issueRepository.save(existingIssue);

            if (statusChanged && isRelevantStatus) {
                webSocketController.sendIssueStatusUpdate(updatedIssue);
            }
            if (newAssignedOfficial != null && !newAssignedOfficial.equals(previousAssignedOfficial)) {
                webSocketController.sendAssignmentNotification(updatedIssue);
            }

            return updatedIssue;
        }
        return null;
    }

    @Override
    public boolean deleteIssue(Long userId, Long issueId) {
        Issue issue = getIssueById(userId, issueId);
        if (issue != null) {
            issueRepository.delete(issue);
            return true;
        }
        return false;
    }

    @Override
    public List<Issue> searchIssues(String category, IssueStatus status, String title,
                                    Double latitude, Double longitude, Double radius,
                                    int page, int size) {
        Specification<Issue> spec = Specification.where(null);

        if (category != null && !category.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (title != null && !title.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }

        if (latitude != null && longitude != null && radius != null) {
            spec = spec.and((root, query, cb) -> {
                Expression<Double> distance = cb.function("ST_Distance_Sphere", Double.class,
                        cb.function("POINT", Point.class, root.get("longitude"), root.get("latitude")),
                        cb.function("POINT", Point.class, cb.literal(longitude), cb.literal(latitude)));
                return cb.lessThanOrEqualTo(distance, radius);
            });
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        return issueRepository.findAll(spec, pageRequest).getContent();
    }
}
