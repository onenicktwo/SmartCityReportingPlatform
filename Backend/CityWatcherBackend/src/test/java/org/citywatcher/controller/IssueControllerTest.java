package org.citywatcher.controller;

import io.restassured.RestAssured;
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

import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class IssueControllerTest {

    @LocalServerPort
    private int port;

    private User testUser;

    private Issue testIssue;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        testUser = createTestUserObject(UserRole.ADMIN);
        testIssue = createTestIssueObject();
    }

    private User createTestUserObject(UserRole role) {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setRole(role);
        return user;
    }

    private Issue createTestIssueObject() {
        Issue issue = new Issue();
        issue.setTitle("Broken Streetlight");
        issue.setDescription("The streetlight on Main St. is not working.");
        issue.setCategory("Infrastructure");
        issue.setStatus(IssueStatus.REPORTED);
        issue.setLatitude(40.7128);
        issue.setLongitude(-74.0060);
        issue.setAddress("123 Main St.");
        return issue;
    }

    private User registerUser(User user) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(user)
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(User.class);
    }

    private Issue createIssue(Long userId, Issue issue) {
        return given()
                .pathParam("userId", userId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(issue)
                .post("/citywatcher/users/{userId}/issues")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(Issue.class);
    }

    private User createVolunteerUser() {
        User volunteerUser = createTestUserObject(UserRole.VOLUNTEER);
        volunteerUser.setUsername("volunteer");
        volunteerUser.setEmail("volunteer@example.com");
        return registerUser(volunteerUser);
    }

    private void assignVolunteerToIssue(Long userId, Long issueId, Long volunteerId) {
        given()
                .pathParam("userId", userId)
                .pathParam("issueId", issueId)
                .pathParam("volunteerId", volunteerId)
                .post("/citywatcher/users/{userId}/issues/{issueId}/volunteers/{volunteerId}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    private void uploadIssueImage(Long userId, Long issueId, String fileName) {
        String imageBase64 = Base64.getEncoder().encodeToString("dummy image data".getBytes());
        String imageRequestBody = String.format("{ \"imageBase64\": \"%s\", \"fileName\": \"%s\" }", imageBase64, fileName);

        given()
                .pathParam("userId", userId)
                .pathParam("issueId", issueId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(imageRequestBody)
                .post("/citywatcher/users/{userId}/issues/{issueId}/image")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testCreateIssue() {
        User createdUser = registerUser(testUser);
        Issue createdIssue = createIssue(createdUser.getId(), testIssue);

        assertNotNull(createdIssue.getId());
        assertEquals(testIssue.getTitle(), createdIssue.getTitle());
        assertEquals(IssueStatus.REPORTED, createdIssue.getStatus());
    }

    @Test
    public void testGetIssueById() {
        User createdUser = registerUser(testUser);
        Issue createdIssue = createIssue(createdUser.getId(), testIssue);

        given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .get("/citywatcher/users/{userId}/issues/{issueId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(createdIssue.getId().intValue()))
                .body("title", equalTo(createdIssue.getTitle()))
                .body("status", equalTo("REPORTED"));
    }

    @Test
    public void testGetIssuesByUser() {
        User user = registerUser(testUser);
        createIssue(user.getId(), testIssue);

        Issue secondIssue = createTestIssueObject();
        secondIssue.setTitle("Pothole on Main Street");
        createIssue(user.getId(), secondIssue);

        given()
                .pathParam("userId", user.getId())
                .get("/citywatcher/users/{userId}/issues")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", greaterThanOrEqualTo(2))
                .body("[0].title", notNullValue())
                .body("[1].title", notNullValue());
    }

    @Test
    public void testUpdateIssue() {
        User user = registerUser(testUser);
        Issue createdIssue = createIssue(user.getId(), testIssue);

        Issue updatedIssue = new Issue();
        updatedIssue.setTitle("Updated Title");
        updatedIssue.setDescription("Updated Description");
        updatedIssue.setStatus(IssueStatus.UNDER_REVIEW);

        given()
                .pathParam("userId", user.getId())
                .pathParam("issueId", createdIssue.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(updatedIssue)
                .put("/citywatcher/users/{userId}/issues/{issueId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("title", equalTo("Updated Title"))
                .body("description", equalTo("Updated Description"))
                .body("status", equalTo("UNDER_REVIEW"));
    }

    @Test
    public void testDeleteIssue() {
        User user = registerUser(testUser);
        Issue createdIssue = createIssue(user.getId(), testIssue);

        given()
                .pathParam("userId", user.getId())
                .pathParam("issueId", createdIssue.getId())
                .delete("/citywatcher/users/{userId}/issues/{issueId}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void testAssignVolunteerToIssue() {
        User admin = registerUser(testUser);
        User volunteer = createVolunteerUser();
        Issue createdIssue = createIssue(admin.getId(), testIssue);

        assignVolunteerToIssue(admin.getId(), createdIssue.getId(), volunteer.getId());
    }

    @Test
    public void testRemoveVolunteerFromIssue() {
        User admin = registerUser(testUser);
        User volunteer = createVolunteerUser();
        Issue createdIssue = createIssue(admin.getId(), testIssue);

        assignVolunteerToIssue(admin.getId(), createdIssue.getId(), volunteer.getId());

        given()
                .pathParam("userId", admin.getId())
                .pathParam("issueId", createdIssue.getId())
                .pathParam("volunteerId", volunteer.getId())
                .delete("/citywatcher/users/{userId}/issues/{issueId}/volunteers/{volunteerId}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }


    @Test
    public void testSearchIssuesByCategory() {
        User user = registerUser(testUser);

        Issue electricalIssue = createTestIssueObject();
        electricalIssue.setTitle("Power Outage");
        electricalIssue.setCategory("Electrical");
        createIssue(user.getId(), electricalIssue);

        Issue plumbingIssue = createTestIssueObject();
        plumbingIssue.setTitle("Water Leak");
        plumbingIssue.setCategory("Plumbing");
        createIssue(user.getId(), plumbingIssue);

        given()
                .pathParam("userId", user.getId())
                .param("category", "Electrical")
                .get("/citywatcher/users/{userId}/issues/search")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1))
                .body("[0].title", equalTo("Power Outage"))
                .body("[0].category", equalTo("Electrical"));
    }

    @Test
    public void testUploadIssueImage() {
        User user = registerUser(testUser);
        Issue issue = createIssue(user.getId(), testIssue);
        uploadIssueImage(user.getId(), issue.getId(), "issue_image.jpg");
    }

    @Test
    public void testGetIssueImage() {
        User user = registerUser(testUser);
        Issue issue = createIssue(user.getId(), testIssue);
        uploadIssueImage(user.getId(), issue.getId(), "issue_image.jpg");

        given()
                .pathParam("userId", user.getId())
                .pathParam("issueId", issue.getId())
                .get("/citywatcher/users/{userId}/issues/{issueId}/image")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.IMAGE_JPEG_VALUE);
    }
}