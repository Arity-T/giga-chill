package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test-utils")
@RequiredArgsConstructor
@Profile("test")
public class TestController {
    private final TestService testService;

    @PostMapping("/cleanup")
    ResponseEntity<Void> cleanDB(Authentication authentication) {
        testService.cleanBD();
        return ResponseEntity.noContent().build();
    }
}
