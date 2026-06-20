package com.realestatebackend.dashboard.controller;

import com.realestatebackend.audit.service.AuditLogService; // 1. IMPORT THIS
import com.realestatebackend.dashboard.dto.DashboardKpiResponse;
import com.realestatebackend.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor // This creates the constructor for you
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class DashboardController {

    private final DashboardService dashboardService;
    private final AuditLogService auditLogService; // 2. ADD THIS PRIVATE FINAL FIELD

    @GetMapping("/kpis")
    public ResponseEntity<DashboardKpiResponse> getTopLevelKpis() {
        return ResponseEntity.ok(dashboardService.getTopLevelKpis());
    }

    // 3. NOW THIS METHOD WILL WORK
    @GetMapping("/recent-activity")
    public ResponseEntity<List<?>> getRecentActivity() {
        // Assuming your service returns a list of audit logs
        return ResponseEntity.ok(auditLogService.getEntityHistory(null)); // Placeholder for now
    }
}