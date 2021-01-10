package com.sociapp.backend.integration.file;

import com.sociapp.backend.file.FileAttachment;
import com.sociapp.backend.file.FileAttachmentRepository;
import com.sociapp.backend.user.User;
import com.sociapp.backend.user.UserService;
import com.sociapp.backend.util.ResetDatabaseTestExecutionListener;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;

import java.io.File;
import java.util.Optional;

import static com.sociapp.backend.util.TestUtil.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@TestExecutionListeners(mergeMode =
        TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
        listeners = {ResetDatabaseTestExecutionListener.class}
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FileControllerTest {

    @LocalServerPort
    private int ports;

    private static final String API_1_0_CONTENT_ATTACHMENTS = "/api/1.0/content-attachments";
    private static final String API_1_0_AUTH = "/api/1.0/auth";

    @Autowired
    UserService userService;

    @Autowired
    FileAttachmentRepository fileAttachmentRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = ports;
    }

    @AfterEach
    void tearDown() {
        fileAttachmentRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    void save_ContentAttachment_Jpg_Successful() {

        User user = createValidUser("user1");
        userService.save(user);
        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        Response response = postWithFile("test_data.jpg", token, API_1_0_CONTENT_ATTACHMENTS);

        assertEquals(200, response.statusCode());
        assertEquals("image/jpeg", response.getBody().jsonPath().getString("fileType"));
        assertNotNull(response.getBody().jsonPath().getString("name"));
        assertNotNull(response.getBody().jsonPath().getString("date"));
        assertNull(response.getBody().jsonPath().getString("content"));

        Optional<FileAttachment> fileInDb = fileAttachmentRepository.findById(1L);

        if(fileInDb.isPresent()) {
            assertEquals(fileInDb.get().getName(), response.getBody().jsonPath().getString("name"));
            assertEquals(fileInDb.get().getFileType(), response.getBody().jsonPath().getString("fileType"));
        }
    }

    @SneakyThrows
    @Test
    void save_ContentAttachment_Png_Successful() {

        User user = createValidUser("user1");
        userService.save(user);
        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        Response response = postWithFile("test_png.png", token, API_1_0_CONTENT_ATTACHMENTS);

        assertEquals(200, response.statusCode());
        assertEquals("image/png", response.getBody().jsonPath().getString("fileType"));
        assertNotNull(response.getBody().jsonPath().getString("name"));
        assertNotNull(response.getBody().jsonPath().getString("date"));
        assertNull(response.getBody().jsonPath().getString("content"));

        Optional<FileAttachment> fileInDb = fileAttachmentRepository.findById(1L);

        if(fileInDb.isPresent()) {
            assertEquals(fileInDb.get().getName(), response.getBody().jsonPath().getString("name"));
            assertEquals(fileInDb.get().getFileType(), response.getBody().jsonPath().getString("fileType"));
        }
    }

    @SneakyThrows
    @Test
    void save_ContentAttachment_Gif_Successful() {

        User user = createValidUser("user1");
        userService.save(user);
        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        Response response = postWithFile("butterfly.gif", token, API_1_0_CONTENT_ATTACHMENTS);

        assertEquals(200, response.statusCode());
        assertEquals("image/gif", response.getBody().jsonPath().getString("fileType"));
        assertNotNull(response.getBody().jsonPath().getString("name"));
        assertNotNull(response.getBody().jsonPath().getString("date"));
        assertNull(response.getBody().jsonPath().getString("content"));

        Optional<FileAttachment> fileInDb = fileAttachmentRepository.findById(1L);

        if(fileInDb.isPresent()) {
            assertEquals(fileInDb.get().getName(), response.getBody().jsonPath().getString("name"));
            assertEquals(fileInDb.get().getFileType(), response.getBody().jsonPath().getString("fileType"));
        }
    }

    @SneakyThrows
    @Test
    void save_ContentAttachment_EmptyContent_ShouldThrowAnError() {

        User user = createValidUser("user1");
        userService.save(user);
        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        Response response = postAndReceiveResponse(token, API_1_0_CONTENT_ATTACHMENTS);

        assertEquals(404, response.statusCode());
    }

    Response postWithFile(String file, String token, String url) {
        return given()
                .multiPart("file", new File("src/test/resources/" + file))
                .header("Authorization", "Bearer " + token)
                .when().log().all()
                .post(url)
                .then().log().all()
                .extract().response();
    }
}
