package com.sociapp.backend.file;

import com.sociapp.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long> {

    List<FileAttachment> findByDateBeforeAndContentIsNull(Date date);

    List<FileAttachment> findByContentUser(User user);
}
