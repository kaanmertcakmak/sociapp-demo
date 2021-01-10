package com.sociapp.backend.user.dto;

import com.sociapp.backend.shared.FileType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserUpdateDto {

    @NotNull(message = "{sociapp.constraint.displayname.NotNull.message}")
    @Size(min = 4, max = 50)
    private String displayName;

    @FileType(types = {"jpeg", "png"})
    private String image;

}
