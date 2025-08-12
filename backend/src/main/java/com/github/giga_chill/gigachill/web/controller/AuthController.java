package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.service.AuthService;
import com.github.giga_chill.gigachill.web.api.AuthApi;
import com.github.giga_chill.gigachill.web.api.model.LoginRequest;
import com.github.giga_chill.gigachill.web.api.model.RegisterRequest;
import com.github.giga_chill.gigachill.web.api.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    private final AuthService authService;

    @Override
    public ResponseEntity<User> getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = authService.me(authentication);
        return ResponseEntity.ok(user);
    }

    @Override
    public ResponseEntity<Void> login(LoginRequest loginRequest) {
        var cookie = authService.login(loginRequest);
        return ResponseEntity.noContent().header("Set-Cookie", cookie.toString()).build();
    }

    @Override
    public ResponseEntity<Void> logout() {
        var cookie = authService.logout();
        return ResponseEntity.noContent().header("Set-Cookie", cookie.toString()).build();
    }

    @Override
    public ResponseEntity<Void> register(RegisterRequest registerRequest) {
        var cookie = authService.register(registerRequest);
        return ResponseEntity.noContent().header("Set-Cookie", cookie.toString()).build();
    }
}
