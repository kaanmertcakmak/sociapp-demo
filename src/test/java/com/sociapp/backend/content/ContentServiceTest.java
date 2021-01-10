package com.sociapp.backend.content;

import com.sociapp.backend.content.dto.ContentSubmitDto;
import com.sociapp.backend.file.FileAttachment;
import com.sociapp.backend.file.FileAttachmentRepository;
import com.sociapp.backend.file.FileService;
import com.sociapp.backend.user.User;
import com.sociapp.backend.user.UserRepository;
import com.sociapp.backend.user.UserService;
import com.sociapp.backend.util.ResetDatabaseTestExecutionListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestExecutionListeners;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @Mock
    ContentRepository contentRepository;

    @Mock
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    FileAttachmentRepository fileAttachmentRepository;

    @Mock
    FileService fileService;

    @Mock
    Specification<Content> contentSpecification;

    @InjectMocks
    ContentService contentService;

    private User user;
    private Content content;

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
        fileAttachmentRepository.deleteAll();
        contentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void saveWithoutAttachment() {
        content = new Content();
        ContentSubmitDto contentSubmitDto = new ContentSubmitDto();
        contentSubmitDto.setContent("test");
        content.setContent(contentSubmitDto.getContent());
        content.setTimestamp(new Date());
        content.setUser(user);

        contentService.save(contentSubmitDto, user);
        verify(contentRepository, times(1)).save(content);
    }

    FileAttachment createFileAttachment() {
        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setFileType("png");
        fileAttachment.setName("testfile");
        fileAttachment.setId(1L);
        return  fileAttachment;
    }

    @Test
    void saveWithAttachment() {
        content = new Content();
        content.setContent("test");
        content.setUser(user);
        contentRepository.save(content);

        FileAttachment fileAttachment = createFileAttachment();
        fileAttachment.setContent(content);

        ContentSubmitDto contentSubmitDto = new ContentSubmitDto();
        contentSubmitDto.setContent("test");
        contentSubmitDto.setAttachmentId(fileAttachment.getId());

        when(fileAttachmentRepository.findById(contentSubmitDto.getAttachmentId())).thenReturn(Optional.of(fileAttachment));
        contentService.save(contentSubmitDto, user);
        verify(fileAttachmentRepository, times(1)).save(fileAttachment);
    }

    @Test
    void getContents() {
        Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
        Page<Content> contents = new PageImpl<>(Collections.singletonList(content));

        when(contentRepository.findAll(pageable)).thenReturn(contents);

        Page<Content> actual = contentService.getContents(pageable);

        assertEquals(contents, actual);
    }

    @Test
    void getContentsOfUser() {
        Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
        Page<Content> contents = new PageImpl<>(Collections.singletonList(content));

        when(userService.getByUsername(user.getUsername())).thenReturn(user);
        when(contentRepository.findByUser(user, pageable)).thenReturn(contents);
        Page<Content> actual = contentService.getContentsOfUser(user.getUsername(), pageable);

        assertEquals(contents, actual);
        verify(userService, times(1)).getByUsername(user.getUsername());
        verify(contentRepository, times(1)).findByUser(user, pageable);
    }

    @Test
    void getOldContents() {
        content = new Content();
        content.setContent("test");
        content.setUser(user);

        Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
        Page<Content> contents = new PageImpl<>(Collections.singletonList(content));

        when(userService.getByUsername(user.getUsername())).thenReturn(user);
        when(contentRepository.findAll((Specification<Content>) any(Object.class), eq(pageable))).thenReturn(contents);

        Page<Content> actual = contentService.getOldContents(content.getId(), user.getUsername(), pageable);

        assertEquals(contents, actual);
    }

    @Test
    void getNewContentsCount() {
        content = new Content();
        content.setContent("test");
        content.setUser(user);

        Pageable pageable = PageRequest.of(0, 10, Sort.unsorted());
        Page<Content> contents = new PageImpl<>(Collections.singletonList(content));

        when(userService.getByUsername(user.getUsername())).thenReturn(user);
        when(contentRepository.count((Specification<Content>) any(Object.class))).thenReturn((long) contents.getSize());

        Long actualCount = contentService.getNewContentsCount(content.getId(), user.getUsername());

        assertEquals(contents.getSize(), actualCount);
    }

    @Test
    void getNewContents() {
        content = new Content();
        content.setContent("test");
        content.setUser(user);


        List<Content> contents = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, String.valueOf(content));

        when(userService.getByUsername(user.getUsername())).thenReturn(user);
        when(contentRepository.findAll((Specification<Content>) any(Object.class), eq(sort))).thenReturn(contents);

        List<Content> actual = contentService.getNewContents(content.getId(), user.getUsername(), sort);

        assertEquals(contents, actual);
    }

    @Test
    void delete() {
        content = new Content();
        content.setContent("test");
        content.setUser(user);
        when(contentRepository.getOne(content.getId())).thenReturn(content);
        contentService.delete(content.getId());
        verify(contentRepository, times(1)).deleteById(content.getId());
    }

    @Test
    void deleteWithFileAttachment() {
        content = new Content();
        content.setContent("test");
        content.setUser(user);
        FileAttachment fileAttachment = createFileAttachment();
        fileAttachment.setContent(content);
        content.setFileAttachment(fileAttachment);

        when(contentRepository.getOne(content.getId())).thenReturn(content);
        contentService.delete(content.getId());
        verify(contentRepository, times(1)).deleteById(content.getId());
        verify(fileService, times(1)).deleteAttachmentFile(fileAttachment.getName());
    }
}