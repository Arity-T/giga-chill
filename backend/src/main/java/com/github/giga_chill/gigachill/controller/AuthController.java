package com.github.giga_chill.gigachill.controller;

import com.github.giga_chill.gigachill.model.*;
import com.github.giga_chill.gigachill.exception.*;
import com.github.giga_chill.gigachill.security.JwtService;
import com.github.giga_chill.gigachill.security.InMemoryUserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
public class AuthController {
    private final JwtService jwtService;
    private final InMemoryUserService userService;

    public AuthController(JwtService jwtService, InMemoryUserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        if (userService.validate(request.login, request.password)) {
            return new AuthResponse(jwtService.generateToken(request.login));
        } else {
            throw new UnauthorizedException("Something went wrong");
        }
    }

    @PostMapping("/auth/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        if (request.login == null || request.login.length() < 4 || request.password == null || request.password.length() < 4) {
            throw new BadRequestException("Something went wrong");
        }

        if (userService.userExists(request.login)) {
            throw new ConflictException("Something went wrong");
        }

        userService.register(request.login, request.password);
        var token = jwtService.generateToken(request.login);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token));
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