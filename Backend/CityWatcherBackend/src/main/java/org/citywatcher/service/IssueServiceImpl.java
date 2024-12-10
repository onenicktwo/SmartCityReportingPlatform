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
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class IssueServiceImpl implements IssueService {
    private final FileStorageService fileStorageService;

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final IssueWebSocketServer issueWebSocketServer;
    private static final Logger logger = LoggerFactory.getLogger(IssueServiceImpl.class);

    @Autowired
    public IssueServiceImpl(IssueRepository issueRepository, UserRepository userRepository, IssueWebSocketServer webSocketController, FileStorageService fileStorageService) {
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.issueWebSocketServer = webSocketController;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public Issue createIssue(Long userId, Issue issue) {
        User reporter = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reporter ID"));

        if (issue.getTitle() == null || issue.getTitle().trim().isEmpty()) {
            issue.setTitle("Untitled");
        }
        if (issue.getDescription() == null || issue.getDescription().trim().isEmpty()) {
            issue.setDescription("");
        }
        if (issue.getLatitude() == null || issue.getLongitude() == null) {
            throw new IllegalArgumentException("Location coordinates are required");
        }
        if (issue.getAddress() == null || issue.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Address is required");
        }

        issue.setStatus(IssueStatus.REPORTED);
        issue.setCategory(issue.getCategory() != null ? issue.getCategory() : "Other");
        issue.setImagePath("");
        issue.setVolunteers(new ArrayList<>());
        issue.setComments(new ArrayList<>());
        issue.setReporter(reporter);

        Date currentTime = new Date();
        issue.setReportedDate(currentTime);
        issue.setLastUpdatedDate(currentTime);

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
    public void addImageToIssue(Long userId, Long issueId, byte[] imageBytes, String fileName) {
        Issue existingIssue = getIssueById(userId, issueId);
        if (existingIssue == null) {
            throw new IllegalArgumentException("Issue not found or does not belong to the user");
        }

        if (imageBytes != null && imageBytes.length > 0) {
            String imagePath = fileStorageService.saveIssueImage(issueId, imageBytes, fileName);

            existingIssue.setImagePath(imagePath);
            existingIssue.setLastUpdatedDate(new Date());
            issueRepository.save(existingIssue);
        } else {
            throw new IllegalArgumentException("Invalid image data or filename");
        }
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
            boolean statusChanged = issueDetails.getStatus() != null &&
                    !existingIssue.getStatus().equals(issueDetails.getStatus());
            boolean isRelevantStatus = issueDetails.getStatus() != null &&
                    (issueDetails.getStatus() == IssueStatus.UNDER_REVIEW
                            || issueDetails.getStatus() == IssueStatus.COMPLETED);
            User previousAssignedOfficial = existingIssue.getAssignedOfficial();

            if (issueDetails.getTitle() != null) existingIssue.setTitle(issueDetails.getTitle());
            if (issueDetails.getDescription() != null) existingIssue.setDescription(issueDetails.getDescription());
            if (issueDetails.getCategory() != null) existingIssue.setCategory(issueDetails.getCategory());
            if (issueDetails.getStatus() != null) existingIssue.setStatus(issueDetails.getStatus());
            if (issueDetails.getLatitude() != null) existingIssue.setLatitude(issueDetails.getLatitude());
            if (issueDetails.getLongitude() != null) existingIssue.setLongitude(issueDetails.getLongitude());
            if (issueDetails.getAddress() != null) existingIssue.setAddress(issueDetails.getAddress());

            if (issueDetails.getAssignedOfficial() != null) {
                User assignedOfficial = userRepository.findById(issueDetails.getAssignedOfficial().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid assigned official ID"));
                existingIssue.setAssignedOfficial(assignedOfficial);
            }

            existingIssue.setLastUpdatedDate(new Date());
            Issue updatedIssue = issueRepository.save(existingIssue);

            try {
                if (statusChanged && isRelevantStatus) {
                    issueWebSocketServer.sendIssueStatusUpdate(updatedIssue);
                }

                User newAssignedOfficial = updatedIssue.getAssignedOfficial();
                if (newAssignedOfficial != null) {
                    if (previousAssignedOfficial == null ||
                            !previousAssignedOfficial.getUsername().equals(newAssignedOfficial.getUsername())) {
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
