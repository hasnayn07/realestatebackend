package com.realestatebackend.document.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentResponse(
        UUID id,
        String fileName,
        String originalName,
        String fileType,
        Long fileSize,
        String documentCategory,
        UUID relatedEntityId,
        String relatedEntityType,
        String uploadedBy,
        LocalDateTime createdAt
) {
}