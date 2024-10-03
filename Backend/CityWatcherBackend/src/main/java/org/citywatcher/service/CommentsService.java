package org.citywatcher.service;

import org.citywatcher.model.Comment;

import java.util.List;

public interface CommentsService {
    Comment createComment(Comment comment);
    Comment getCommentById(Long id);
    List<Comment> getAllComments();
    Comment updateComment(Long id, Comment comment);
    boolean deleteComment(Long id);
}