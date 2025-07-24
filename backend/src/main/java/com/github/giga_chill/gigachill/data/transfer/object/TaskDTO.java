package com.github.giga_chill.gigachill.data.transfer.object;

import jakarta.annotation.Nullable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private UUID taskId;
    @Nullable private String title;
    @Nullable private String description;
    @Nullable private String status;
    @Nullable private String deadlineDatetime;
    @Nullable private String executorComment;
    @Nullable private String reviewerComment;
    @Nullable private UserDTO author;
    @Nullable private UserDTO executor;
}
