package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ParticipantInfo(
        @JsonProperty("login") String login,
        @JsonProperty("name") String name,
        @JsonProperty("id") String id,
        @JsonProperty("user_role") String userRole) {}
