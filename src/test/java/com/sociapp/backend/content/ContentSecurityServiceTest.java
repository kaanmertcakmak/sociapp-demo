package com.sociapp.backend.content;

import com.sociapp.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentSecurityServiceTest {

    @Mock
    ContentRepository contentRepository;

    @InjectMocks
    ContentSecurityService contentSecurityService;

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

    @Test
    void isAllowedToDeleteIfContentIsEmtpy() {
        Content content = new Content();
        when(contentRepository.findById(content.getId())).thenReturn(Optional.empty());

        boolean actual = contentSecurityService.isAllowedToDelete(content.getId(), user);

        assertFalse(actual);
    }

    @Test
    void isAllowedToDeleteIfContentIsNotEmptyAndUserIdEquals() {
        Content content = new Content();
        content.setContent("test");
        content.setUser(user);

        when(contentRepository.findById(content.getId())).thenReturn(Optional.of(content));

        boolean actual = contentSecurityService.isAllowedToDelete(content.getId(), user);

        assertEquals(content.getUser().getId(), user.getId());
        assertTrue(actual);
    }

    @Test
    void isAllowedToDeleteIfContentIsNotEmptyAndUserIsIncorrect() {
        User unAllowedUser = new User();
        unAllowedUser.setId(2L);
        unAllowedUser.setUsername("unallowed");
        unAllowedUser.setPassword("test");
        unAllowedUser.setDisplayName("test");

        Content content = new Content();
        content.setContent("test");
        content.setUser(unAllowedUser);

        when(contentRepository.findById(content.getId())).thenReturn(Optional.of(content));

        boolean actual = contentSecurityService.isAllowedToDelete(content.getId(), user);

        assertFalse(actual);
    }
}