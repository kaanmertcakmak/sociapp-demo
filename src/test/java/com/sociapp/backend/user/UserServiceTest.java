package com.sociapp.backend.user;

import com.sociapp.backend.file.FileService;
import com.sociapp.backend.user.dto.UserUpdateDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    FileService fileService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("test-user");
        user.setDisplayName("test-display");
        user.setPassword("P4ssword");
        user.setImage("profile-image.png");
        assertNotNull(user.getId());
        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void save() {
        userService.save(user);
        verify(userRepository, times(1)).save(user);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        assertEquals(userRepository.findByUsername(user.getUsername()).getUsername(), user.getUsername());
        assertEquals(userRepository.findByUsername(user.getUsername()).getId(), user.getId());
        assertEquals(userRepository.findByUsername(user.getUsername()).getDisplayName(), user.getDisplayName());
        assertEquals(userRepository.findByUsername(user.getUsername()).getImage(), user.getImage());
    }

    @Test
    void createUserWhenUsernameIsNull() {
        user.setUsername(null);
        userService.save(user);
        verify(userRepository, times(1)).save(user);
        assertNull(userRepository.getOne(1L));
    }

    @Test
    void getUsers() {
        Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
        Page<User> users = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.findByUsernameNot(user.getUsername(), pageable)).thenReturn(users);

        Page<User> actual = userService.getUsers(pageable, user);
        assertEquals(users, actual);
    }

    @Test
    void shouldBeAbleToGetExistingUserWithCorrectUsername() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        User actual = userService.getByUsername(user.getUsername());
        assertEquals(user, actual);
    }

    @Test
    void findUserWithNonExistingUsername() {
        String nonExistingUsername = "abc";
        when(userRepository.findByUsername(nonExistingUsername)).thenReturn(user);

        User actual = userService.getByUsername(nonExistingUsername);
        assertEquals(user, actual);
    }

    @Test
    void updateUserDisplayName() {
        String newDisplayName = "displayName Updated";

        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setDisplayName(newDisplayName);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        userService.updateUser(user.getUsername(), userUpdateDto);

        verify(userRepository, times(1)).save(user);

        assertEquals(newDisplayName, user.getDisplayName());
    }

    @Test
    void deleteUser() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        userService.deleteUser(user.getUsername());

        verify(userRepository, times(1)).delete(user);
    }
}