package com.realestatebackend.audit.service;

import com.realestatebackend.audit.dto.AuditLogResponse;
import com.realestatebackend.audit.entity.AuditAction;

import java.util.List;
import java.util.UUID;

public interface AuditLogService {

    // The core method you will call from your other modules
    void logAction(AuditAction action, String entityName, UUID entityId, String description);

    // The method the frontend will call to view the timeline
    List<AuditLogResponse> getEntityHistory(UUID entityId);
}