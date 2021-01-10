package com.sociapp.backend.content;

import com.sociapp.backend.content.dto.ContentDto;
import com.sociapp.backend.content.dto.ContentSubmitDto;
import com.sociapp.backend.shared.CurrentUser;
import com.sociapp.backend.shared.GenericResponse;
import com.sociapp.backend.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/1.0")
public class ContentController {

    @Autowired
    ContentService contentService;

    @PostMapping("/contents")
    @ResponseStatus(HttpStatus.CREATED)
    public GenericResponse saveContent(@Valid @RequestBody ContentSubmitDto content, @CurrentUser User user) {
        contentService.save(content, user);
        return new GenericResponse("Content is created successfully");
    }

    @GetMapping("/contents")
    Page<ContentDto> getContents(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return contentService.getContents(pageable).map(ContentDto::new);
    }

    @GetMapping({"/contents/{id:[0-9]+}", "/users/{username}/contents/{id:[0-9]+}"})
    ResponseEntity<?> getContentsRelative(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                          @PathVariable Long id,
                                          @PathVariable(required = false) String username,
                                          @RequestParam(name = "count", required = false, defaultValue = "false") boolean count,
                                          @RequestParam(name = "direction", defaultValue = "before") String direction) {
        if(count) {
            Long newContentCount = contentService.getNewContentsCount(id, username);
            Map<String, Long> response = new HashMap<>();
            response.put("count", newContentCount);
            return ResponseEntity.ok(response);
        }

        if(direction.equals("after")) {
            List<Content> newContents = contentService.getNewContents(id, username, pageable.getSort());
            List<ContentDto> newContentsDto = newContents.stream().map(ContentDto::new).collect(Collectors.toList());

            return ResponseEntity.ok(newContentsDto);
        }
        return ResponseEntity.ok(contentService.getOldContents(id, username, pageable).map(ContentDto::new));
    }

    @GetMapping("/users/{username}/contents")
    Page<ContentDto> getUsersContents(@PathVariable String username,
                                      @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return contentService.getContentsOfUser(username, pageable).map(ContentDto::new);
    }

    @DeleteMapping("/contents/{id:[0-9]+}")
    @PreAuthorize("@contentSecurityService.isAllowedToDelete(#id, principal)")
    GenericResponse deleteContent(@PathVariable Long id) {
        contentService.delete(id);
        return new GenericResponse("Content is removed");
    }
}
