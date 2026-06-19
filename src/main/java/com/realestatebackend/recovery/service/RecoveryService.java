package com.realestatebackend.recovery.service;

import com.realestatebackend.recovery.dto.RecoveryDashboardResponse;

public interface RecoveryService {

    RecoveryDashboardResponse getDashboardMetrics();

    void sweepAndMarkOverdueInstallments();
}