package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.exception.UnauthorizedException;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.repository.UserRepository;
import com.github.giga_chill.jooq.generated.tables.records.UsersRecord;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

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

    public Optional<UsersRecord> findById(String id) {
        return userRepository.findById(UUID.fromString(id));
    }

    public Optional<UsersRecord> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public boolean validate(String login, String password) {
        return userRepository.findByLogin(login)
        .map(user -> passwordEncoder.matches(password, user.getPasswordHash()))
        .orElse(false);
    }

    public boolean userExistsById(String userId) {
        return findById(userId).isPresent();
    }

    public boolean userExistsByLogin(String login) {
        return userRepository.findByLogin(login).isPresent();
    }

    public User userAuthentication(Authentication authentication) {
        var login = authentication.getName();
        if (!userExistsByLogin(login)) {
            throw new UnauthorizedException("User not found");
        }
        return usersRecordToUser(Objects.requireNonNull(findByLogin(login).orElse(null)));
    }

    private User usersRecordToUser(UsersRecord user){
        return new User(user.getUserId().toString(), user.getLogin(), user.getName());
    }

    public User getByLogin(String login){
        return usersRecordToUser(Objects.requireNonNull(findByLogin(login).orElse(null)));
    }

    /**
     * Проверяет, что все пользователи из списка userIds существуют в базе.
     * @param userIds список id пользователей
     * @return true, если все пользователи существуют, иначе false
     */
    public boolean allUsersExistByIds(List<String> userIds) {
        List<UUID> uuids = userIds.stream().map(UUID::fromString).toList();
        int count = userRepository.countByIds(uuids);
        return count == userIds.size();
    }
}
