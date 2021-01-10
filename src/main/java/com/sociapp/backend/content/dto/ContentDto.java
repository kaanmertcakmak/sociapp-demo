package com.sociapp.backend.content.dto;

import com.sociapp.backend.content.Content;
import com.sociapp.backend.file.dto.FileAttachmentDto;
import com.sociapp.backend.user.dto.UserDto;
import lombok.Data;

@Data
public class ContentDto {

    private Long id;

    private String content;

    private long timestamp;

    private UserDto user;

    private FileAttachmentDto fileAttachment;

    public ContentDto(Content content) {
        this.setId(content.getId());
        this.setContent(content.getContent());
        this.setTimestamp(content.getTimestamp().getTime());
        this.setUser(new UserDto(content.getUser()));
        if(content.getFileAttachment() != null) {
            this.fileAttachment = new FileAttachmentDto(content.getFileAttachment());
        }
    }
}
