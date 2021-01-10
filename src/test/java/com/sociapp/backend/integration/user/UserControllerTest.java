package com.sociapp.backend.integration.user;

import com.sociapp.backend.user.User;
import com.sociapp.backend.user.UserRepository;
import com.sociapp.backend.user.UserService;
import com.sociapp.backend.util.ResetDatabaseTestExecutionListener;
import com.sociapp.backend.util.TestUtil;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;

import java.io.IOException;
import java.util.List;

import static com.sociapp.backend.util.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

@TestExecutionListeners(mergeMode =
        TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
        listeners = {ResetDatabaseTestExecutionListener.class}
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerTest {

    private static final String API_1_0_USERS = "/api/1.0/users";
    private static final String API_1_0_AUTH = "/api/1.0/auth";

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
    void createUser_success() {
        JSONObject jsonObject = new JSONObject()
                .put("username", "newUser")
                .put("password", "Kaan1234")
                .put("displayName", "newDisplay");

        Response response = postJsonAndReceiveResponse(jsonObject, API_1_0_USERS);

        assertEquals("User is created successfully", response.getBody().jsonPath().getString("message"));

        User createdUser = userService.getByUsername("newUser");

        assertEquals("newUser", createdUser.getUsername());
        assertEquals("newDisplay", createdUser.getDisplayName());
    }

    @SneakyThrows
    @Test
    void createUser_withIncorrectPassword_shouldThrowError() {
        JSONObject jsonObject = new JSONObject()
                .put("username", "newUser")
                .put("password", "Kaannmert")
                .put("displayName", "newDisplay");

        Response response = postJsonAndReceiveResponse(jsonObject, API_1_0_USERS);

        assertEquals(400, response.statusCode());
        assertEquals("Password must have atleast 1 uppercase letter, 1 lowercase letter and 1 number",
                response.getBody().jsonPath().getString("validationErrors.password"));
    }

    @SneakyThrows
    @Test
    void createExistingUser_shouldThrowError() {
        userService.save(createValidUser("newUser"));

        JSONObject jsonObject = new JSONObject()
                .put("username", "newUser")
                .put("password", "Kaan1234")
                .put("displayName", "newDisplay");

        Response response = postJsonAndReceiveResponse(jsonObject, API_1_0_USERS);

        assertEquals(400, response.statusCode());
        assertEquals("This username is already taken",
                response.getBody().jsonPath().getString("validationErrors.username"));
    }

    @SneakyThrows
    @Test
    void createUser_withoutUsername_shouldThrowError() {
        JSONObject jsonObject = new JSONObject()
                .put("password", "Kaan1234")
                .put("displayName", "newDisplay");

        Response response = postJsonAndReceiveResponse(jsonObject, API_1_0_USERS);

        assertEquals(response.statusCode(), 400);
        assertEquals("Username can not be null!", response.getBody().jsonPath().getString("validationErrors.username"));
    }

    @SneakyThrows
    @Test
    void createUser_withoutPasswordAndDisplayName_shouldThrowError() {
        JSONObject jsonObject = new JSONObject()
                .put("username", "Kaan1234");

        Response response = postJsonAndReceiveResponse(jsonObject, API_1_0_USERS);

        assertEquals(response.statusCode(), 400);
        assertEquals("Password can not be null!", response.getBody().jsonPath().getString("validationErrors.password"));
        assertEquals("Display name can not be null!", response.getBody().jsonPath().getString("validationErrors.displayName"));
    }

    @SneakyThrows
    @Test
    void createUser_withMaxSize_shouldThrowMaxSizeError() {
        JSONObject jsonObject = new JSONObject()
                .put("username", getAlphaNumericString(51))
                .put("password", getAlphaNumericString(51))
                .put("displayName", getAlphaNumericString(51));

        Response response = postJsonAndReceiveResponse(jsonObject, API_1_0_USERS);

        assertEquals(response.statusCode(), 400);
        assertEquals("size must be between 6 and 50", response.getBody().jsonPath().getString("validationErrors.password"));
        assertEquals("size must be between 4 and 50", response.getBody().jsonPath().getString("validationErrors.displayName"));
        assertEquals("size must be between 4 and 50", response.getBody().jsonPath().getString("validationErrors.username"));
    }

    @SneakyThrows
    @Test
    void createUser_withMinSize_shouldThrowMaxSizeError() {
        JSONObject jsonObject = new JSONObject()
                .put("username", getAlphaNumericString(2))
                .put("password", "Kaan1234")
                .put("displayName", getAlphaNumericString(2));

        Response response = postJsonAndReceiveResponse(jsonObject, API_1_0_USERS);

        assertEquals(response.statusCode(), 400);
        assertEquals("size must be between 4 and 50", response.getBody().jsonPath().getString("validationErrors.displayName"));
        assertEquals("size must be between 4 and 50", response.getBody().jsonPath().getString("validationErrors.username"));
    }

    @Test
    void getUsers_whenUserLoggedIn_receivePageWithouLoggedInUser() {
        userService.save(TestUtil.createValidUser("user1"));
        userService.save(TestUtil.createValidUser("user2"));
        userService.save(TestUtil.createValidUser("user3"));

        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        Response response = getRequestAndReceiveResponse(token, API_1_0_USERS);

        assertEquals(response.getBody().jsonPath().getList("content").size(), 2);

        List<String> usernamesInResponse = response.getBody().jsonPath().getList("content.username");

        assertFalse(usernamesInResponse.contains("user1"));
    }

    @Test
    void getUsers_whenThereIsAUserInDb_ButNotLoggedIn() {
        userService.save(TestUtil.createValidUser("user1"));
        userService.save(TestUtil.createValidUser("user2"));
        userService.save(TestUtil.createValidUser("user3"));

        Response response = getRequestAndReceiveResponse(null, API_1_0_USERS);

        assertEquals(response.getBody().jsonPath().getList("content").size(), 3);
    }

    @Test
    void getUser_successfully() {
        userService.save(TestUtil.createValidUser("user1"));

        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        String url = API_1_0_USERS + "/user1";

        Response response = getRequestAndReceiveResponse(token, url);

        assertEquals(200, response.statusCode());
        assertEquals("user1", response.getBody().jsonPath().getString("username"));
        assertEquals("test-display", response.getBody().jsonPath().getString("displayName"));
        assertEquals("profile-image.png", response.getBody().jsonPath().getString("image"));
    }

    @Test
    void getUser_WhichDoesNotExist_ReturnNotFound() {
        userService.save(TestUtil.createValidUser("user1"));

        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        String url = API_1_0_USERS + "/user2";

        Response response = getRequestAndReceiveResponse(token, url);

        assertEquals(404, response.statusCode());
    }

    @Test
    void getUser_WithoutToken_ShouldBeSuccessful() {
        userService.save(TestUtil.createValidUser("user1"));

        String url = API_1_0_USERS + "/user1";

        Response response = getRequestAndReceiveResponse(null, url);

        assertEquals(200, response.statusCode());
        assertEquals("user1", response.getBody().jsonPath().getString("username"));
        assertEquals("test-display", response.getBody().jsonPath().getString("displayName"));
        assertEquals("profile-image.png", response.getBody().jsonPath().getString("image"));
    }

    @Test
    void updateUser_displayName_Success() throws JSONException {
        userService.save(TestUtil.createValidUser("user1"));

        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        String url = API_1_0_USERS + "/user1";

        JSONObject updateRequestJson = new JSONObject()
                .put("displayName", "updated-displayName");

        Response response = putRequestAndReceiveResponse(token, updateRequestJson, url);

        assertEquals(200, response.statusCode());
        assertEquals("user1", response.getBody().jsonPath().getString("username"));
        assertEquals("updated-displayName", response.getBody().jsonPath().getString("displayName"));

        assertEquals("updated-displayName", userRepository.findByUsername("user1").getDisplayName());
    }

    @Test
    void updateUser_Jpg_Success() throws JSONException, IOException {
        userService.save(TestUtil.createValidUser("user1"));

        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        String url = API_1_0_USERS + "/user1";

        String encodedImage = readFileToBase64("test_data.jpg");

        JSONObject updateRequestJson = new JSONObject()
                .put("image", encodedImage)
                .put("displayName", "updatedDisplay");

        Response response = putRequestAndReceiveResponse(token, updateRequestJson, url);

        assertEquals(200, response.statusCode());
    }

    @Test
    void updateUser_Png_Success() throws JSONException, IOException {
        userService.save(TestUtil.createValidUser("user1"));

        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        String url = API_1_0_USERS + "/user1";

        String encodedImage = readFileToBase64("test_png.png");

        JSONObject updateRequestJson = new JSONObject()
                .put("image", encodedImage)
                .put("displayName", "updatedDisplay");

        Response response = putRequestAndReceiveResponse(token, updateRequestJson, url);

        assertEquals(200, response.statusCode());
    }

    @Test
    void updateUser_Gif_ShouldReturnError() throws JSONException, IOException {
        userService.save(TestUtil.createValidUser("user1"));

        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        String url = API_1_0_USERS + "/user1";

        String encodedImage = readFileToBase64("butterfly.gif");

        JSONObject updateRequestJson = new JSONObject()
                .put("image", encodedImage)
                .put("displayName", "updatedDisplay");

        Response response = putRequestAndReceiveResponse(token, updateRequestJson, url);

        assertEquals(400, response.statusCode());
        assertEquals("Unsupported file. Only jpeg, png supported.",
                response.getBody().jsonPath().getString("validationErrors.image"));
    }

    @Test
    void updateUser_ToNonExistingUser_ShouldReturnForbidden() throws JSONException {
        userService.save(TestUtil.createValidUser("user1"));

        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        String url = API_1_0_USERS + "/user2";

        JSONObject updateRequestJson = new JSONObject()
                .put("displayName", "updated-displayName");

        Response response = putRequestAndReceiveResponse(token, updateRequestJson, url);

        assertEquals(403, response.statusCode());
    }

    @Test
    void deleteUser_Success() {
        userService.save(TestUtil.createValidUser("user1"));

        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        String url = API_1_0_USERS + "/user1";

        Response response = deleteRequestAndReceiveResponse(token, url);

        assertEquals(200, response.statusCode());
        assertEquals("User is removed.", response.getBody().jsonPath().getString("message"));

        assertNull(userRepository.findByUsername("user1"));
    }

    @Test
    void deleteUser_IncorrectUser_ReturnForbidden() {
        userService.save(TestUtil.createValidUser("user1"));

        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        String url = API_1_0_USERS + "/user2";

        Response response = deleteRequestAndReceiveResponse(token, url);

        assertEquals(403, response.statusCode());
    }
}