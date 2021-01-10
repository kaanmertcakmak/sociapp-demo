package com.sociapp.backend.file.dto;

import com.sociapp.backend.file.FileAttachment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileAttachmentDto {

    private String name;

    private String fileType;

    public FileAttachmentDto(FileAttachment fileAttachment) {
        this.setName(fileAttachment.getName());
        this.fileType = fileAttachment.getFileType();
    }
}
