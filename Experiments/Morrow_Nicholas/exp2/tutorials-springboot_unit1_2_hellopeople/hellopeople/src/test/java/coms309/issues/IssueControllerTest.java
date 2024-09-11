package coms309.issues;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IssueControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/issues";
    }

    @Test
    public void testCreateIssue() {
        Issue newIssue = new Issue("Pothole on Main St", "POTHOLE", "John Doe", "123 Main St");
        ResponseEntity<Issue> response = restTemplate.postForEntity(baseUrl, newIssue, Issue.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getId(), is(notNullValue()));
        assertThat(response.getBody().getStatus(), is(IssueStatus.REPORTED));
    }

    @Test
    public void testGetAllIssues() {
        ResponseEntity<List> response = restTemplate.getForEntity(baseUrl, List.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
    }

    @Test
    public void testGetIssueById() {
        Issue newIssue = new Issue("Graffiti on Wall", "GRAFFITI", "Jane Doe", "456 Elm St");
        ResponseEntity<Issue> createResponse = restTemplate.postForEntity(baseUrl, newIssue, Issue.class);
        String issueId = createResponse.getBody().getId();

        ResponseEntity<Issue> getResponse = restTemplate.getForEntity(baseUrl + "/" + issueId, Issue.class);

        assertThat(getResponse.getStatusCode(), is(HttpStatus.OK));
        assertThat(getResponse.getBody(), is(notNullValue()));
        assertThat(getResponse.getBody().getId(), is(issueId));
    }

    @Test
    public void testUpdateIssue() {
        Issue newIssue = new Issue("Noise Complaint", "NOISE", "Bob Smith", "789 Oak St");
        ResponseEntity<Issue> createResponse = restTemplate.postForEntity(baseUrl, newIssue, Issue.class);
        String issueId = createResponse.getBody().getId();

        Issue updatedIssue = new Issue("Resolved Noise Complaint", "NOISE", "Bob Smith", "789 Oak St");
        updatedIssue.setStatus(IssueStatus.RESOLVED);
        restTemplate.put(baseUrl + "/" + issueId, updatedIssue);

        ResponseEntity<Issue> getResponse = restTemplate.getForEntity(baseUrl + "/" + issueId, Issue.class);

        assertThat(getResponse.getStatusCode(), is(HttpStatus.OK));
        assertThat(getResponse.getBody(), is(notNullValue()));
        assertThat(getResponse.getBody().getDescription(), is("Resolved Noise Complaint"));
    }
}