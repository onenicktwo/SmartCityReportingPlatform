package org.citywatcher.controller;

import org.citywatcher.model.Issue;
import org.citywatcher.model.IssueStatus;
import org.citywatcher.model.User;
import org.citywatcher.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/citywatcher/users/{userId}/issues")
public class IssueController {

    private final IssueService issueService;

    @Autowired
    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping
    public ResponseEntity<Issue> createIssue(@PathVariable Long userId, @RequestBody Issue issue) {
        Issue createdIssue = issueService.createIssue(userId, issue);
        return new ResponseEntity<>(createdIssue, HttpStatus.CREATED);
    }

    @GetMapping("/{issueId}")
    public ResponseEntity<Issue> getIssueById(@PathVariable Long userId, @PathVariable Long issueId) {
        Issue issue = issueService.getIssueById(userId, issueId);
        if (issue != null) {
            return new ResponseEntity<>(issue, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Issue>> getIssuesByUser(@PathVariable Long userId) {
        List<Issue> issues = issueService.getIssuesByUser(userId);
        return new ResponseEntity<>(issues, HttpStatus.OK);
    }

    @PutMapping("/{issueId}")
    public ResponseEntity<Issue> updateIssue(@PathVariable Long userId, @PathVariable Long issueId, @RequestBody Issue issue) {
        Issue updatedIssue = issueService.updateIssue(userId, issueId, issue);
        if (updatedIssue != null) {
            return new ResponseEntity<>(updatedIssue, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{issueId}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long userId, @PathVariable Long issueId) {
        boolean deleted = issueService.deleteIssue(userId, issueId);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

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
}