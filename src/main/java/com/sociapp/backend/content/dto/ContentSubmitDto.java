package com.sociapp.backend.content.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class ContentSubmitDto {

    @Size(min = 1, max = 1000)
    private String content;

    private Long attachmentId;

}
