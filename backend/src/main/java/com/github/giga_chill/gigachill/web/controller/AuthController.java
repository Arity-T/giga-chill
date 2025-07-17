package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.exception.*;
import com.github.giga_chill.gigachill.model.*;
import com.github.giga_chill.gigachill.security.JwtService;
import com.github.giga_chill.gigachill.service.UserService;
import com.github.giga_chill.gigachill.web.info.UserInfo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Void> login(
            @RequestBody AuthRequest request, HttpServletResponse response) {
        userService.validateLogin(request.login);
        userService.validatePassword(request.password);

        if (!userService.validate(request.login, request.password)) {
            throw new UnauthorizedException("Invalid login or password");
        }

        String jwt = jwtService.generateToken(request.login);

        var cookie =
                ResponseCookie.from("token", jwt)
                        .httpOnly(true)
                        .secure(false) // TODO: false для localhost, true для прода
                        .path("/")
                        .sameSite("Strict")
                        .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.noContent().build(); // 204
    }

    @PostMapping("/auth/register")
    public ResponseEntity<Void> register(
            @RequestBody AuthRequest request, HttpServletResponse response) {
        userService.validateLogin(request.login);
        userService.validatePassword(request.password);

        if (userService.userExistsByLogin(request.login)) {
            throw new ConflictException("A user with this login already exists");
        }

        userService.register(request.login, request.password, request.name);
        String jwt = jwtService.generateToken(request.login);

        var cookie =
                ResponseCookie.from("token", jwt)
                        .httpOnly(true)
                        .secure(false) // TODO: false для localhost, true для прода
                        .path("/")
                        .sameSite("Strict")
                        .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.noContent().build(); // 204
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Создаём cookie с таким же именем, но с пустым значением и сроком жизни 0
        var cookie =
                ResponseCookie.from("token", "")
                        .httpOnly(true)
                        .secure(false) // TODO: true на проде
                        .path("/")
                        .sameSite("Strict")
                        .maxAge(0) // Удалить cookie
                        .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfo> me(Authentication authentication) {
        var user = userService.findByLogin(authentication.getName());
        if (user.isEmpty()) {
            throw new UnauthorizedException("User not found");
        }

        return ResponseEntity.ok(
                new UserInfo(
                        user.get().getLogin(),
                        user.get().getName(),
                        user.get().getUserId().toString()));
    }
}
