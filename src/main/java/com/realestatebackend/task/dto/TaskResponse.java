package com.realestatebackend.task.dto;

import com.realestatebackend.task.entity.TaskPriority;
import com.realestatebackend.task.entity.TaskStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        LocalDate dueDate,
        TaskStatus status,
        TaskPriority priority,
        UUID assignedToId,
        String assignedToName,
        UUID relatedEntityId,
        String relatedEntityType,
        LocalDateTime createdAt
) {
}