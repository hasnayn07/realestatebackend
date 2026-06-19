package com.realestatebackend.dealer.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record DealerPayableDto(
        UUID dealerId,
        String dealerName,
        String agencyName,
        long totalBookings,
        BigDecimal totalSalesVolume,
        BigDecimal commissionPayable
) {
    // Standard expected constructor
    public DealerPayableDto(UUID dealerId, String dealerName, String agencyName, Long totalBookings, BigDecimal totalSalesVolume) {
        this(dealerId, dealerName, agencyName,
                totalBookings != null ? totalBookings : 0L,
                totalSalesVolume != null ? totalSalesVolume : BigDecimal.ZERO,
                BigDecimal.ZERO); // Commission will be calculated in the service layer
    }

    // Hibernate Fallback 1: The exact error you got (Long, int)
    public DealerPayableDto(UUID dealerId, String dealerName, String agencyName, Long totalBookings, int totalSalesVolume) {
        this(dealerId, dealerName, agencyName,
                totalBookings != null ? totalBookings : 0L,
                BigDecimal.valueOf(totalSalesVolume),
                BigDecimal.ZERO);
    }

    // Hibernate Fallback 2: Just in case the DB dialect returns a Long
    public DealerPayableDto(UUID dealerId, String dealerName, String agencyName, Long totalBookings, Long totalSalesVolume) {
        this(dealerId, dealerName, agencyName,
                totalBookings != null ? totalBookings : 0L,
                totalSalesVolume != null ? BigDecimal.valueOf(totalSalesVolume) : BigDecimal.ZERO,
                BigDecimal.ZERO);
    }

    // Hibernate Fallback 3: Just in case it returns a Double
    public DealerPayableDto(UUID dealerId, String dealerName, String agencyName, Long totalBookings, Double totalSalesVolume) {
        this(dealerId, dealerName, agencyName,
                totalBookings != null ? totalBookings : 0L,
                totalSalesVolume != null ? BigDecimal.valueOf(totalSalesVolume) : BigDecimal.ZERO,
                BigDecimal.ZERO);
    }
}