package coms309.issues;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/issues")
public class IssueController {

    private HashMap<String, Issue> issueList = new HashMap<>();

    @GetMapping
    public ResponseEntity<List<Issue>> getAllIssues() {
        return ResponseEntity.ok(new ArrayList<>(issueList.values()));
    }

    @PostMapping
    public ResponseEntity<Issue> createIssue(@RequestBody Issue issue) {
        String id = UUID.randomUUID().toString();
        issue.setId(id);
        issue.setStatus(IssueStatus.REPORTED);
        issueList.put(id, issue);
        return ResponseEntity.ok(issue);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Issue> getIssue(@PathVariable String id) {
        Issue issue = issueList.get(id);
        if (issue != null) {
            return ResponseEntity.ok(issue);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Issue> updateIssue(@PathVariable String id, @RequestBody Issue updatedIssue) {
        if (issueList.containsKey(id)) {
            updatedIssue.setId(id);
            issueList.put(id, updatedIssue);
            return ResponseEntity.ok(updatedIssue);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable String id) {
        if (issueList.remove(id) != null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Issue>> getIssuesByCategory(@PathVariable String category) {
        List<Issue> filteredIssues = issueList.values().stream()
                .filter(issue -> issue.getCategory().equalsIgnoreCase(category))
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(filteredIssues);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Issue>> getIssuesByStatus(@PathVariable IssueStatus status) {
        List<Issue> filteredIssues = issueList.values().stream()
                .filter(issue -> issue.getStatus() == status)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(filteredIssues);
    }
}