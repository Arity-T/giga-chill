package com.github.giga_chill.gigachill.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Participant {
    public String id;
    public String login;
    public String name;
    public String role;
}
