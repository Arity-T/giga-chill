package ru.gigachill.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import ru.gigachill.service.AuthService;
import ru.gigachill.web.api.AuthApi;
import ru.gigachill.web.api.model.LoginRequest;
import ru.gigachill.web.api.model.RegisterRequest;
import ru.gigachill.web.api.model.User;

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
