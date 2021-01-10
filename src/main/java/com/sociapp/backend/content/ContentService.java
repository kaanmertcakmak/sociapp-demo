package com.sociapp.backend.content;

import com.sociapp.backend.content.dto.ContentSubmitDto;
import com.sociapp.backend.file.FileAttachment;
import com.sociapp.backend.file.FileAttachmentRepository;
import com.sociapp.backend.file.FileService;
import com.sociapp.backend.user.User;
import com.sociapp.backend.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ContentService {

    ContentRepository contentRepository;

    UserService userService;

    FileAttachmentRepository fileAttachmentRepository;

    FileService fileService;

    public void save(ContentSubmitDto contentSubmitDto, User user) {
        Content content = new Content();
        content.setContent(contentSubmitDto.getContent());
        content.setTimestamp(new Date());
        content.setUser(user);
        contentRepository.save(content);
        if(contentSubmitDto.getAttachmentId() != null) {
            Optional<FileAttachment> optionalFileAttachment = fileAttachmentRepository.findById(contentSubmitDto.getAttachmentId());
            if(optionalFileAttachment.isPresent()) {
                FileAttachment fileAttachment = optionalFileAttachment.get();
                fileAttachment.setContent(content);
                fileAttachmentRepository.save(fileAttachment);
            }
        }
    }

    public Page<Content> getContents(Pageable pageable) {
        return contentRepository.findAll(pageable);
    }

    public Page<Content> getContentsOfUser(String username, Pageable pageable) {
        User userInDB = userService.getByUsername(username);
        return contentRepository.findByUser(userInDB, pageable);
    }

    public Page<Content> getOldContents(Long id, String username, Pageable pageable) {
        Specification<Content> contentSpecification = idLessThan(id);

        if(username != null) {
            User userInDB = userService.getByUsername(username);
            contentSpecification = contentSpecification.and(userIs(userInDB));
        }

        return contentRepository.findAll(contentSpecification, pageable);
    }

    public Long getNewContentsCount(Long id, String username) {
        Specification<Content> contentSpecification = idGreaterThan(id);

        if(username != null) {
            User userInDB = userService.getByUsername(username);
            contentSpecification = contentSpecification.and(userIs(userInDB));
        }

        return contentRepository.count(contentSpecification);
    }

    public List<Content> getNewContents(Long id, String username, Sort sort) {
        Specification<Content> contentSpecification = idGreaterThan(id);

        if(username != null) {
            User userInDB = userService.getByUsername(username);
            contentSpecification = contentSpecification.and(userIs(userInDB));
        }

        return contentRepository.findAll(contentSpecification, sort);
    }

    public Specification<Content> idLessThan(Long id) {
        return (Specification<Content>) (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("id"), id);
    }

    public Specification<Content> idGreaterThan(Long id) {
        return (Specification<Content>) (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("id"), id);
    }

    public Specification<Content> userIs(User user) {
        return (Specification<Content>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
    }

    public void delete(Long id) {
        Content contentInDb = contentRepository.getOne(id);
        if(contentInDb.getFileAttachment() != null) {
            String fileName = contentInDb.getFileAttachment().getName();
            fileService.deleteAttachmentFile(fileName);
        }
        contentRepository.deleteById(id);
    }
}
