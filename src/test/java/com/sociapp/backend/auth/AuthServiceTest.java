package com.sociapp.backend.auth;

import com.sociapp.backend.user.User;
import com.sociapp.backend.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String USERNAME = "test-user";
    private static final String PASSWORD = "P4ssword";
    private static final String DISPLAY_NAME = "test-display";
    private static final String IMAGE = "profile-image.png";

    @InjectMocks
    AuthService authService;

    @Mock
    UserRepository userRepository;

    @Mock
    TokenRepository tokenRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserDetails userDetails;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setDisplayName(DISPLAY_NAME);
        user.setPassword(PASSWORD);
        user.setImage(IMAGE);
        assertNotNull(user.getId());

        MockitoAnnotations.initMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void authenticate() {
        Credentials credentials = new Credentials();
        credentials.setPassword(PASSWORD);
        credentials.setUsername(USERNAME);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(passwordEncoder.matches(user.getPassword(), credentials.getPassword())).thenReturn(true);
        AuthResponse actual = authService.authenticate(credentials);

        assertEquals(USERNAME, actual.getUserDto().getUsername());
        assertEquals(DISPLAY_NAME, actual.getUserDto().getDisplayName());
        assertEquals(IMAGE, actual.getUserDto().getImage());
    }

    @Test
    void exceptionShouldBeThrownIfUsernameIsIncorrect() {
        Credentials credentials = new Credentials();
        credentials.setPassword(PASSWORD);
        credentials.setUsername("incorrect");

        assertThrows(AuthException.class, () -> authService.authenticate(credentials));
    }

    @Test
    void exceptionShouldBeThrownIfPasswordIsIncorrect() {
        Credentials credentials = new Credentials();
        credentials.setPassword("incorrect password");
        credentials.setUsername(USERNAME);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        assertThrows(AuthException.class, () -> authService.authenticate(credentials));
    }

    @Test
    void getUserDetails() {
        Credentials credentials = new Credentials();
        credentials.setPassword(PASSWORD);
        credentials.setUsername(USERNAME);

        String token = authService.generateRandomToken();

        Token tokenEntity = new Token();
        tokenEntity.setToken(token);
        tokenEntity.setUser(user);
        tokenRepository.save(tokenEntity);

        when(tokenRepository.findById(token)).thenReturn(Optional.of(tokenEntity));

        userDetails = authService.getUserDetails(token);

        assertEquals(USERNAME, userDetails.getUsername());
        assertEquals(PASSWORD, userDetails.getPassword());
    }

    @Test
    void clearToken() {
        String token = authService.generateRandomToken();

        Token tokenEntity = new Token();
        tokenEntity.setToken(token);
        tokenEntity.setUser(user);
        tokenRepository.save(tokenEntity);

        authService.clearToken(token);
        verify(tokenRepository, times(1)).deleteById(token);
    }
}