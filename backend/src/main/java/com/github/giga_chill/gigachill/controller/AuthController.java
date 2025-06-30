package com.github.giga_chill.gigachill.controller;

import com.github.giga_chill.gigachill.model.*;
import com.github.giga_chill.gigachill.exception.*;
import com.github.giga_chill.gigachill.security.JwtService;
import com.github.giga_chill.gigachill.security.InMemoryUserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
public class AuthController {
    private final JwtService jwtService;
    private final InMemoryUserService userService;

    public AuthController(JwtService jwtService, InMemoryUserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Void> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        if (!userService.validate(request.login, request.password)) {
            throw new UnauthorizedException("Something went wrong");
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
        if (userService.userExists(request.login)) {
            throw new ConflictException("Something went wrong");
        }

        if (request.login == null || request.login.length() < 4 || request.password == null || request.password.length() < 4) {
            throw new BadRequestException("Something went wrong");
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

    @GetMapping("/me")
    public ResponseEntity<UserInfo> me(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedException("Something went wrong");
        }

        var user = userService.getByLogin(authentication.getName());
        if (user == null) {
            throw new UnauthorizedException("Something went wrong");
        }

        return ResponseEntity.ok(new UserInfo(user.login, user.name, user.id));
    }
}