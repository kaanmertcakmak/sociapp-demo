package com.sociapp.backend.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiException {

    private int statusCode;

    private String errorMessage;

    private String path;

    private final Long timestamp = new Date().getTime();

    private Map<String, String> validationErrors;

    public ApiException(int statusCode, String errorMessage, String path) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
        this.path = path;
    }
}
