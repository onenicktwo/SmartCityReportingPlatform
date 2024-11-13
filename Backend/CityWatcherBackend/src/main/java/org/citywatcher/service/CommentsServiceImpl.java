package org.citywatcher.service;

import org.citywatcher.model.Comment;
import org.citywatcher.model.Issue;
import org.citywatcher.model.User;
import org.citywatcher.repository.CommentsRepository;
import org.citywatcher.repository.IssueRepository;
import org.citywatcher.repository.UserRepository;
import org.citywatcher.websocket.IssueWebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final IssueWebSocketServer issueWebSocketServer;
    private static final Logger logger = LoggerFactory.getLogger(CommentsServiceImpl.class);

    @Autowired
    public CommentsServiceImpl(CommentsRepository commentRepository,
                               IssueRepository issueRepository,
                               UserRepository userRepository,
                               IssueWebSocketServer webSocketController) {
        this.commentRepository = commentRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.issueWebSocketServer = webSocketController;
    }

    @Override
    public Comment createComment(Long userId, Long issueId, Comment comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid User ID"));
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Issue ID"));

        comment.setUser(user);
        comment.setIssue(issue);
        Comment savedComment = commentRepository.save(comment);

        // Notify relevant users via WebSocket
        try {
            issueWebSocketServer.sendCommentNotification(issue, savedComment.getContent());
        } catch (Exception e) {
            logger.error("Failed to send WebSocket notification for new comment: " + e.getMessage(), e);
        }

        return savedComment;
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Override
    public List<Comment> getCommentsByIssue(Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Issue ID"));
        return commentRepository.findByIssue(issue);
    }

    @Override
    public Comment updateComment(Long commentId, Long userId, Long issueId, Comment commentDetails) {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Comment ID"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid User ID"));
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Issue ID"));

        // Check if the comment belongs to the specified user and issue
        if (!existingComment.getUser().getId().equals(userId) || !existingComment.getIssue().getId().equals(issueId)) {
            throw new IllegalArgumentException("Comment does not belong to the specified user or issue");
        }

        existingComment.setContent(commentDetails.getContent());
        existingComment.setInternalNote(commentDetails.isInternalNote());
        existingComment.setUser(user);
        existingComment.setIssue(issue);

        return commentRepository.save(existingComment);
    }

    @Override
    public boolean deleteComment(Long commentId, Long userId, Long issueId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Comment ID"));

        // Check if the comment belongs to the specified user and issue
        if (comment.getUser().getId().equals(userId) && comment.getIssue().getId().equals(issueId)) {
            commentRepository.delete(comment);
            return true;
        }
        return false;
    }
}