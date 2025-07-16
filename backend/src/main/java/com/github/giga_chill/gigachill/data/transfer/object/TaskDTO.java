package com.github.giga_chill.gigachill.data.transfer.object;

import jakarta.annotation.Nullable;
import java.util.UUID;

public record TaskDTO(
        UUID taskId,
        @Nullable String title,
        @Nullable String description,
        @Nullable String status,
        @Nullable String deadlineDatetime,
        @Nullable UserDTO author,
        @Nullable UserDTO executor) {}
