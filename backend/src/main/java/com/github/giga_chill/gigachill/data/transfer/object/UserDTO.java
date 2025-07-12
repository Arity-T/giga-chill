package com.github.giga_chill.gigachill.data.transfer.object;

import java.util.UUID;

public record UserDTO(UUID id,
                      String login,
                      String name) {
}
