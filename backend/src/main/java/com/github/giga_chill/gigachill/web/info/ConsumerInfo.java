package com.github.giga_chill.gigachill.web.info;

import java.math.BigDecimal;

public record ConsumerInfo(
        String login, String name, String id, String user_role, BigDecimal balance) {}
