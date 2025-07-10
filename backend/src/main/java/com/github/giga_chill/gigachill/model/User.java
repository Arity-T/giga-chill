package com.github.giga_chill.gigachill.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    public UUID id;
    public String login;
    public String name;

    public User(String login, String name){
        this.id = UUID.randomUUID();
        this.login = login;
        this.name = name;
    }


}
