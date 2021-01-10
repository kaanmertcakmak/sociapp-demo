package com.sociapp.backend.user;

import com.sociapp.backend.shared.CurrentUser;
import com.sociapp.backend.shared.GenericResponse;
import com.sociapp.backend.user.dto.UserDto;
import com.sociapp.backend.user.dto.UserUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/1.0")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public GenericResponse createUser(@Valid @RequestBody User user) {
        userService.save(user);
        return new GenericResponse("User is created successfully");
    }

    @GetMapping("/users")
    Page<UserDto> getUsers(Pageable pageable, @CurrentUser User user) {
        return userService.getUsers(pageable, user).map(UserDto::new);
    }

    @GetMapping("/users/{username}")
    UserDto getUser(@PathVariable String username) {
        User user = userService.getByUsername(username);
        return new UserDto(user);
    }

    @PutMapping("/users/{username}")
    @PreAuthorize("#username == principal.username")
    UserDto updateUser(@Valid @RequestBody UserUpdateDto updatedUser, @PathVariable String username) {
        User user = userService.updateUser(username, updatedUser);
        return new UserDto(user);
    }

    @DeleteMapping("/users/{username}")
    @PreAuthorize("#username == principal.username")
    GenericResponse deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return new GenericResponse("User is removed.");
    }
}
