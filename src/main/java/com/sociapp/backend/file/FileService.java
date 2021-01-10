package com.sociapp.backend.file;

import com.sociapp.backend.configuration.AppConfiguration;
import com.sociapp.backend.user.User;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@EnableScheduling
public class FileService {

    @Autowired
    AppConfiguration appConfiguration;

    Tika tika;

    FileAttachmentRepository fileAttachmentRepository;

    public FileService(FileAttachmentRepository fileAttachmentRepository) {
        this.tika = new Tika();
        this.fileAttachmentRepository = fileAttachmentRepository;
    }

    public String writeBase64EncodedStringToFile(String image) throws IOException {
        String fileName = generateRandomName();
        File target = new File(appConfiguration.getProfileStoragePath() + "/" + fileName);
        OutputStream outputStream = new FileOutputStream(target);

        byte[] base64Encoded = Base64.getDecoder().decode(image);

        outputStream.write(base64Encoded);
        outputStream.close();
        return fileName;
    }

    public String generateRandomName() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public void deleteProfileImage(String oldImageName) {
        if(oldImageName != null) {
            deleteFile(Paths.get(appConfiguration.getProfileStoragePath(), oldImageName));
        }
    }

    public String detectType(String base64) {
        byte[] base64Encoded = Base64.getDecoder().decode(base64);
        return detectType(base64Encoded);
    }

    public String detectType(byte[] arr) {
        return tika.detect(arr);
    }

    public FileAttachment saveContentAttachment(MultipartFile multipartFile) {
        String fileName = generateRandomName();
        File target = new File(appConfiguration.getAttachmentStoragePath() + "/" + fileName);
        String fileType = null;
        try {
            byte[] arr = multipartFile.getBytes();
            OutputStream outputStream = new FileOutputStream(target);
            outputStream.write(arr);
            outputStream.close();
            fileType = detectType(arr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setName(fileName);
        fileAttachment.setDate(new Date());
        fileAttachment.setFileType(fileType);

        return fileAttachmentRepository.save(fileAttachment);
    }

    @Scheduled(fixedRate = (24 * 60 * 60 * 1000))
    public void cleanupStorage() {
        Date twentyFourHoursAgo = new Date(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
        List<FileAttachment> filesToBeDeleted = fileAttachmentRepository.findByDateBeforeAndContentIsNull(twentyFourHoursAgo);

        filesToBeDeleted.forEach(fileAttachment -> {
            deleteAttachmentFile(fileAttachment.getName());
            fileAttachmentRepository.deleteById(fileAttachment.getId());
        });
    }

    public void deleteAttachmentFile(String name) {
        if(name != null) {
            deleteFile(Paths.get(appConfiguration.getAttachmentStoragePath(), name));
        }
    }

    private void deleteFile(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllStoredFilesForUser(User userInDb) {
        deleteProfileImage(userInDb.getImage());
        List<FileAttachment> filesOfUserToBeRemoved = fileAttachmentRepository.findByContentUser(userInDb);
        filesOfUserToBeRemoved.forEach(fileofUser -> deleteAttachmentFile(fileofUser.getName()));
    }
}
