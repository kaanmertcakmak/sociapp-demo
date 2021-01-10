package com.sociapp.backend.content;

import com.sociapp.backend.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContentSecurityService {

    @Autowired
    ContentRepository contentRepository;

    public boolean isAllowedToDelete(Long id, User loggedInUser) {
        Optional<Content> optionalContent = contentRepository.findById(id);
        if(optionalContent.isEmpty()) {
            return false;
        }

        Content content = optionalContent.get();
        return content.getUser().getId().equals(loggedInUser.getId());
    }
}
