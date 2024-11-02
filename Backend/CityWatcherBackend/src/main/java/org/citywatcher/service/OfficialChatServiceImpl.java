package org.citywatcher.service;

import org.citywatcher.model.Comment;
import org.citywatcher.model.Issue;
import org.citywatcher.model.User;
import org.citywatcher.model.UserRole;
import org.citywatcher.repository.CommentsRepository;
import org.citywatcher.repository.IssueRepository;
import org.citywatcher.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public class OfficialChatServiceImpl {
    private final CommentsRepository commentRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    @Autowired
    public OfficialChatServiceImpl(
            CommentsRepository commentRepository,
            IssueRepository issueRepository,
            UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
    }

    public Comment sendMessage(Long userId, Long issueId, Comment message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        validateOfficialAccess(user);

        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found"));

        message.setUser(user);
        message.setIssue(issue);
        message.setInternalNote(true);

        return commentRepository.save(message);
    }

    public List<Comment> getOfficialMessages(Long userId, Long issueId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        validateOfficialAccess(user);

        return commentRepository.findByIssueIdAndIsInternalNoteTrueOrderByTimestampDesc(issueId);
    }

    public void deleteMessage(Long userId, Long messageId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        validateOfficialAccess(user);

        Comment message = commentRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));

        if (!message.isInternalNote()) {
            throw new IllegalStateException("Cannot delete non-internal messages through official chat");
        }

        if (user.getRole() != UserRole.ADMIN &&
                !message.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You can only delete your own messages");
        }

        commentRepository.delete(message);
    }

    private void validateOfficialAccess(User user) {
        if (user.getRole() != UserRole.CITY_OFFICIAL &&
                user.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("Only city officials and administrators can access official chat");
        }
    }
}
