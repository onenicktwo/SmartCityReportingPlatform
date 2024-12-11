package org.citywatcher.controller;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.citywatcher.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentControllerTest {

    @LocalServerPort
    private int port;

    private User testUser;
    private Issue testIssue;
    private Comment testComment;
    private Report testReport;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password");
        testUser.setRole(UserRole.CITIZEN);

        testIssue = new Issue();
        testIssue.setTitle("Test Issue");
        testIssue.setDescription("Test Description");
        testIssue.setCategory("Test Category");
        testIssue.setLatitude(0.0);
        testIssue.setLongitude(0.0);
        testIssue.setAddress("Test Ave");
        testIssue.setStatus(IssueStatus.REPORTED);

        testComment = new Comment();
        testComment.setContent("Test comment content");
        testComment.setInternalNote(false);

        testReport = new Report();
        testReport.setReason("Inappropriate content");
    }

    private User createTestUser() {
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();
        return response.as(User.class);
    }

    private Issue createTestIssue(Long userId) {
        Response response = given()
                .pathParam("userId", userId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testIssue)
                .when()
                .post("/citywatcher/users/{userId}/issues")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();
        return response.as(Issue.class);
    }

    @Test
    public void testCreateComment() {
        User createdUser = createTestUser();
        Issue createdIssue = createTestIssue(createdUser.getId());

        Response commentResponse = given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testComment)
                .when()
                .post("/citywatcher/users/{userId}/issues/{issueId}/comments")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        Comment createdComment = commentResponse.as(Comment.class);
        assertNotNull(createdComment.getId());
        assertEquals(testComment.getContent(), createdComment.getContent());
    }

    @Test
    public void testGetCommentById() {
        User createdUser = createTestUser();
        Issue createdIssue = createTestIssue(createdUser.getId());

        Response commentResponse = given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testComment)
                .when()
                .post("/citywatcher/users/{userId}/issues/{issueId}/comments")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        Comment createdComment = commentResponse.as(Comment.class);

        given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .pathParam("commentId", createdComment.getId())
                .when()
                .get("/citywatcher/users/{userId}/issues/{issueId}/comments/{commentId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(createdComment.getId().intValue()))
                .body("content", equalTo(testComment.getContent()));
    }

    @Test
    public void testGetCommentsByIssue() {
        User createdUser = createTestUser();
        Issue createdIssue = createTestIssue(createdUser.getId());

        given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testComment)
                .when()
                .post("/citywatcher/users/{userId}/issues/{issueId}/comments")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        Comment secondComment = new Comment();
        secondComment.setContent("Second test comment");
        secondComment.setInternalNote(false);

        given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(secondComment)
                .when()
                .post("/citywatcher/users/{userId}/issues/{issueId}/comments")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .when()
                .get("/citywatcher/users/{userId}/issues/{issueId}/comments")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(2))
                .body("content", hasItems(testComment.getContent(), secondComment.getContent()));
    }

    @Test
    public void testUpdateComment() {
        User createdUser = createTestUser();
        Issue createdIssue = createTestIssue(createdUser.getId());

        Response commentResponse = given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testComment)
                .when()
                .post("/citywatcher/users/{userId}/issues/{issueId}/comments")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        Comment createdComment = commentResponse.as(Comment.class);

        createdComment.setContent("Updated content");

        Response updatedResponse = given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .pathParam("commentId", createdComment.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createdComment)
                .when()
                .put("/citywatcher/users/{userId}/issues/{issueId}/comments/{commentId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        Comment updatedComment = updatedResponse.as(Comment.class);
        assertEquals("Updated content", updatedComment.getContent());
    }

    @Test
    public void testDeleteComment() {
        User createdUser = createTestUser();
        Issue createdIssue = createTestIssue(createdUser.getId());

        Response commentResponse = given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testComment)
                .when()
                .post("/citywatcher/users/{userId}/issues/{issueId}/comments")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        Comment createdComment = commentResponse.as(Comment.class);

        given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .pathParam("commentId", createdComment.getId())
                .when()
                .delete("/citywatcher/users/{userId}/issues/{issueId}/comments/{commentId}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .pathParam("commentId", createdComment.getId())
                .when()
                .get("/citywatcher/users/{userId}/issues/{issueId}/comments/{commentId}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testReportComment() {
        User createdUser = createTestUser();
        Issue createdIssue = createTestIssue(createdUser.getId());

        Response commentResponse = given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testComment)
                .when()
                .post("/citywatcher/users/{userId}/issues/{issueId}/comments")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        Comment createdComment = commentResponse.as(Comment.class);

        Response reportResponse = given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .pathParam("commentId", createdComment.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testReport)
                .when()
                .post("/citywatcher/users/{userId}/issues/{issueId}/comments/{commentId}/report")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        Report createdReport = reportResponse.as(Report.class);
        assertNotNull(createdReport.getId());
        assertEquals(testReport.getReason(), createdReport.getReason());
    }
}