package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.exception.ConflictException;
import com.github.giga_chill.gigachill.exception.UnauthorizedException;
import com.github.giga_chill.gigachill.mapper.UserMapper;
import com.github.giga_chill.gigachill.security.JwtService;
import com.github.giga_chill.gigachill.web.api.model.LoginRequest;
import com.github.giga_chill.gigachill.web.api.model.RegisterRequest;
import com.github.giga_chill.gigachill.web.api.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Value("${server.is_prod:false}")
    private boolean isProd;

    public ResponseCookie login(LoginRequest loginRequest) {
        userService.validateLogin(loginRequest.getLogin());
        userService.validatePassword(loginRequest.getPassword());

        if (!userService.validate(loginRequest.getLogin(), loginRequest.getPassword())) {
            throw new UnauthorizedException("Invalid login or password");
        }

        String jwt = jwtService.generateToken(loginRequest.getLogin());

        return ResponseCookie.from("token", jwt)
                .httpOnly(true)
                .secure(isProd)
                .path("/")
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie register(RegisterRequest registerRequest) {
        userService.validateLogin(registerRequest.getLogin());
        userService.validatePassword(registerRequest.getPassword());

        if (userService.userExistsByLogin(registerRequest.getLogin())) {
            throw new ConflictException("A user with this login already exists");
        }

        userService.register(
                registerRequest.getLogin(),
                registerRequest.getPassword(),
                registerRequest.getName());
        String jwt = jwtService.generateToken(registerRequest.getLogin());

        return ResponseCookie.from("token", jwt)
                .httpOnly(true)
                .secure(isProd)
                .path("/")
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie logout() {
        return ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(isProd)
                .path("/")
                .sameSite("Strict")
                .maxAge(0) // Удалить cookie
                .build();
    }

    public User me(Authentication authentication) {
        return userMapper.toUser(userService.userAuthentication(authentication));
    }
}
