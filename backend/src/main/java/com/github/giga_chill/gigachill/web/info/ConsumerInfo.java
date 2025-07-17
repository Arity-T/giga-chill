package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record ConsumerInfo(
        @JsonProperty("login") String login,
        @JsonProperty("name") String name,
        @JsonProperty("id") String id,
        @JsonProperty("user_role") String userRole,
        @JsonProperty("balance") BigDecimal balance) {}
