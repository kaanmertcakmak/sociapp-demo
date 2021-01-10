package com.sociapp.backend.shared;

import com.sociapp.backend.file.FileService;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FileTypeValidator implements ConstraintValidator<FileType, String> {

    @Autowired
    FileService fileService;

    String[] types;

    @Override
    public void initialize(FileType constraintAnnotation) {
        types = constraintAnnotation.types();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s == null || s.isEmpty()) return true;

        String fileType = fileService.detectType(s);

        for(String supportedType : types) {
            if(fileType.contains(supportedType)) return true;
        }

        String supportedTypes = String.join(", ", this.types);

        constraintValidatorContext.disableDefaultConstraintViolation();
        HibernateConstraintValidatorContext hibernateConstraintValidatorContext = constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class);

        hibernateConstraintValidatorContext
                .addMessageParameter("types", supportedTypes);

        hibernateConstraintValidatorContext
                .buildConstraintViolationWithTemplate(constraintValidatorContext.getDefaultConstraintMessageTemplate())
                .addConstraintViolation();

        return false;
    }
}
