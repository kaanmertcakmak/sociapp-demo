package com.sociapp.backend.errors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ErrorHandler implements ErrorController {

    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping("/error")
    ApiException handleError(WebRequest webRequest) {
        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(webRequest,
                ErrorAttributeOptions
                        .of(
                                ErrorAttributeOptions.Include.MESSAGE,
                                ErrorAttributeOptions.Include.BINDING_ERRORS));
        String message = (String) attributes.get("message");
        String path = (String) attributes.get("path");
        int status = (int) attributes.get("status");
        ApiException exception = new ApiException(status, message, path);
        if(attributes.containsKey("errors")) {
            @SuppressWarnings("unchecked") List<FieldError> fieldErrorList = (List<FieldError>) attributes.get("errors");
            Map<String, String> validationErrors = new HashMap<>();
            fieldErrorList
                    .forEach(
                            (fieldError -> validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage()))
                    );
            exception.setValidationErrors(validationErrors);
        }
        return exception;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
