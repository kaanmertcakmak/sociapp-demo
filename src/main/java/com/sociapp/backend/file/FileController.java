package com.sociapp.backend.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {

    @Autowired
    FileService fileService;

    @PostMapping("/api/1.0/content-attachments")
    FileAttachment saveContentAttachment(MultipartFile file) {
        return fileService.saveContentAttachment(file);
    }
}
