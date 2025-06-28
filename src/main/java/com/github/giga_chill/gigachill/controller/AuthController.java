package com.github.giga_chill.gigachill.controller;

import com.github.giga_chill.gigachill.model.*;
import com.github.giga_chill.gigachill.security.JwtService;
import com.github.giga_chill.gigachill.security.InMemoryUserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtService jwtService;
    private final InMemoryUserService userService;

    public AuthController(JwtService jwtService, InMemoryUserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        if (userService.validate(request.login, request.password)) {
            return new AuthResponse(jwtService.generateToken(request.login));
        } else {
            throw new RuntimeException("Неверный логин или пароль");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        if (userService.userExists(request.login)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);
        }

        userService.register(request.login, request.password);
        var token = jwtService.generateToken(request.login);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token));
    }

    @GetMapping("/me")
    public UserInfo me(Authentication authentication) {
        return new UserInfo(authentication.getName());
    }
}