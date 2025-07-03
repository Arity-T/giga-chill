package com.github.giga_chill.gigachill.service;

import com.github.giga_chill.gigachill.exception.UnauthorizedException;
import com.github.giga_chill.gigachill.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryUserService {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public InMemoryUserService() {
        users.put("admin", new User("admin", "Артём Тищенко", "1234"));
    }

    public boolean userExists(String login) {
        return users.containsKey(login);
    }

    public void register(String login, String password, String name) {
        users.put(login, new User(login, name, password));
    }

    public boolean validate(String login, String password) {
        return users.containsKey(login) && users.get(login).password.equals(password);
    }

    public User getByLogin(String login) {
        return users.get(login);
    }

    public User userAuthentication(Authentication authentication) {
      var login = authentication.getName();
      if (login == null) {
        throw new UnauthorizedException("Пользователь не найден");
      }
      return getByLogin(login);
    }
}
