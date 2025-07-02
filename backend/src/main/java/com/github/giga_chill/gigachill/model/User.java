package com.github.giga_chill.gigachill.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

public class User implements UserDetails {
    public String id;
    public String login;
    public String name;
    public String password;

    public Set<Role> roles;

    public User(String login, String name, String password) {
        this.id = UUID.randomUUID().toString();
        this.login = login;
        this.name = name;
        this.password = password;
        this.roles = new HashSet<>();

        //Убрать
        if (login.equals("Admin") || login.equals("setAdmin")) {
            roles.add(Role.ROLE_ADMIN);
        } else if (login.equals("Owner") || login.equals("setOwner")) {
            roles.add(Role.ROLE_OWNER);
        } else {
            roles.add(Role.ROLE_PARTICIPANT);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    public Collection<String> rolesToString(){
        return roles.stream()
                .map(Role::name)           // или role -> role.name()
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
