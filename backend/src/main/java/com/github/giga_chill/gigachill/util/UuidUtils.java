package com.github.giga_chill.gigachill.util;

import com.github.giga_chill.gigachill.exception.BadRequestException;

import java.util.UUID;

public final class UuidUtils {
    private UUID safeUUID(String raw) {
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid UUID: " + raw);
        }
    }
}
