package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumerInfo{
        @JsonProperty("login") private String login;
        @JsonProperty("name") private String name;
        @JsonProperty("id") private String id;
        @JsonProperty("user_role") private String userRole;
        @JsonProperty("balance") private BigDecimal balance;}
