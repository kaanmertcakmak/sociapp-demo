package com.sociapp.backend.file;

import com.sociapp.backend.configuration.AppConfiguration;
import com.sociapp.backend.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    AppConfiguration appConfiguration;

    @Mock
    FileAttachmentRepository fileAttachmentRepository;

    @InjectMocks
    FileService fileService;

    private FileAttachment fileAttachment;

    @BeforeEach
    void setUp() {
        fileAttachment = new FileAttachment();
        fileAttachment.setName("test");
        fileAttachment.setDate(new Date());
        fileAttachment.setFileType("png");
        fileAttachment.setId(1L);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void saveContentAttachment() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("test", "test".getBytes());
        fileAttachment.setFileType(fileService.detectType(multipartFile.getBytes()));
        when(appConfiguration.getAttachmentStoragePath()).thenReturn("./storage-dev/attachments");
        when(fileAttachmentRepository.save(any(FileAttachment.class))).thenReturn(fileAttachment);

        FileAttachment actual = fileService.saveContentAttachment(multipartFile);
        verify(fileAttachmentRepository, times(1)).save(any(FileAttachment.class));
        assertEquals(fileAttachment, actual);
    }

    @Test
    void cleanupStorage() {
        List<FileAttachment> fileAttachments = Collections.singletonList(fileAttachment);

        when(fileAttachmentRepository.findByDateBeforeAndContentIsNull(any(Date.class))).thenReturn(fileAttachments);

        fileService.cleanupStorage();
        verify(fileAttachmentRepository, times(1)).deleteById(fileAttachment.getId());
        assertEquals(Optional.empty(), fileAttachmentRepository.findById(fileAttachment.getId()));
    }

    @Test
    void deleteAllStoredFilesForUser() {
        User user = new User();
        user.setUsername("test");
        user.setDisplayName("test");
        user.setPassword("test");

        List<FileAttachment> fileAttachments = Collections.singletonList(fileAttachment);

        when(fileAttachmentRepository.findByContentUser(user)).thenReturn(fileAttachments);

        fileService.deleteAllStoredFilesForUser(user);

        assertFalse(fileAttachmentRepository.findById(1L).isPresent());
    }

    @AfterEach
    void cleanUp() {
        File filePath = new File("./storage-dev/attachments");
        String[] files = filePath.list();

        assert files != null;
        for(String file : files) {
            File currentFile = new File(filePath.getPath(), file);
            currentFile.delete();
        }
    }
}