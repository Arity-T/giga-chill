package com.github.giga_chill.gigachill.model;

import java.util.Set;

public record UserInfo(String login, String name, String id, Set<Role> roles) {}
