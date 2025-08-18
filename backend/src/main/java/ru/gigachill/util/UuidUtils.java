package ru.gigachill.util;

import java.util.UUID;
import ru.gigachill.exception.BadRequestException;

public final class UuidUtils {
    public static UUID safeUUID(String raw) {
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid UUID: " + raw);
        }
    }
}
