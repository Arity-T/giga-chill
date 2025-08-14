package com.github.giga_chill.gigachill.model;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    private UUID id;
    private String login;
    private String name;

    public UserEntity(String login, String name) {
        this.id = UUID.randomUUID();
        this.login = login;
        this.name = name;
    }
}
