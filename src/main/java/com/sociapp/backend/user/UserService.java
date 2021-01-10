package com.sociapp.backend.user;

import com.sociapp.backend.errors.NotFoundException;
import com.sociapp.backend.file.FileService;
import com.sociapp.backend.user.dto.UserUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UserService {

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    FileService fileService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, FileService fileService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileService = fileService;
    }

    public void save(User user) {
        String encryptedPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        userRepository.save(user);
    }

    public Page<User> getUsers(Pageable pageable, User user) {
        if(user != null) {
            return userRepository.findByUsernameNot(user.getUsername(), pageable);
        }
        return userRepository.findAll(pageable);
    }

    public User getByUsername(String username) {
        User userInDB = userRepository.findByUsername(username);
        if(userInDB == null) {
            throw new NotFoundException();
        }
        return userInDB;
    }

    public User updateUser(String username, UserUpdateDto updatedUser) {
        User userInDb = getByUsername(username);
        userInDb.setDisplayName(updatedUser.getDisplayName());
        if(updatedUser.getImage() != null) {
            String oldImageName = userInDb.getImage();
            try {
                String storedFileName = fileService.writeBase64EncodedStringToFile(updatedUser.getImage());
                userInDb.setImage(storedFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileService.deleteProfileImage(oldImageName);
        }
        return userRepository.save(userInDb);
    }

    public void deleteUser(String username) {
        User userInDb = userRepository.findByUsername(username);

        fileService.deleteAllStoredFilesForUser(userInDb);

        userRepository.delete(userInDb);
    }
}
