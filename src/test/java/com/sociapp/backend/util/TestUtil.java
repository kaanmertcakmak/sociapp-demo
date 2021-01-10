package com.sociapp.backend.util;

import com.sociapp.backend.content.Content;
import com.sociapp.backend.content.dto.ContentSubmitDto;
import com.sociapp.backend.file.FileAttachment;
import com.sociapp.backend.file.FileService;
import com.sociapp.backend.user.User;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;

public class TestUtil {

    public static User createValidUser() {
        User user = new User();
        user.setUsername("test-user");
        user.setDisplayName("test-display");
        user.setPassword("P4ssword");
        user.setImage("profile-image.png");
        return user;
    }

    public static User createValidUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setDisplayName("test-display");
        user.setPassword("P4ssword");
        user.setImage("profile-image.png");
        return user;
    }

    public static Response postJsonAndReceiveResponse(JSONObject jsonObject, String url) {
        return given()
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .when()
                .post(url)
                .then().log().all()
                .extract().response();
    }

    public static Response postJsonAndReceiveResponse(JSONObject jsonObject, String url, String token) {
        return given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .when().log().all()
                .post(url)
                .then().log().all()
                .extract().response();
    }

    public static Response postAndReceiveResponse(String url, String token) {
        return given()
                .header("Authorization", "Bearer " + token)
                .when().log().all()
                .post(url)
                .then().log().all()
                .extract().response();
    }

    public static Response putRequestAndReceiveResponse(String token, JSONObject jsonObject, String url) {
        RequestSpecification requestSpecification = token != null ? given()
                .header("Authorization", "Bearer " + token) : given();

        return requestSpecification
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .when().log().all()
                .put(url)
                .then().log().all()
                .extract().response();
    }

    public static String readFileToBase64(String fileName) throws IOException {
        ClassPathResource imageResource = new ClassPathResource(fileName);
        byte[] imageArr = FileUtils.readFileToByteArray(imageResource.getFile());
        return Base64.getEncoder().encodeToString(imageArr);
    }

    public static MultipartFile createFile() throws IOException {
        ClassPathResource imageResource = new ClassPathResource("test_data.jpg");
        byte[] fileAsByte = FileUtils.readFileToByteArray(imageResource.getFile());

        return new MockMultipartFile("test_data.jpg", fileAsByte);
    }




    public static Response getRequestAndReceiveResponse(String token, String url) {
        RequestSpecification requestSpecification = token != null ? given()
                .header("Authorization", "Bearer " + token) : given();

        return requestSpecification
                .when().log().all()
                .get(url)
                .then().log().all()
                .extract().response();
    }

    public static Response deleteRequestAndReceiveResponse(String token, String url) {
        RequestSpecification requestSpecification = token != null ? given()
                .header("Authorization", "Bearer " + token) : given();

        return requestSpecification
                .when().log().all()
                .delete(url)
                .then().log().all()
                .extract().response();
    }

    public static String getAlphaNumericString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

    @SneakyThrows
    public static ContentSubmitDto createValidContentWithAttachment(FileAttachment fileAttachment) {
        ContentSubmitDto contentSubmitDto = new ContentSubmitDto();
        contentSubmitDto.setAttachmentId(fileAttachment.getId());
        contentSubmitDto.setContent("test-content");

        return contentSubmitDto;
    }

    @SneakyThrows
    public static ContentSubmitDto createValidContentWithoutAttachment(String contentName) {
        ContentSubmitDto contentSubmitDto = new ContentSubmitDto();
        contentSubmitDto.setContent(contentName);

        return contentSubmitDto;
    }

    @SneakyThrows
    public static String authenticateAndGetToken(String username, String url) {
        JSONObject jsonObject = new JSONObject()
                .put("username", username)
                .put("password", "P4ssword");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(jsonObject.toString())
                .when()
                .post(url)
                .then().log().all()
                .extract().response();

        return response.getBody().jsonPath().getString("token");
    }

}
