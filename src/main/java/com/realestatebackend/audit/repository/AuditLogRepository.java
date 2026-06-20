package com.realestatebackend.audit.repository;

import com.realestatebackend.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    // Fetch the history timeline for a specific entity, newest first
    List<AuditLog> findByEntityIdOrderByCreatedAtDesc(UUID entityId);
}