package ru.gigachill.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.gigachill.exception.ConflictException;
import ru.gigachill.exception.UnauthorizedException;
import ru.gigachill.mapper.UserMapper;
import ru.gigachill.security.JwtService;
import ru.gigachill.web.api.model.LoginRequest;
import ru.gigachill.web.api.model.RegisterRequest;
import ru.gigachill.web.api.model.User;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Value("${server.https_cookies_only:false}")
    private boolean isSecureCookies;

    public ResponseCookie login(LoginRequest loginRequest) {
        userService.validateLogin(loginRequest.getLogin());
        userService.validatePassword(loginRequest.getPassword());

        if (!userService.validate(loginRequest.getLogin(), loginRequest.getPassword())) {
            throw new UnauthorizedException("Invalid login or password");
        }

        String jwt = jwtService.generateToken(loginRequest.getLogin());

        return ResponseCookie.from("token", jwt)
                .httpOnly(true)
                .secure(isSecureCookies)
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
                .secure(isSecureCookies)
                .path("/")
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie logout() {
        return ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(isSecureCookies)
                .path("/")
                .sameSite("Strict")
                .maxAge(0) // Удалить cookie
                .build();
    }

    public User me(Authentication authentication) {
        return userMapper.toUser(userService.userAuthentication(authentication));
    }
}
