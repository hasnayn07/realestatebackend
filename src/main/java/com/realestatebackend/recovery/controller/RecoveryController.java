package com.realestatebackend.recovery.controller;

import com.realestatebackend.recovery.dto.RecoveryDashboardResponse;
import com.realestatebackend.recovery.service.RecoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recovery")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class RecoveryController {

    private final RecoveryService recoveryService;

    @GetMapping("/dashboard")
    public ResponseEntity<RecoveryDashboardResponse> getDashboardMetrics() {
        RecoveryDashboardResponse response = recoveryService.getDashboardMetrics();
        return ResponseEntity.ok(response);
    }
}