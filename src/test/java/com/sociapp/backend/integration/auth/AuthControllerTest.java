package com.sociapp.backend.integration.auth;

import com.sociapp.backend.user.UserRepository;
import com.sociapp.backend.user.UserService;
import com.sociapp.backend.util.ResetDatabaseTestExecutionListener;
import com.sociapp.backend.util.TestUtil;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;

import static com.sociapp.backend.util.TestUtil.authenticateAndGetToken;
import static com.sociapp.backend.util.TestUtil.postJsonAndReceiveResponse;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@TestExecutionListeners(mergeMode =
        TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
        listeners = {ResetDatabaseTestExecutionListener.class}
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthControllerTest {

    private static final String API_1_0_AUTH = "/api/1.0/auth";
    private static final String API_1_0_LOGOUT = "/api/1.0/logout";

    @LocalServerPort
    private int ports;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = ports;
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    void handleAuthentication_Success() {
        userService.save(TestUtil.createValidUser("user1"));

        JSONObject jsonObject = new JSONObject()
                .put("username", "user1")
                .put("password", "P4ssword");
        Response response = postJsonAndReceiveResponse(jsonObject, API_1_0_AUTH);

        assertEquals(200, response.statusCode());
        assertEquals("user1", response.getBody().jsonPath().getString("userDto.username"));
        assertEquals("test-display", response.getBody().jsonPath().getString("userDto.displayName"));
        assertEquals("profile-image.png", response.getBody().jsonPath().getString("userDto.image"));
    }

    @SneakyThrows
    @Test
    void handleAuthentication_WithIncorrectUsername_ShouldReturnError() {
        userService.save(TestUtil.createValidUser("user1"));

        JSONObject jsonObject = new JSONObject()
                .put("username", "user2")
                .put("password", "P4ssword");
        Response response = postJsonAndReceiveResponse(jsonObject, API_1_0_AUTH);

        assertEquals(401, response.statusCode());
        assertEquals("Username is incorrect", response.getBody().jsonPath().getString("errorMessage"));
    }

    @SneakyThrows
    @Test
    void handleAuthentication_WithIncorrectPassword_ShouldReturnError() {
        userService.save(TestUtil.createValidUser("user1"));

        JSONObject jsonObject = new JSONObject()
                .put("username", "user1")
                .put("password", "P4sswordd");
        Response response = postJsonAndReceiveResponse(jsonObject, API_1_0_AUTH);

        assertEquals(401, response.statusCode());
        assertEquals("Password is incorrect", response.getBody().jsonPath().getString("errorMessage"));
    }


    @Test
    void logout_Success() {
        userService.save(TestUtil.createValidUser("user1"));

        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        Response response = given()
                .headers("Authorization", "Bearer " + token)
                .when().log().all()
                .post(API_1_0_LOGOUT)
                .then().log().all()
                .extract().response();

        assertEquals(200, response.statusCode());
        assertEquals("Successfully logout", response.getBody().jsonPath().getString("message"));
    }
}