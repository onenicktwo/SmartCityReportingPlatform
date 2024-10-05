package org.citywatcher.controller;

import org.citywatcher.model.Comment;
import org.citywatcher.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/citywatcher/users/{userId}/issues/{issueId}/comments")
public class CommentController {

    private final CommentsService commentService;

    @Autowired
    public CommentController(CommentsService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(
            @PathVariable Long userId,
            @PathVariable Long issueId,
            @RequestBody Comment comment) {
        try {
            Comment createdComment = commentService.createComment(userId, issueId, comment);
            return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<Comment> getCommentById(
            @PathVariable Long userId,
            @PathVariable Long issueId,
            @PathVariable Long commentId) {
        Comment comment = commentService.getCommentById(commentId);
        if (comment != null && comment.getUser().getId().equals(userId) && comment.getIssue().getId().equals(issueId)) {
            return new ResponseEntity<>(comment, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getCommentsByIssue(
            @PathVariable Long userId,
            @PathVariable Long issueId) {
        List<Comment> comments = commentService.getCommentsByIssue(issueId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long userId,
            @PathVariable Long issueId,
            @PathVariable Long commentId,
            @RequestBody Comment comment) {
        try {
            Comment updatedComment = commentService.updateComment(commentId, userId, issueId, comment);
            if (updatedComment != null) {
                return new ResponseEntity<>(updatedComment, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long userId,
            @PathVariable Long issueId,
            @PathVariable Long commentId) {
        boolean deleted = commentService.deleteComment(commentId, userId, issueId);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}