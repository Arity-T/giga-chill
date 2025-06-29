package com.github.giga_chill.gigachill.security;

import com.github.giga_chill.gigachill.model.User;
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

  public void register(String login, String password) {
      users.put(login, new User(login, "Default name", password));
  }

  public boolean validate(String login, String password) {
      return users.containsKey(login) && users.get(login).password.equals(password);
  }

  public User getByLogin(String login) {
    return users.get(login);
}
}
