package com.github.giga_chill.gigachill.controller;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(e.getMessage());
  }

    // DTO для ошибок
    public static class ErrorResponse {
      public String message;
      public ErrorResponse(String message) {
          this.message = message;
      }
    }
}
