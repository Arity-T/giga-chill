package com.github.giga_chill.gigachill.controller;

import com.github.giga_chill.gigachill.model.*;
import com.github.giga_chill.gigachill.exception.*;
import com.github.giga_chill.gigachill.security.JwtService;
import com.github.giga_chill.gigachill.security.InMemoryUserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@RestController
public class AuthController {
    private final JwtService jwtService;
    private final InMemoryUserService userService;
    private final Path ADMIN_DATA_SOURCE = Paths.get("src/main/resources/test_for_admin.txt");
    private final Path OWNER_DATA_SOURCE = Paths.get("src/main/resources/test_for_owner.txt");
    private final Path MEMBER_DATA_SOURCE = Paths.get("src/main/resources/test_for_member.txt");

    public AuthController(JwtService jwtService, InMemoryUserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Void> login(@RequestBody AuthRequest request, HttpServletResponse response) {
        if (!userService.validate(request.login, request.password)) {
            throw new UnauthorizedException("Неверный логин или пароль");
        }

        String jwt = jwtService.generateToken(request.login, userService.getByLogin(request.login).rolesToString());



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
            throw new ConflictException("Пользователь с таким логином уже существует");
        }

        if (request.login == null || request.login.length() < 4 || request.password == null || request.password.length() < 4) {
            throw new BadRequestException("Логин и пароль должны быть не короче 4 символов");
        }

        userService.register(request.login, request.password, request.name);
        String jwt = jwtService.generateToken(request.login, userService.getByLogin(request.login).rolesToString());

        System.out.println(userService.getByLogin(request.login).rolesToString());

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
        var user = userService.getByLogin(authentication.getName());
        if (user == null) {
            throw new UnauthorizedException("Пользователь не найден");
        }
//        System.out.println("Authorities: " + authentication.getAuthorities());
        return ResponseEntity.ok(new UserInfo(user.login, user.name, user.id, user.roles));
    }

    @GetMapping(path = "/admin", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_OWNER')")
    public ResponseEntity<String> getAdminData(Authentication authentication) throws IOException {

        System.out.println("Authorities: " + authentication.getAuthorities());
        if (!Files.exists(ADMIN_DATA_SOURCE) || !Files.isReadable(ADMIN_DATA_SOURCE)) {
            throw new NotFoundException("Файл не существует");
        }

        String content = Files.readString(ADMIN_DATA_SOURCE, StandardCharsets.UTF_8);
        System.out.println(content);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(content);
    }

    @GetMapping(path = "/owner", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ResponseEntity<String> getOwnerData(Authentication authentication) throws IOException {

        System.out.println("Authorities: " + authentication.getAuthorities());
        if (!Files.exists(OWNER_DATA_SOURCE) || !Files.isReadable(OWNER_DATA_SOURCE)) {
            throw new NotFoundException("Файл не существует");
        }

        String content = Files.readString(OWNER_DATA_SOURCE, StandardCharsets.UTF_8);
        System.out.println(content);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(content);
    }

    @GetMapping(path = "/member", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_OWNER', 'ROLE_MEMBER')")
    public ResponseEntity<String> getMemberData(Authentication authentication) throws IOException {

        System.out.println("Authorities: " + authentication.getAuthorities());
        if (!Files.exists(MEMBER_DATA_SOURCE) || !Files.isReadable(MEMBER_DATA_SOURCE)) {
            throw new NotFoundException("Файл не существует");
        }

        String content = Files.readString(MEMBER_DATA_SOURCE, StandardCharsets.UTF_8);
        System.out.println(content);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(content);
    }

}