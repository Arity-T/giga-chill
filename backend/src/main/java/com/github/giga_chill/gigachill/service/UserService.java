package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.exception.BadRequestException;
import com.github.giga_chill.gigachill.exception.NotFoundException;
import com.github.giga_chill.gigachill.exception.UnauthorizedException;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.repository.UserRepository;
import com.github.giga_chill.jooq.generated.tables.records.UsersRecord;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[a-zA-Z0-9]{4,}$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]{8,}$");

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public void register(String login, String password, String name) {
        var user = new UsersRecord();
        user.setLogin(login);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setName(name);
        userRepository.save(user);
    }

    public Optional<UsersRecord> findById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<UsersRecord> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public boolean validate(String login, String password) {
        return userRepository
                .findByLogin(login)
                .map(user -> passwordEncoder.matches(password, user.getPasswordHash()))
                .orElse(false);
    }

    public boolean userExistsById(UUID userId) {
        return findById(userId).isPresent();
    }

    public boolean userExistsByLogin(String login) {
        return userRepository.findByLogin(login).isPresent();
    }

    public User userAuthentication(Authentication authentication) {
        var login = authentication.getName();
        return usersRecordToUser(
                findByLogin(login).orElseThrow(() -> new UnauthorizedException("User not found")));
    }

    public User getById(UUID id) {
        return usersRecordToUser(
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new NotFoundException("User with id " + id + " not found")));
    }

    private User usersRecordToUser(UsersRecord user) {
        return new User(user.getUserId(), user.getLogin(), user.getName());
    }

    public User getByLogin(String login) {
        return usersRecordToUser(
                findByLogin(login)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "User with login " + login + " not found")));
    }

    /**
     * Проверяет, что все пользователи из списка userIds существуют в базе.
     *
     * @param userIds список id пользователей
     * @return true, если все пользователи существуют, иначе false
     * @throws BadRequestException если хотя бы один id отсутствует в базе
     */
    public boolean allUsersExistByIds(List<UUID> userIds) {
        List<UUID> uuids = new java.util.ArrayList<>();
        for (UUID id : userIds) {
            try {
                uuids.add(id);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid user id format: " + id);
            }
        }
        int count = userRepository.countByIds(uuids);
        return count == userIds.size();
    }

    public void validateLogin(String login) {
        if (Objects.isNull(login) || !LOGIN_PATTERN.matcher(login).matches()) {
            if (Objects.isNull(login) || login.length() < 4) {
                throw new BadRequestException("Login must be at least 4 characters long");
            }
            throw new BadRequestException("Login can only contain Latin letters and digits");
        }
    }

    public void validatePassword(String password) {
        if (Objects.isNull(password) || !PASSWORD_PATTERN.matcher(password).matches()) {
            if (Objects.isNull(password) || password.length() < 8) {
                throw new BadRequestException("Password must be at least 8 characters long");
            }
            throw new BadRequestException(
                    "Password can only contain Latin letters, digits, and some special characters");
        }
    }
}
