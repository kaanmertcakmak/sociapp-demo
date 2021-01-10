package com.sociapp.backend.auth;

import com.sociapp.backend.user.dto.UserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {

    private String token;

    private UserDto userDto;
}
