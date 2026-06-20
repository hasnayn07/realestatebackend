package com.realestatebackend.audit.dto;

import com.realestatebackend.audit.entity.AuditAction;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        AuditAction action,
        String entityName,
        UUID entityId,
        String description,
        String performedBy,
        LocalDateTime createdAt
) {
}