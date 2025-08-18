package ru.gigachill.config;

import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.gigachill.properties.FrontendProperties;
import ru.gigachill.security.CustomAuthenticationEntryPoint;
import ru.gigachill.security.JwtFilter;

@Configuration
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final FrontendProperties frontendProperties;

    public SecurityConfig(
            JwtFilter jwtFilter,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            FrontendProperties frontendProperties) {
        this.jwtFilter = jwtFilter;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.frontendProperties = frontendProperties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable) // Отключаем CSRF
                .sessionManagement(
                        sess ->
                                sess.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS)) // Отключаем сессии
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(
                                                "/auth/login", "/auth/register", "/test-utils/**")
                                        .permitAll() // Разрешаем все запросы к этим эндпоинтам
                                        .anyRequest()
                                        .authenticated() // Для остальных запросов требуем
                        // аутентификацию
                        )
                .exceptionHandling(
                        ex -> ex.authenticationEntryPoint(customAuthenticationEntryPoint))
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class) // Добавляем JWT фильтр перед
                // UsernamePasswordAuthenticationFilter
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns(
                                frontendProperties
                                        .getOrigin()) // TODO: заменить на allowedOrigins с адресом
                        // фронта
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                        .allowCredentials(true);
            }
        };
    }
}
