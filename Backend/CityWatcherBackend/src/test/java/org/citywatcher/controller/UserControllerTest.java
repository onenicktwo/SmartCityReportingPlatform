package org.citywatcher.controller;

import io.restassured.RestAssured;
import io.restassured.response.Response;
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

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password");
        testUser.setRole(UserRole.CITIZEN);
    }

    @Test
    public void testRegisterUser() {
        Response response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        User createdUser = response.as(User.class);
        assertNotNull(createdUser.getId());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
    }

    @Test
    public void testUploadUserImage() {
        Response registerResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        User createdUser = registerResponse.as(User.class);

        String imageBase64 = Base64.getEncoder().encodeToString("image data".getBytes());
        String requestBody = "{ \"imageBase64\": \"" + imageBase64 + "\", \"fileName\": \"profile.jpg\" }";

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when()
                .post("/citywatcher/users/" + createdUser.getId() + "/image")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void testGetUserById() {
        Response createResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        User createdUser = createResponse.as(User.class);

        given()
                .when()
                .get("/citywatcher/users/" + createdUser.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(createdUser.getId().intValue()))
                .body("username", equalTo(createdUser.getUsername()));
    }

    @Test
    public void testGetAllUsers() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register");

        testUser.setUsername("testuser2");
        testUser.setEmail("testuser2@example.com");

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register");

        given()
                .when()
                .get("/citywatcher/users")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("size()", greaterThanOrEqualTo(2));
    }

    @Test
    public void testUpdateUser() {
        Response createResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        User createdUser = createResponse.as(User.class);
        createdUser.setUsername("updateduser");
        createdUser.setEmail("updateduser@example.com");

        Response updateResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createdUser)
                .when()
                .put("/citywatcher/users/" + createdUser.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().response();

        User updatedUser = updateResponse.as(User.class);
        assertEquals("updateduser", updatedUser.getUsername());
        assertEquals("updateduser@example.com", updatedUser.getEmail());
    }

    @Test
    public void testDeleteUser() {
        Response createResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        User createdUser = createResponse.as(User.class);

        given()
                .when()
                .delete("/citywatcher/users/" + createdUser.getId())
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        given()
                .when()
                .get("/citywatcher/users/" + createdUser.getId())
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void testUploadAndGetUserImage() {
        Response registerResponse = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(testUser)
                .when()
                .post("/citywatcher/users/register")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().response();

        User createdUser = registerResponse.as(User.class);

        String imageBase64 = Base64.getEncoder().encodeToString("image data".getBytes());
        String requestBody = "{ \"imageBase64\": \"" + imageBase64 + "\", \"fileName\": \"profile.jpg\" }";

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .when()
                .post("/citywatcher/users/" + createdUser.getId() + "/image")
                .then()
                .statusCode(HttpStatus.OK.value());

        given()
                .when()
                .get("/citywatcher/users/" + createdUser.getId() + "/image")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(MediaType.IMAGE_JPEG_VALUE);
    }
}