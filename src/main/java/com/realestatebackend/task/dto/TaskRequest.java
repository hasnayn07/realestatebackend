package com.realestatebackend.task.dto;

import com.realestatebackend.task.entity.TaskPriority;
import com.realestatebackend.task.entity.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record TaskRequest(
        @NotBlank(message = "Task title is required")
        String title,

        String description,

        @NotNull(message = "Due date is required")
        @FutureOrPresent(message = "Due date must be today or in the future")
        LocalDate dueDate,

        TaskStatus status,
        TaskPriority priority,

        @NotNull(message = "Assigned agent ID is required")
        UUID assignedToId,

        UUID relatedEntityId,
        String relatedEntityType
) {
}