package com.realestatebackend.recovery.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record DefaulterDto(
        UUID customerId,
        String customerName,
        String customerPhone,
        UUID unitId,
        String unitNumber,
        String blockName,
        int overdueInstallmentsCount,
        BigDecimal totalOverdueAmount
) {

    // Hibernate Fallback 1: Catches the exact error you got (int, Long)
    public DefaulterDto(UUID customerId, String customerName, String customerPhone,
                        UUID unitId, String unitNumber, String blockName,
                        int overdueInstallmentsCount, Long totalOverdueAmount) {
        this(customerId, customerName, customerPhone, unitId, unitNumber, blockName,
                overdueInstallmentsCount,
                totalOverdueAmount != null ? BigDecimal.valueOf(totalOverdueAmount) : BigDecimal.ZERO);
    }

    // Hibernate Fallback 2: Just in case your DB dialect ignores the CAST(COUNT) and returns (Long, Long)
    public DefaulterDto(UUID customerId, String customerName, String customerPhone,
                        UUID unitId, String unitNumber, String blockName,
                        Long overdueInstallmentsCount, Long totalOverdueAmount) {
        this(customerId, customerName, customerPhone, unitId, unitNumber, blockName,
                overdueInstallmentsCount != null ? overdueInstallmentsCount.intValue() : 0,
                totalOverdueAmount != null ? BigDecimal.valueOf(totalOverdueAmount) : BigDecimal.ZERO);
    }

    // Hibernate Fallback 3: Just in case it returns (Long, BigDecimal)
    public DefaulterDto(UUID customerId, String customerName, String customerPhone,
                        UUID unitId, String unitNumber, String blockName,
                        Long overdueInstallmentsCount, BigDecimal totalOverdueAmount) {
        this(customerId, customerName, customerPhone, unitId, unitNumber, blockName,
                overdueInstallmentsCount != null ? overdueInstallmentsCount.intValue() : 0,
                totalOverdueAmount != null ? totalOverdueAmount : BigDecimal.ZERO);
    }
}