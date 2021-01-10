package com.sociapp.backend.auth;

import com.sociapp.backend.user.User;
import com.sociapp.backend.user.UserRepository;
import com.sociapp.backend.user.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    TokenRepository tokenRepository;

    public AuthResponse authenticate(Credentials credentials) {
        User userInDb = userRepository.findByUsername(credentials.getUsername());
        if(userInDb == null) throw new AuthException("Username is incorrect");

        boolean isPasswordMatching = passwordEncoder.matches(credentials.getPassword(), userInDb.getPassword());
        if(!isPasswordMatching) throw new AuthException("Password is incorrect");

        UserDto userDto = new UserDto(userInDb);
        String token = generateRandomToken();

        Token tokenEntity = new Token();
        tokenEntity.setToken(token);
        tokenEntity.setUser(userInDb);
        tokenRepository.save(tokenEntity);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUserDto(userDto);
        return response;
    }

    @Transactional
    public UserDetails getUserDetails(String token) {
        Optional<Token> optionalToken = tokenRepository.findById(token);
        return optionalToken.<UserDetails>map(Token::getUser).orElse(null);
    }

    public String generateRandomToken() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public void clearToken(String token) {
        tokenRepository.deleteById(token);
    }
}
