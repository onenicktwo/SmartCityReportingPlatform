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

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password");
        testUser.setRole(UserRole.ADMIN);

        testIssue = new Issue();
        testIssue.setTitle("Broken Streetlight");
        testIssue.setDescription("The streetlight on Main St. is not working.");
        testIssue.setCategory("Infrastructure");
        testIssue.setStatus(IssueStatus.REPORTED);
        testIssue.setLatitude(40.7128);
        testIssue.setLongitude(-74.0060);
        testIssue.setAddress("123 Main St.");
    }

    @Test
    public void testCreateIssue() {
        Response userResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        User createdUser = userResponse.as(User.class);

        Response issueResponse = given()
                .pathParam("userId", createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testIssue)
                .when()
                .post("/citywatcher/users/{userId}/issues")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        Issue createdIssue = issueResponse.as(Issue.class);

        assertNotNull(createdIssue.getId());
        assertEquals(testIssue.getTitle(), createdIssue.getTitle());
        assertEquals(IssueStatus.REPORTED, createdIssue.getStatus()); // Default status
    }

    @Test
    public void testGetIssueById() {
        Response userResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        User createdUser = userResponse.as(User.class);

        Response issueResponse = given()
                .pathParam("userId", createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testIssue)
                .when()
                .post("/citywatcher/users/{userId}/issues")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        Issue createdIssue = issueResponse.as(Issue.class);

        given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .when()
                .get("/citywatcher/users/{userId}/issues/{issueId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(createdIssue.getId().intValue()))
                .body("title", equalTo(createdIssue.getTitle()))
                .body("status", equalTo("REPORTED"));
    }

    @Test
    public void testGetIssuesByUser() {
        Response userResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        User createdUser = userResponse.as(User.class);

        given()
                .pathParam("userId", createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testIssue)
                .when()
                .post("/citywatcher/users/{userId}/issues")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        testIssue.setTitle("Pothole on Main Street");
        given()
                .pathParam("userId", createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testIssue)
                .when()
                .post("/citywatcher/users/{userId}/issues")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .pathParam("userId", createdUser.getId())
                .when()
                .get("/citywatcher/users/{userId}/issues")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", greaterThanOrEqualTo(2))
                .body("[0].title", notNullValue())
                .body("[1].title", notNullValue());
    }

    @Test
    public void testUpdateIssue() {
        Response userResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        User createdUser = userResponse.as(User.class);

        Response issueResponse = given()
                .pathParam("userId", createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testIssue)
                .when()
                .post("/citywatcher/users/{userId}/issues")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        Issue createdIssue = issueResponse.as(Issue.class);

        Issue updatedIssue = new Issue();
        updatedIssue.setTitle("Updated Title");
        updatedIssue.setDescription("Updated Description");
        updatedIssue.setStatus(IssueStatus.UNDER_REVIEW);

        given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(updatedIssue)
                .when()
                .put("/citywatcher/users/{userId}/issues/{issueId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("title", equalTo("Updated Title"))
                .body("description", equalTo("Updated Description"))
                .body("status", equalTo("UNDER_REVIEW"));
    }

    @Test
    public void testDeleteIssue() {
        Response userResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        User createdUser = userResponse.as(User.class);

        Response issueResponse = given()
                .pathParam("userId", createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testIssue)
                .when()
                .post("/citywatcher/users/{userId}/issues")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        Issue createdIssue = issueResponse.as(Issue.class);

        given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .when()
                .delete("/citywatcher/users/{userId}/issues/{issueId}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void testUploadIssueImage() {
        Response userResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        User createdUser = userResponse.as(User.class);

        Response issueResponse = given()
                .pathParam("userId", createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testIssue)
                .when()
                .post("/citywatcher/users/{userId}/issues")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        Issue createdIssue = issueResponse.as(Issue.class);

        String imageBase64 = Base64.getEncoder().encodeToString("dummy image data".getBytes());
        String requestBody = "{ \"imageBase64\": \"" + imageBase64 + "\", \"fileName\": \"issue_image.jpg\" }";

        given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when()
                .post("/citywatcher/users/{userId}/issues/{issueId}/image")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testAssignVolunteerToIssue() {
        Response userResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        User createdAdmin = userResponse.as(User.class);

        User volunteerUser = new User();
        volunteerUser.setUsername("volunteer");
        volunteerUser.setEmail("volunteer@example.com");
        volunteerUser.setPassword("password");
        volunteerUser.setRole(UserRole.VOLUNTEER);

        Response volunteerResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(volunteerUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        User createdVolunteer = volunteerResponse.as(User.class);

        Response issueResponse = given()
                .pathParam("userId", createdAdmin.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testIssue)
                .when()
                .post("/citywatcher/users/{userId}/issues")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        Issue createdIssue = issueResponse.as(Issue.class);

        given()
                .pathParam("userId", createdAdmin.getId())
                .pathParam("issueId", createdIssue.getId())
                .pathParam("volunteerId", createdVolunteer.getId())
                .when()
                .post("/citywatcher/users/{userId}/issues/{issueId}/volunteers/{volunteerId}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testRemoveVolunteerFromIssue() {
        Response userResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        User createdAdmin = userResponse.as(User.class);

        User volunteerUser = new User();
        volunteerUser.setUsername("volunteer");
        volunteerUser.setEmail("volunteer@example.com");
        volunteerUser.setPassword("password");
        volunteerUser.setRole(UserRole.VOLUNTEER);

        Response volunteerResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(volunteerUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        User createdVolunteer = volunteerResponse.as(User.class);

        Response issueResponse = given()
                .pathParam("userId", createdAdmin.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testIssue)
                .when()
                .post("/citywatcher/users/{userId}/issues")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        Issue createdIssue = issueResponse.as(Issue.class);

        given()
                .pathParam("userId", createdAdmin.getId())
                .pathParam("issueId", createdIssue.getId())
                .pathParam("volunteerId", createdVolunteer.getId())
                .when()
                .post("/citywatcher/users/{userId}/issues/{issueId}/volunteers/{volunteerId}")
                .then()
                .statusCode(HttpStatus.OK.value());

        given()
                .pathParam("userId", createdAdmin.getId())
                .pathParam("issueId", createdIssue.getId())
                .pathParam("volunteerId", createdVolunteer.getId())
                .when()
                .delete("/citywatcher/users/{userId}/issues/{issueId}/volunteers/{volunteerId}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testSearchIssuesByCategory() {
        Response userResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        User createdUser = userResponse.as(User.class);

        Issue issue1 = new Issue();
        issue1.setTitle("Power Outage");
        issue1.setDescription("There is a power outage in the area.");
        issue1.setCategory("Electrical");
        issue1.setStatus(IssueStatus.REPORTED);
        issue1.setLatitude(40.7128);
        issue1.setLongitude(-74.0060);
        issue1.setAddress("456 Main St.");

        Issue issue2 = new Issue();
        issue2.setTitle("Water Leak");
        issue2.setDescription("There is a water leak in the basement.");
        issue2.setCategory("Plumbing");
        issue2.setStatus(IssueStatus.REPORTED);
        issue2.setLatitude(40.7128);
        issue2.setLongitude(-74.0060);
        issue2.setAddress("789 Main St.");

        given()
                .pathParam("userId", createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(issue1)
                .when()
                .post("/citywatcher/users/{userId}/issues")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .pathParam("userId", createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(issue2)
                .when()
                .post("/citywatcher/users/{userId}/issues")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .pathParam("userId", createdUser.getId())
                .param("category", "Electrical")
                .when()
                .get("/citywatcher/users/{userId}/issues/search")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$.size()", equalTo(1)) // Expecting only one issue to match
                .body("[0].title", equalTo("Power Outage"))
                .body("[0].category", equalTo("Electrical"));
    }

    @Test
    public void testGetIssueImage() {
        Response userResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        User createdUser = userResponse.as(User.class);

        Issue testIssue = new Issue();
        testIssue.setTitle("Broken Streetlight");
        testIssue.setDescription("The streetlight on Main St. is not working.");
        testIssue.setCategory("Infrastructure");
        testIssue.setStatus(IssueStatus.REPORTED);
        testIssue.setLatitude(40.7128);
        testIssue.setLongitude(-74.0060);
        testIssue.setAddress("123 Main St.");

        Response issueResponse = given()
                .pathParam("userId", createdUser.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testIssue)
                .when()
                .post("/citywatcher/users/{userId}/issues")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        Issue createdIssue = issueResponse.as(Issue.class);

        String imageBase64 = Base64.getEncoder().encodeToString("dummy image data".getBytes());
        String imageRequestBody = "{ \"imageBase64\": \"" + imageBase64 + "\", \"fileName\": \"issue_image.jpg\" }";

        given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(imageRequestBody)
                .when()
                .post("/citywatcher/users/{userId}/issues/{issueId}/image")
                .then()
                .statusCode(HttpStatus.OK.value());

        given()
                .pathParam("userId", createdUser.getId())
                .pathParam("issueId", createdIssue.getId())
                .when()
                .get("/citywatcher/users/{userId}/issues/{issueId}/image")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.IMAGE_JPEG_VALUE);
    }
}