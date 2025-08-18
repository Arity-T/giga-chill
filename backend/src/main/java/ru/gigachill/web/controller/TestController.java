package ru.gigachill.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.gigachill.service.TestService;
import ru.gigachill.web.api.TestUtilsApi;

@RestController
@RequiredArgsConstructor
@Profile("test")
public class TestController implements TestUtilsApi {
    private final TestService testService;

    @Override
    public ResponseEntity<Void> cleanup() {
        testService.cleanBD();
        return ResponseEntity.noContent().build();
    }
}
