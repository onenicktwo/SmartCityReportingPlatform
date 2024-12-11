package org.citywatcher.controller;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.citywatcher.dto.*;
import org.citywatcher.model.Issue;
import org.citywatcher.model.IssueStatus;
import org.citywatcher.model.User;
import org.citywatcher.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AnalyticsControllerTest {

    @LocalServerPort
    private int port;

    private User testAdmin;
    private User testOfficial;
    private User testVolunteer;
    private List<Issue> testIssues = new ArrayList<>();


    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        testAdmin = createUser("admin", "admin@example.com", "password", UserRole.ADMIN);
        testOfficial = createUser("official", "official@example.com", "password", UserRole.CITY_OFFICIAL);
        testVolunteer = createUser("volunteer", "volunteer@example.com", "password", UserRole.VOLUNTEER);

        testIssues.add(createIssue("Issue 1", "Desc 1", "Cat 1", IssueStatus.REPORTED, testAdmin, null, 34.0522, -118.2437, "101 Main St"));
        testIssues.add(createIssue("Issue 2", "Desc 2", "Cat 2", IssueStatus.UNDER_REVIEW, testAdmin, testOfficial, 34.0522, -118.2437, "102 Main St"));
        testIssues.add(createIssue("Issue 3", "Desc 3", "Cat 1", IssueStatus.COMPLETED, testAdmin, testOfficial, 34.0522, -118.2437, "103 Main St"));

        assignVolunteerToIssue(testAdmin.getId(), testIssues.get(0).getId(), testVolunteer.getId());

    }

    private User createUser(String username, String email, String password, UserRole role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(user)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response().as(User.class);
    }

    private Issue createIssue(String title, String description, String category, IssueStatus status, User user, User official, double latitude, double longitude, String address) {
        Issue issue = new Issue();
        issue.setTitle(title);
        issue.setDescription(description);
        issue.setCategory(category);
        issue.setStatus(status);
        issue.setReporter(user);
        issue.setAssignedOfficial(official); //
        issue.setLatitude(latitude);
        issue.setLongitude(longitude);
        issue.setAddress(address);

        return given()
                .pathParam("userId", user.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(issue)
                .when()
                .post("/citywatcher/users/{userId}/issues")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response().as(Issue.class);
    }

    private void assignVolunteerToIssue(Long userId, Long issueId, Long volunteerId) {
        given()
                .pathParam("userId", userId)
                .pathParam("issueId", issueId)
                .pathParam("volunteerId", volunteerId)
                .when()
                .post("/citywatcher/users/{userId}/issues/{issueId}/volunteers/{volunteerId}") // Correct endpoint
                .then()
                .statusCode(HttpStatus.OK.value());
    }



    @Test
    public void testGetStatusStats() {
        Response response = given()
                .param("startDate", "2023-01-01") // Example date, adjust as needed
                .param("endDate", "2024-12-31")
                .when()
                .get("/citywatcher/analytics/status-stats")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        StatusStats stats = response.as(StatusStats.class);
        assertEquals(testIssues.size(), stats.getTotalIssues());
    }

    @Test
    public void testGetOfficialWorkloadStats() {
        given()
                .when()
                .get("/citywatcher/analytics/official-workload")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", greaterThanOrEqualTo(1));
    }

    @Test
    public void testGetLocationStats() {
        given()
                .when()
                .get("/citywatcher/analytics/location-stats")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", greaterThanOrEqualTo(1));
    }

    @Test
    public void testGetVolunteerStats() {
        given()
                .when()
                .get("/citywatcher/analytics/volunteer-stats")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", greaterThanOrEqualTo(0));

    }

    @Test
    public void testGetResponseTimeStats() {
        given()
                .when()
                .get("/citywatcher/analytics/response-time-stats")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

}