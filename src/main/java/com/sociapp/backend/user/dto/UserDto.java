package com.sociapp.backend.user.dto;

import com.sociapp.backend.user.User;
import lombok.Data;

@Data
public class UserDto {

    String username;

    String displayName;

    String image;

    public UserDto(User user) {
        this.setUsername(user.getUsername());
        this.setDisplayName(user.getDisplayName());
        this.setImage(user.getImage());
    }
}
