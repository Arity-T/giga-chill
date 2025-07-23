package com.github.giga_chill.gigachill.web.info;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo{
    @JsonProperty("login") private String login;
    @JsonProperty("name") private String name;
    @JsonProperty("id") private String id;}
