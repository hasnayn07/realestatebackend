package com.realestatebackend.dealer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DealerResponse(
        UUID id,
        String name,
        String cnic,
        String agencyName,
        BigDecimal commissionPercentage,
        LocalDateTime createdAt
) {
}