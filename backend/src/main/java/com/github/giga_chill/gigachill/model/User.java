package com.github.giga_chill.gigachill.model;

import java.util.UUID;

public class User {
  public String id;
  public String login;
  public String name;
  public String password;

  public User(String login, String name, String password) {
      this.id = UUID.randomUUID().toString();
      this.login = login;
      this.name = name;
      this.password = password;
  }
}
