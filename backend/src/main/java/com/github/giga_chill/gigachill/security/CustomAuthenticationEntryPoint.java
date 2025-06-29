package com.github.giga_chill.gigachill.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void commence(HttpServletRequest request,
                       HttpServletResponse response,
                       AuthenticationException authException) throws IOException {

      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);

      var error = Map.of("message", "Something went wrong");
      objectMapper.writeValue(response.getOutputStream(), error);
  }
}
