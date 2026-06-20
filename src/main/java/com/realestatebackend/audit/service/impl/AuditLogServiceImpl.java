package com.realestatebackend.audit.service.impl;

import com.realestatebackend.audit.dto.AuditLogResponse;
import com.realestatebackend.audit.entity.AuditAction;
import com.realestatebackend.audit.entity.AuditLog;
import com.realestatebackend.audit.repository.AuditLogRepository;
import com.realestatebackend.audit.service.AuditLogService;
import com.realestatebackend.auth.entity.User;
import com.realestatebackend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(AuditAction action, String entityName, UUID entityId, String description) {
        try {
            // Get the email of the user who is currently logged in
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            User activeUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Audit Error: Active user not found"));

            AuditLog logEntry = AuditLog.builder()
                    .action(action)
                    .entityName(entityName)
                    .entityId(entityId)
                    .description(description)
                    .performedBy(activeUser)
                    .build();

            auditLogRepository.save(logEntry);
            log.info("AUDIT [{}]: {} - {}", action, entityName, description);

        } catch (Exception e) {
            // We catch and log the error so a failed audit log doesn't crash the entire user request
            log.error("Failed to write audit log for {} {}: {}", entityName, entityId, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getEntityHistory(UUID entityId) {
        return auditLogRepository.findByEntityIdOrderByCreatedAtDesc(entityId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getAction(),
                log.getEntityName(),
                log.getEntityId(),
                log.getDescription(),
                log.getPerformedBy().getFullName(), // Ensure this matches your User entity
                log.getCreatedAt()
        );
    }
}