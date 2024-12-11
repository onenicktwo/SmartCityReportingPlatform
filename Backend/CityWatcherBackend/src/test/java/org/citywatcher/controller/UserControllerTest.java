package org.citywatcher.controller;

import io.restassured.RestAssured;
import io.restassured.response.Response;
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {

    @LocalServerPort
    private int port;
    private User testUser;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        testUser = createTestUserObject();
    }

    private User createTestUserObject() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setRole(UserRole.CITIZEN);
        return user;
    }

    private Issue createTestIssueObject(String title, String category) {
        Issue issue = new Issue();
        issue.setTitle(title);
        issue.setDescription("Test description");
        issue.setCategory(category);
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

    private void uploadUserImage(Long userId, String fileName) {
        String imageBase64 = Base64.getEncoder().encodeToString("image data".getBytes());
        String requestBody = String.format("{ \"imageBase64\": \"%s\", \"fileName\": \"%s\" }", imageBase64, fileName);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .post("/citywatcher/users/" + userId + "/image")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testRegisterUser() {
        User createdUser = registerUser(testUser);
        assertNotNull(createdUser.getId());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
    }

    @Test
    public void testUploadUserImage() {
        User createdUser = registerUser(testUser);
        uploadUserImage(createdUser.getId(), "profile.jpg");
    }

    @Test
    public void testGetUserById() {
        User createdUser = registerUser(testUser);

        given()
                .get("/citywatcher/users/" + createdUser.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(createdUser.getId().intValue()))
                .body("username", equalTo(createdUser.getUsername()));
    }

    @Test
    public void testGetAllUsers() {
        registerUser(testUser);

        User secondUser = createTestUserObject();
        secondUser.setUsername("testuser2");
        secondUser.setEmail("testuser2@example.com");
        registerUser(secondUser);

        given()
                .get("/citywatcher/users")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", greaterThanOrEqualTo(2));
    }

    @Test
    public void testUpdateUser() {
        User createdUser = registerUser(testUser);
        createdUser.setUsername("updateduser");
        createdUser.setEmail("updateduser@example.com");

        User updatedUser = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createdUser)
                .put("/citywatcher/users/" + createdUser.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().as(User.class);

        assertEquals("updateduser", updatedUser.getUsername());
        assertEquals("updateduser@example.com", updatedUser.getEmail());
    }

    @Test
    public void testDeleteUser() {
        User createdUser = registerUser(testUser);

        given()
                .delete("/citywatcher/users/" + createdUser.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .get("/citywatcher/users/" + createdUser.getId())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testUploadAndGetUserImage() {
        User createdUser = registerUser(testUser);
        uploadUserImage(createdUser.getId(), "profile.jpg");

        given()
                .get("/citywatcher/users/" + createdUser.getId() + "/image")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.IMAGE_JPEG_VALUE);
    }

    private void followIssue(Long userId, Long issueId) {
        given()
                .pathParam("userId", userId)
                .pathParam("issueId", issueId)
                .post("/citywatcher/users/{userId}/followed-issues/{issueId}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    private void unfollowIssue(Long userId, Long issueId) {
        given()
                .pathParam("userId", userId)
                .pathParam("issueId", issueId)
                .delete("/citywatcher/users/{userId}/followed-issues/{issueId}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testFollowIssue() {
        User createdUser = registerUser(testUser);
        Issue createdIssue = createIssue(createdUser.getId(), createTestIssueObject("Broken Streetlight", "Infrastructure"));
        followIssue(createdUser.getId(), createdIssue.getId());
    }

    @Test
    public void testUnfollowIssue() {
        User createdUser = registerUser(testUser);
        Issue createdIssue = createIssue(createdUser.getId(), createTestIssueObject("Broken Streetlight", "Infrastructure"));

        followIssue(createdUser.getId(), createdIssue.getId());
        unfollowIssue(createdUser.getId(), createdIssue.getId());
    }

    @Test
    public void testGetFollowedIssues() {
        User createdUser = registerUser(testUser);

        Issue issue1 = createIssue(createdUser.getId(),
                createTestIssueObject("Broken Streetlight", "Infrastructure"));
        followIssue(createdUser.getId(), issue1.getId());

        Issue issue2 = createIssue(createdUser.getId(),
                createTestIssueObject("Water Leak", "Plumbing"));
        followIssue(createdUser.getId(), issue2.getId());

        given()
                .pathParam("userId", createdUser.getId())
                .get("/citywatcher/users/{userId}/followed-issues")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(2))
                .body("[0].title", equalTo("Broken Streetlight"))
                .body("[1].title", equalTo("Water Leak"));
    }
}