package org.citywatcher.service;

import org.citywatcher.model.Issue;
import org.citywatcher.model.IssueStatus;
import org.citywatcher.model.User;
import org.citywatcher.model.UserRole;
import org.citywatcher.repository.IssueRepository;
import org.citywatcher.repository.UserRepository;
import org.citywatcher.websocket.IssueWebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.awt.*;
import java.util.Date;
import java.util.List;

@Service
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final IssueWebSocketServer issueWebSocketServer;
    private static final Logger logger = LoggerFactory.getLogger(IssueServiceImpl.class);

    @Autowired
    public IssueServiceImpl(IssueRepository issueRepository, UserRepository userRepository, IssueWebSocketServer webSocketController) {
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.issueWebSocketServer = webSocketController;
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

        try {
            if (savedIssue.getAssignedOfficial() != null) {
                issueWebSocketServer.sendAssignmentNotification(savedIssue);
            }
        } catch (Exception e) {
            logger.error("Failed to send WebSocket notification for issue create: " + e.getMessage(), e);
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
            existingIssue.setAddress((issueDetails.getAddress()));
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

            try {
                if (statusChanged && isRelevantStatus) {
                    issueWebSocketServer.sendIssueStatusUpdate(updatedIssue);
                }
                if (newAssignedOfficial != null) {
                    if (previousAssignedOfficial == null || previousAssignedOfficial.getUsername().equals(newAssignedOfficial.getUsername())) {
                        issueWebSocketServer.sendAssignmentNotification(updatedIssue);
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to send WebSocket notification for issue update: " + e.getMessage(), e);
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
    public Issue addVolunteer(Long userId, Long issueId, Long volunteerId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        if (requester.getRole() != UserRole.ADMIN && requester.getRole() != UserRole.CITY_OFFICIAL) {
            throw new IllegalArgumentException("Only administrators and city officials can assign volunteers");
        }

        Issue existingIssue = (issueRepository.findById(issueId).isPresent()) ? issueRepository.findById(issueId).get() : null;
        if (existingIssue == null) {
            throw new IllegalArgumentException("Issue not found");
        }

        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid volunteer ID"));

        if (volunteer.getRole() != UserRole.VOLUNTEER) {
            throw new IllegalArgumentException("User is not a volunteer");
        }

        // Check if volunteer is already assigned
        if (existingIssue.getVolunteers().stream()
                .anyMatch(v -> v.getId().equals(volunteerId))) {
            throw new IllegalArgumentException("Volunteer already assigned to this issue");
        }

        existingIssue.getVolunteers().add(volunteer);
        existingIssue.setLastUpdatedDate(new Date());
        Issue updatedIssue = issueRepository.save(existingIssue);

        return updatedIssue;
    }

    @Override
    public Issue removeVolunteer(Long userId, Long issueId, Long volunteerId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        if (requester.getRole() != UserRole.ADMIN && requester.getRole() != UserRole.CITY_OFFICIAL) {
            throw new IllegalArgumentException("Only administrators and city officials can assign volunteers");
        }

        Issue existingIssue = (issueRepository.findById(issueId).isPresent()) ? issueRepository.findById(issueId).get() : null;
        if (existingIssue == null) {
            throw new IllegalArgumentException("Issue not found");
        }

        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid volunteer ID"));

        boolean removed = existingIssue.getVolunteers().removeIf(v -> v.getId().equals(volunteerId));
        if (!removed) {
            throw new IllegalArgumentException("Volunteer not assigned to this issue");
        }

        existingIssue.setLastUpdatedDate(new Date());
        Issue updatedIssue = issueRepository.save(existingIssue);

        return updatedIssue;
    }

    @Override
    public List<Issue> getIssuesByVolunteer(Long volunteerId) {
        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid volunteer ID"));

        if (volunteer.getRole() != UserRole.VOLUNTEER) {
            throw new IllegalArgumentException("User is not a volunteer");
        }

        return issueRepository.findByVolunteersContaining(volunteer);
    }

    @Override
    public List<User> getVolunteersForIssue(Long issueId) {
        Issue issue = (issueRepository.findById(issueId).isPresent()) ? issueRepository.findById(issueId).get() : null;
        if (issue == null) {
            throw new IllegalArgumentException("Issue not found");
        }

        return issue.getVolunteers();
    }

    @Override
    public List<Issue> searchIssues(String category, IssueStatus status, String title, String address, Double latitude, Double longitude, Double radius, int page, int size) {
        Specification<Issue> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (category != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("category"), category));
            }
            if (status != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), status));
            }
            if (title != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            if (address != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), "%" + address.toLowerCase() + "%"));
            }
            if (latitude != null && longitude != null && radius != null) {
                Expression<Double> distance = criteriaBuilder.function("ST_Distance_Sphere", Double.class,
                        criteriaBuilder.function("ST_MakePoint", Point.class, root.get("longitude"), root.get("latitude")),
                        criteriaBuilder.function("ST_MakePoint", Point.class, criteriaBuilder.literal(longitude), criteriaBuilder.literal(latitude)));
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.le(distance, radius));
            }

            return predicate;
        };

        return issueRepository.findAll(spec, PageRequest.of(page, size)).getContent();
    }
}
