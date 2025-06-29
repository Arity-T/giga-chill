package com.github.giga_chill.gigachill.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .body(new ErrorResponse(e.getMessage()));
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ErrorResponse> handleConflict(ConflictException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
              .body(new ErrorResponse(e.getMessage()));
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(new ErrorResponse(e.getMessage()));
  }

  // Фоллбек
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleOther(RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(new ErrorResponse("Something went wrong"));
  }

  // DTO для ошибок
  public static class ErrorResponse {
    public String message;
    public ErrorResponse(String message) {
        this.message = message;
    }
  }
}
