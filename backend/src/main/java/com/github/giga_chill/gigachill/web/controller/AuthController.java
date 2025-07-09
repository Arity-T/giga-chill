package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.model.*;
import com.github.giga_chill.gigachill.exception.*;
import com.github.giga_chill.gigachill.web.info.UserInfo;
import com.github.giga_chill.gigachill.security.JwtService;
import com.github.giga_chill.gigachill.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;



@RestController
public class AuthController {
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Void> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        if (!userService.validate(request.login, request.password)) {
            throw new UnauthorizedException("Неверный логин или пароль");
        }

        String jwt = jwtService.generateToken(request.login);

        ResponseCookie cookie = ResponseCookie.from("token", jwt)
                .httpOnly(true)
                .secure(false) // TODO: false для localhost, true для прода
                .path("/")
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.noContent().build(); // 204
    }

    @PostMapping("/auth/register")
    public ResponseEntity<Void> register(@RequestBody AuthRequest request, HttpServletResponse response) {
        if (userService.userExistsById(request.login)) {
            throw new ConflictException("Пользователь с таким логином уже существует");
        }

        if (request.login == null || request.login.length() < 4 || request.password == null || request.password.length() < 4) {
            throw new BadRequestException("Логин и пароль должны быть не короче 4 символов");
        }

        userService.register(request.login, request.password, request.name);
        String jwt = jwtService.generateToken(request.login);

        ResponseCookie cookie = ResponseCookie.from("token", jwt)
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
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false) // TODO: true на проде
                .path("/")
                .sameSite("Strict")
                .maxAge(0)  // Удалить cookie
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfo> me(Authentication authentication) {
        var user = userService.findByLogin(authentication.getName());
        if (user.isEmpty()) {
            throw new UnauthorizedException("Пользователь не найден");
        }

        return ResponseEntity.ok(new UserInfo(user.get().getLogin(), user.get().getName(),
                user.get().getUserId().toString()));
    }

}