package com.realestatebackend.dashboard.dto;

import java.math.BigDecimal;

public record DashboardKpiResponse(
        Long totalUnits,
        Long availableUnits,
        BigDecimal totalExpectedRevenue,
        BigDecimal totalCashCollected,
        Long activeDefaulters
) {
}