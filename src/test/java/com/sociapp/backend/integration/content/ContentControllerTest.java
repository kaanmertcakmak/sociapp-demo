package com.sociapp.backend.integration.content;

import com.sociapp.backend.content.Content;
import com.sociapp.backend.content.ContentRepository;
import com.sociapp.backend.content.ContentService;
import com.sociapp.backend.content.dto.ContentSubmitDto;
import com.sociapp.backend.file.FileAttachment;
import com.sociapp.backend.file.FileAttachmentRepository;
import com.sociapp.backend.file.FileService;
import com.sociapp.backend.user.User;
import com.sociapp.backend.user.UserRepository;
import com.sociapp.backend.user.UserService;
import com.sociapp.backend.util.ResetDatabaseTestExecutionListener;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.sociapp.backend.util.TestUtil.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;

@TestExecutionListeners(mergeMode =
        TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
        listeners = {ResetDatabaseTestExecutionListener.class}
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ContentControllerTest {
    private static final String API_1_0_AUTH = "/api/1.0/auth";
    private static final String API_1_0_CONTENT = "/api/1.0/contents";
    @LocalServerPort
    private int ports;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    ContentService contentService;

    @Autowired
    FileAttachmentRepository fileAttachmentRepository;

    @Autowired
    FileService fileService;

    @Autowired
    WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        RestAssured.port = ports;
    }

    @AfterEach
    void tearDown() {
        System.out.println("cleanup");
        userRepository.deleteAll();
        contentRepository.deleteAll();
        fileAttachmentRepository.deleteAll();
    }

    @SneakyThrows
    @Test
    void saveContent_withoutAttachment_success() {
        userService.save(createValidUser("user1"));

        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        JSONObject contentBody = new JSONObject()
                .put("content", "test content");

        Response response = postJsonAndReceiveResponse(contentBody, API_1_0_CONTENT, token);

        assertEquals(201, response.statusCode());
        assertEquals("Content is created successfully", response.getBody().jsonPath().getString("message"));

        Page<Content> contentsOfUser = contentService.getContentsOfUser("user1", PageRequest.of(0, 10, Sort.unsorted()));

        assertThat(contentsOfUser.getTotalElements(), equalTo(1L));
        assertEquals("test content", contentsOfUser.getContent().get(0).getContent());
    }

    @SneakyThrows
    @Test
    void saveContent_withAttachment_success() {
        userService.save(createValidUser("user1"));

        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        MultipartFile file = createFile();

        fileService.saveContentAttachment(file);

        JSONObject contentBody = new JSONObject()
                .put("content", "test content")
                .put("attachmentId", 1L);

        Response response = postJsonAndReceiveResponse(contentBody, API_1_0_CONTENT, token);

        assertEquals(201, response.statusCode());
        assertEquals("Content is created successfully", response.getBody().jsonPath().getString("message"));

        FileAttachment fileInDB = fileAttachmentRepository.findAll().get(0);

        assertEquals(fileInDB.getContent().getContent(), contentBody.getString("content"));

        Content contentInDB = contentRepository.findAll().get(0);

        assertEquals(contentInDB.getContent(), contentBody.getString("content"));
        assertThat(contentInDB.getFileAttachment().getContent().getContent(), equalTo(fileInDB.getContent().getContent()));
    }

    @SneakyThrows
    @Test
    void saveContent_withoutToken_shouldThrowAnError() {
        JSONObject contentBody = new JSONObject()
                .put("content", "test content");

        Response response = postJsonAndReceiveResponse(contentBody, API_1_0_CONTENT, null);

        assertEquals(401, response.statusCode());
        assertEquals("Unauthorized", response.getBody().jsonPath().getString("errorMessage"));
    }

    @SneakyThrows
    @Test
    void saveContent_withEmptyContent_shouldThrowAnError() {
        userService.save(createValidUser("user1"));

        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        JSONObject contentBody = new JSONObject()
                .put("content", "");

        Response response = postJsonAndReceiveResponse(contentBody, API_1_0_CONTENT, token);

        assertEquals(400, response.statusCode());
        assertEquals("size must be between 1 and 1000", response.getBody().jsonPath().getString("validationErrors.content"));
    }

    @SneakyThrows
    @Test
    void saveContent_withMaxSize_shouldThrowAnError() {
        userService.save(createValidUser("user1"));

        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        JSONObject contentBody = new JSONObject()
                .put("content", getAlphaNumericString(1001));

        Response response = postJsonAndReceiveResponse(contentBody, API_1_0_CONTENT, token);

        assertEquals(400, response.statusCode());
        assertEquals("size must be between 1 and 1000", response.getBody().jsonPath().getString("validationErrors.content"));
    }

    @SneakyThrows
    @Test
    void getContents_Success() {
        User user = createValidUser("user1");
        userService.save(user);
        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        MultipartFile file = createFile();

        FileAttachment fileAttachment = fileService.saveContentAttachment(file);

        ContentSubmitDto content = createValidContentWithAttachment(fileAttachment);

        contentService.save(content, user);

        Response response = getRequestAndReceiveResponse(token, API_1_0_CONTENT);

        assertEquals(200, response.statusCode());
        assertEquals(content.getContent(), response.getBody().jsonPath().getList("content.content").get(0));
        assertEquals(user.getUsername(), response.getBody().jsonPath().getList("content.user.username").get(0));
        assertEquals(user.getDisplayName(), response.getBody().jsonPath().getList("content.user.displayName").get(0));
        assertEquals(user.getImage(), response.getBody().jsonPath().getList("content.user.image").get(0));
        assertEquals(fileAttachment.getName(), response.getBody().jsonPath().getList("content.fileAttachment.name").get(0));
        assertEquals(fileAttachment.getFileType(), response.getBody().jsonPath().getList("content.fileAttachment.fileType").get(0));
        assertNotNull(response.getBody().jsonPath().getList("content.timestamp").get(0));
    }

    @SneakyThrows
    @Test
    void getContents_WithPagination_Success() {
        User user = createValidUser("user1");
        userService.save(user);
        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        createContents(10, user);

        Response response = getRequestAndReceiveResponse(token, API_1_0_CONTENT + "?page=0&size=3");

        assertEquals(200, response.statusCode());
        assertEquals(3, response.getBody().jsonPath().getList("content").size());
        assertThat(response.getBody().jsonPath().getList("content.content"), hasItems("content9", "content8", "content7"));
        assertEquals(0, response.getBody().jsonPath().getInt("number"));
    }

    @SneakyThrows
    @Test
    void getContents_WithSortedAsc_Success() {
        User user = createValidUser("user1");
        userService.save(user);
        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        createContents(10, user);

        Response response = getRequestAndReceiveResponse(token, API_1_0_CONTENT + "?page=0&size=5&sort=id&id.dir=asc");

        List<Integer> contentIds = response.getBody().jsonPath().getList("content.id");
        List<Integer> copyContentIds = new java.util.ArrayList<>(List.copyOf(contentIds));
        Collections.sort(copyContentIds);

        assertEquals(contentIds, copyContentIds);
        assertEquals(200, response.statusCode());
        assertEquals(5, response.getBody().jsonPath().getList("content").size());
        assertThat(response.getBody().jsonPath().getList("content.content"), hasItems("content1", "content2", "content3", "content4", "content0"));
    }

    @SneakyThrows
    @Test
    void getOlderContentById_Success_ReturnedContentsIdsShouldBeSmaller() {
        User user = createValidUser("user1");
        userService.save(user);
        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        createContents(3, user);

        Response response = getRequestAndReceiveResponse(token, API_1_0_CONTENT + "/3");

        assertEquals(200, response.statusCode());
        assertEquals(2, response.getBody().jsonPath().getList("content").size());
        assertThat(response.getBody().jsonPath().getList("content.id"), hasItems(1, 2));
    }

    @SneakyThrows
    @Test
    void getNewerContentById_Success_ReturnedContentsIdsShouldBeBigger() {
        User user = createValidUser("user1");
        userService.save(user);
        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        createContents(4, user);

        Response response = getRequestAndReceiveResponse(token, API_1_0_CONTENT + "/1?direction=after");

        assertEquals(200, response.statusCode());
        assertEquals(3, response.getBody().jsonPath().getList("content").size());
        assertThat(response.getBody().jsonPath().getList("id"), hasItems(2, 3, 4));
    }

    @SneakyThrows
    @Test
    void getNewContentsCount_Success_ReturnedContentsIdsShouldBeBigger() {
        User user = createValidUser("user1");
        userService.save(user);
        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        createContents(4, user);

        Response response = getRequestAndReceiveResponse(token, API_1_0_CONTENT + "/1?count=true");

        assertEquals(200, response.statusCode());
        assertEquals(3, response.getBody().jsonPath().getInt("count"));
    }

    @SneakyThrows
    @Test
    void getUsersOlderContentsById_Success_ReturnedContentsIdsShouldBeSmaller() {
        User user = createValidUser("user1");
        User user2 = createValidUser("user2");
        userService.save(user);
        userService.save(user2);
        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        createContents(3, user);
        createContents(4, user2);

        Response response = getRequestAndReceiveResponse(token,"/api/1.0/users/user1/contents/3");

        assertEquals(200, response.statusCode());
        assertEquals(2, response.getBody().jsonPath().getList("content").size());
        assertThat(response.getBody().jsonPath().getList("content.id"), hasItems(1, 2));
        assertThat(response.getBody().jsonPath().getList("content.user.username"), hasItem("user1"));
        assertThat(response.getBody().jsonPath().getList("content.user.username"), not(hasItem("user2")));
    }

    @SneakyThrows
    @Test
    void getUsersOlderContentsById_WithIncorrectId_ReturnsEmptyList() {
        User user = createValidUser("user1");
        userService.save(user);
        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        createContents(3, user);

        Response response = getRequestAndReceiveResponse(token,"/api/1.0/users/user1/contents/1");

        assertEquals(200, response.statusCode());
        assertEquals(0, response.getBody().jsonPath().getList("content").size());
    }

    @SneakyThrows
    @Test
    void getUsersOlderContentsById_WithIncorrectUsername_ReturnsNotFound() {
        User user = createValidUser("user1");
        userService.save(user);
        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        createContents(3, user);

        Response response = getRequestAndReceiveResponse(token,"/api/1.0/users/user2/contents/1");

        assertEquals(404, response.statusCode());
    }

    @SneakyThrows
    @Test
    void getUsersNewerContentsById_Success_ReturnedContentsIdsShouldBeBigger() {
        User user = createValidUser("user1");
        User user2 = createValidUser("user2");
        userService.save(user);
        userService.save(user2);
        String token = authenticateAndGetToken("user2", API_1_0_AUTH);

        createContents(3, user);
        createContents(4, user2);

        Response response = getRequestAndReceiveResponse(token,"/api/1.0/users/user2/contents/5?direction=after");

        assertEquals(200, response.statusCode());
        assertEquals(2, response.getBody().jsonPath().getList("content").size());
        assertThat(response.getBody().jsonPath().getList("id"), hasItems(6, 7));
        assertThat(response.getBody().jsonPath().getList("user.username"), hasItem("user2"));
        assertThat(response.getBody().jsonPath().getList("user.username"), not(hasItem("user1")));
    }

    @SneakyThrows
    @Test
    void getUsersNewerContentsCount_Success() {
        User user = createValidUser("user1");
        User user2 = createValidUser("user2");
        userService.save(user);
        userService.save(user2);
        String token = authenticateAndGetToken("user2", API_1_0_AUTH);

        createContents(4, user2);

        Response response = getRequestAndReceiveResponse(token,"/api/1.0/users/user2/contents/2?count=true");

        assertEquals(200, response.statusCode());
        assertEquals(2, response.getBody().jsonPath().getInt("count"));
    }

    @Test
    void getAllContentsOfUser_Success() {
        User user = createValidUser("user1");
        User user2 = createValidUser("user2");
        userService.save(user);
        userService.save(user2);
        String token = authenticateAndGetToken("user2", API_1_0_AUTH);

        createContents(3, user);
        createContents(4, user2);

        Response response = getRequestAndReceiveResponse(token,"/api/1.0/users/user2/contents");

        assertEquals(200, response.statusCode());
        assertEquals(4, response.getBody().jsonPath().getList("content").size());
        assertThat(response.getBody().jsonPath().getList("content.user.username"), hasItem("user2"));
    }

    @Test
    void getAllContentsOfUser_WithPagination_Success() {
        User user = createValidUser("user1");
        User user2 = createValidUser("user2");
        userService.save(user);
        userService.save(user2);
        String token = authenticateAndGetToken("user2", API_1_0_AUTH);

        createContents(3, user);
        createContents(7, user2);

        Response response = getRequestAndReceiveResponse(token,"/api/1.0/users/user2/contents?page=1&size=3");

        assertEquals(200, response.statusCode());
        assertEquals(3, response.getBody().jsonPath().getList("content").size());
        assertThat(response.getBody().jsonPath().getList("content.user.username"), hasItem("user2"));
        assertThat(response.getBody().jsonPath().getList("content.content"), hasItems("content3", "content2", "content1"));
    }

    @Test
    void getAllContentsOfUser_WithPaginationAndSort_Success() {
        User user = createValidUser("user1");
        userService.save(user);
        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        createContents(3, user);

        Response response = getRequestAndReceiveResponse(token,"/api/1.0/users/user1/contents?sort=id&id.dir=asc");

        assertEquals(200, response.statusCode());
        List<Integer> contentIds = response.getBody().jsonPath().getList("content.id");
        List<Integer> copyContentIds = new java.util.ArrayList<>(List.copyOf(contentIds));
        Collections.sort(copyContentIds);

        assertEquals(contentIds, copyContentIds);
    }

    @Test
    void getAllContentsOfUser_WithIncorrectUsername_ReturnsNotFound() {
        User user = createValidUser("user1");
        userService.save(user);
        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        createContents(3, user);

        Response response = getRequestAndReceiveResponse(token,"/api/1.0/users/user2/contents?sort=id&id.dir=asc");

        assertEquals(404, response.statusCode());
    }

    @Test
    void deleteContent_Success() {
        User user = createValidUser("user1");
        userService.save(user);
        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        createContents(3, user);

        Response response = deleteRequestAndReceiveResponse(token,API_1_0_CONTENT + "/1");

        assertEquals(200, response.statusCode());
        assertEquals("Content is removed", response.getBody().jsonPath().getString("message"));

        response = getRequestAndReceiveResponse(token, API_1_0_CONTENT);
        assertEquals(2, response.getBody().jsonPath().getList("content").size());
        assertThat(response.getBody().jsonPath().getList("content.content"), not(hasItem("content0")));
    }

    @SneakyThrows
    @Test
    void deleteContent_withAttachment_Success_shouldAlsoDeleteAttachment() {
        User user = createValidUser("user1");
        userService.save(user);
        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        MultipartFile file = createFile();

        FileAttachment fileAttachment = fileService.saveContentAttachment(file);

        ContentSubmitDto content = createValidContentWithAttachment(fileAttachment);

        contentService.save(content, user);

        Response response = deleteRequestAndReceiveResponse(token,API_1_0_CONTENT + "/1");

        assertEquals(200, response.statusCode());
        assertEquals("Content is removed", response.getBody().jsonPath().getString("message"));

        response = getRequestAndReceiveResponse(null, API_1_0_CONTENT);
        assertEquals(0, response.getBody().jsonPath().getList("content").size());

        Optional<FileAttachment> fileAttachmentInDB = fileAttachmentRepository.findById(1L);
        assertFalse(fileAttachmentInDB.isPresent());
    }

    @Test
    void deleteContent_WithIncorrectId_ReturnsForbidden() {
        User user = createValidUser("user1");
        userService.save(user);
        String token = authenticateAndGetToken("user1", API_1_0_AUTH);

        createContents(3, user);

        Response response = deleteRequestAndReceiveResponse(token,API_1_0_CONTENT + "/5");

        assertEquals(403, response.statusCode());
    }

    void createContents(int contentCount, User user) {
        for(int i = 0; i< contentCount; i++) {
            contentService.save(createValidContentWithoutAttachment("content" + i), user);
        }
    }
}
