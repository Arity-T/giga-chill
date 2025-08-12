package com.github.giga_chill.gigachill.web.controller;

import com.github.giga_chill.gigachill.service.TestService;
import com.github.giga_chill.gigachill.web.api.DatabaseApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Profile("test")
public class TestController implements DatabaseApi {
    private final TestService testService;

    @Override
    public ResponseEntity<Void> testUtilsCleanupPost() {
        testService.cleanBD();
        return ResponseEntity.noContent().build();
    }
}
