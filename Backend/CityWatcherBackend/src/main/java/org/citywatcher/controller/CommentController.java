package org.citywatcher.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.citywatcher.model.Comment;
import org.citywatcher.model.Report;
import org.citywatcher.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/citywatcher/users/{userId}/issues/{issueId}/comments")
@Tag(name = "Comments Management", description = "APIs for managing comments on issues")
public class CommentController {

    private final CommentsService commentService;

    @Autowired
    public CommentController(CommentsService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "Create a new comment", description = "After creating a new comment, a notification will be sent for users logged in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
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

    @Operation(summary = "Get a comment by it's ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content)
    })
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

    @Operation(summary = "Get all comments on an issue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of comments retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "Issue not found", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Comment>> getCommentsByIssue(
            @PathVariable Long userId,
            @PathVariable Long issueId) {
        List<Comment> comments = commentService.getCommentsByIssue(issueId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @Operation(summary = "Edit/Update an existing comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "Comment, issue, or user not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
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

    @Operation(summary = "Delete a comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comment deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Comment, issue, or user not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    })
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

    @Operation(summary = "Report a comment", description = "Allows citizens to report inappropriate comments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment reported successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized to report comments", content = @Content)
    })
    @PostMapping("/{commentId}/report")
    public ResponseEntity<Report> reportComment(
            @PathVariable Long userId,
            @PathVariable Long issueId,
            @PathVariable Long commentId,
            @RequestBody Report report) {

        try {
            Report comment = commentService.reportComment(commentId, userId, report);
            return new ResponseEntity<>(comment, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}