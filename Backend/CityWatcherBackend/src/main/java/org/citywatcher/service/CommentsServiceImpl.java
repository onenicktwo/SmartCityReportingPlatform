package org.citywatcher.service;

import org.citywatcher.model.Comment;
import org.citywatcher.model.Issue;
import org.citywatcher.model.User;
import org.citywatcher.repository.CommentsRepository;
import org.citywatcher.repository.IssueRepository;
import org.citywatcher.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentsServiceImpl implements CommentsService {

    private final CommentsRepository commentRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentsServiceImpl(CommentsRepository commentRepository,
                              IssueRepository issueRepository,
                              UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Comment createComment(Comment comment) {
        Issue issue = issueRepository.findById(comment.getIssue().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Issue ID"));
        User user = userRepository.findById(comment.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid User ID"));

        comment.setIssue(issue);
        comment.setUser(user);
        return commentRepository.save(comment);
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    public Comment updateComment(Long id, Comment commentDetails) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isPresent()) {
            Comment existingComment = optionalComment.get();
            existingComment.setContent(commentDetails.getContent());
            existingComment.setInternalNote(commentDetails.isInternalNote());

            if (commentDetails.getIssue() != null && !commentDetails.getIssue().getId().equals(existingComment.getIssue().getId())) {
                Issue issue = issueRepository.findById(commentDetails.getIssue().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Issue ID"));
                existingComment.setIssue(issue);
            }

            if (commentDetails.getUser() != null && !commentDetails.getUser().getId().equals(existingComment.getUser().getId())) {
                User user = userRepository.findById(commentDetails.getUser().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid User ID"));
                existingComment.setUser(user);
            }

            return commentRepository.save(existingComment);
        }
        return null;
    }

    @Override
    public boolean deleteComment(Long id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}