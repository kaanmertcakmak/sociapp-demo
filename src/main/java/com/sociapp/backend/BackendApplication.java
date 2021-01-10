package com.sociapp.backend;

import com.sociapp.backend.content.ContentService;
import com.sociapp.backend.content.dto.ContentSubmitDto;
import com.sociapp.backend.user.User;
import com.sociapp.backend.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	@Profile("dev")
	CommandLineRunner createInitialUsers(UserService userService, ContentService contentService) {
		return args -> {
			try {
				userService.getByUsername("user1");
			} catch (Exception e) {
				for(int i = 1; i <= 25; i++) {
					User user = new User();
					user.setUsername("user" + i);
					user.setDisplayName("display" + i);
					user.setPassword("Kaan1234");
					userService.save(user);

					for(int j = 0; j < 20; j++) {
						ContentSubmitDto content = new ContentSubmitDto();
						content.setContent("test content (" + j + ") from user (" + i + ")");
						content.setAttachmentId((long) j);
						contentService.save(content, user);
					}
				}
			}
		};
	}

}
