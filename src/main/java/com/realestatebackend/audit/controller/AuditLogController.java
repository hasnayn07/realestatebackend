package com.realestatebackend.audit.controller;

import com.realestatebackend.audit.dto.AuditLogResponse;
import com.realestatebackend.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')") // Restrict history viewing to management
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/{entityId}")
    public ResponseEntity<List<AuditLogResponse>> getEntityHistory(@PathVariable UUID entityId) {
        return ResponseEntity.ok(auditLogService.getEntityHistory(entityId));
    }
}