package org.citywatcher.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.citywatcher.dto.ImageUploadRequest;
import org.citywatcher.model.Issue;
import org.citywatcher.model.IssueStatus;
import org.citywatcher.model.User;
import org.citywatcher.service.FileStorageService;
import org.citywatcher.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/citywatcher/users/{userId}/issues")
@Tag(name = "Issue Management", description = "APIs for official issue management")
public class IssueController {

    private final IssueService issueService;
    private final FileStorageService fileStorageService;

    @Autowired
    public IssueController(IssueService issueService, FileStorageService fileStorageService) {
        this.issueService = issueService;
        this.fileStorageService = fileStorageService;
    }

    @Operation(summary = "Create a new issue", description = "Create a new issue without an image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Issue created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Issue.class))),
            @ApiResponse(responseCode = "400", description = "Invalid issue data", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Issue> createIssue(@PathVariable Long userId, @RequestBody Issue issue) {
        Issue createdIssue = issueService.createIssue(userId, issue);
        return new ResponseEntity<>(createdIssue, HttpStatus.CREATED);
    }

    @Operation(summary = "Upload an image for an issue", description = "Upload an image for an existing issue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "404", description = "Issue not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid image file", content = @Content)
    })
    @PostMapping("/{issueId}/image")
    public ResponseEntity<Void> uploadIssueImage(@PathVariable Long userId,
                                                 @PathVariable Long issueId,
                                                 @RequestBody ImageUploadRequest imageUploadRequest) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(imageUploadRequest.getImageBase64());
            issueService.addImageToIssue(userId, issueId, imageBytes, imageUploadRequest.getFileName());
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get issue by it's ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issue found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Issue.class))),
            @ApiResponse(responseCode = "404", description = "Issue not found", content = @Content)
    })
    @GetMapping("/{issueId}")
    public ResponseEntity<Issue> getIssueById(@PathVariable Long userId, @PathVariable Long issueId) {
        Issue issue = issueService.getIssueById(userId, issueId);
        if (issue != null) {
            return new ResponseEntity<>(issue, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get all issues reported by a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of issues found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Issue.class)))
    })
    @GetMapping
    public ResponseEntity<List<Issue>> getIssuesByUser(@PathVariable Long userId) {
        List<Issue> issues = issueService.getIssuesByUser(userId);
        return new ResponseEntity<>(issues, HttpStatus.OK);
    }

    @Operation(summary = "Edit/Update an existing issue", description = "After editing an issue, if it's status is changed a notification will be sent to each person connected to it. If the official is changed out, the new official will get a notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issue updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Issue.class))),
            @ApiResponse(responseCode = "404", description = "Issue not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid issue data", content = @Content)
    })
    @PutMapping("/{issueId}")
    public ResponseEntity<Issue> updateIssue(@PathVariable Long userId,
                                             @PathVariable Long issueId,
                                             @RequestBody Issue issueDetails) {
        Issue updatedIssue = issueService.updateIssue(userId, issueId, issueDetails);
        if (updatedIssue != null) {
            return new ResponseEntity<>(updatedIssue, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Delete an issue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Issue deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Issue not found", content = @Content)
    })
    @DeleteMapping("/{issueId}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long userId, @PathVariable Long issueId) {
        boolean deleted = issueService.deleteIssue(userId, issueId);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Official/Admin assign a volunteer to an issue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Volunteer assigned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Issue.class))),
            @ApiResponse(responseCode = "404", description = "Issue or volunteer not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    })
    @PostMapping("/{issueId}/volunteers/{volunteerId}")
    public ResponseEntity<Issue> addVolunteer(
            @PathVariable Long userId,
            @PathVariable Long issueId,
            @PathVariable Long volunteerId) {
        try {
            Issue updatedIssue = issueService.addVolunteer(userId, issueId, volunteerId);
            return new ResponseEntity<>(updatedIssue, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Official/Admin remove a volunteer from an issue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Volunteer removed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Issue.class))),
            @ApiResponse(responseCode = "404", description = "Issue or volunteer not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    })
    @DeleteMapping("/{issueId}/volunteers/{volunteerId}")
    public ResponseEntity<Issue> removeVolunteer(
            @PathVariable Long userId,
            @PathVariable Long issueId,
            @PathVariable Long volunteerId) {
        try {
            Issue updatedIssue = issueService.removeVolunteer(userId, issueId, volunteerId);
            return new ResponseEntity<>(updatedIssue, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get all issues assigned to a volunteer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issues retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "404", description = "Volunteer not found", content = @Content)
    })
    @GetMapping("/volunteer/{volunteerId}")
    public ResponseEntity<List<Issue>> getIssuesByVolunteer(
            @PathVariable Long volunteerId) {
        try {
            List<Issue> issues = issueService.getIssuesByVolunteer(volunteerId);
            return new ResponseEntity<>(issues, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get all volunteers assigned to an issue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Volunteers retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "404", description = "Issue not found", content = @Content)
    })
    @GetMapping("/{issueId}/volunteers")
    public ResponseEntity<List<User>> getVolunteersForIssue(
            @PathVariable Long issueId) {
        try {
            List<User> volunteers = issueService.getVolunteersForIssue(issueId);
            return new ResponseEntity<>(volunteers, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Search issues by various criteria", description = "Categories include category, issue status, title, address, latitude, longitude, and radius of search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issues retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search criteria", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<List<Issue>> searchIssues(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) IssueStatus status,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radius,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<Issue> issues = issueService.searchIssues(category, status, title, address, latitude, longitude, radius, page, size);
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/{issueId}/image")
    public ResponseEntity<Resource> getIssueImage(@PathVariable Long userId, @PathVariable Long issueId) {
        try {
            ResponseEntity<Issue> issue = getIssueById(userId, issueId);
            if (issue.getBody() != null) {
                Resource file = fileStorageService.loadFileAsResource(issue.getBody().getImagePath());
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(file);
            } else {
                return ResponseEntity.status(issue.getStatusCode()).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}