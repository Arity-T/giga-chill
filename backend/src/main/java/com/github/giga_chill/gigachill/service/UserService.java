package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.repository.UserRepository;
import com.github.giga_chill.jooq.generated.tables.records.UsersRecord;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Optional<UsersRecord> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public boolean validate(String login, String password) {
        return userRepository.findByLogin(login)
        .map(user -> passwordEncoder.matches(password, user.getPasswordHash()))
        .orElse(false);
    }

    public boolean userExists(String login) {
        return userRepository.findByLogin(login).isPresent();
    }
}
