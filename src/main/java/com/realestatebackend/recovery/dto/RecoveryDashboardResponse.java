package com.realestatebackend.recovery.dto;

import java.math.BigDecimal;
import java.util.List;

public record RecoveryDashboardResponse(
        BigDecimal totalOutstandingAmount,
        BigDecimal totalOverdueAmount,
        BigDecimal dueThisMonthAmount,
        BigDecimal collectedThisMonthAmount,
        BigDecimal recoveryRatePercentage,
        List<DefaulterDto> topDefaulters
) {
}