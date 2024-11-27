package org.citywatcher.service;

import org.citywatcher.model.Comment;

import java.util.List;

public interface CommentsService {
    Comment createComment(Long issueId, Long userId, Comment comment);
    Comment getCommentById(Long id);
    List<Comment> getCommentsByIssue(Long issueId);
    Comment updateComment(Long id, Long issueId, Long userId, Comment commentDetails);
    boolean deleteComment(Long id, Long userId, Long issueId);
}