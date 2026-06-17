package com.realestatebackend.booking.dto;

import com.realestatebackend.booking.entity.BookingStatus;
import com.realestatebackend.booking.entity.InstallmentFrequency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        // customer context
        UUID customerId,
        String customerName,
        String customerCnic,
        // unit context
        UUID unitId,
        String unitNumber,
        String projectName,
        // agent
        UUID agentId,
        String agentName,
        // money + plan
        BigDecimal salePrice,
        BigDecimal downPayment,
        Integer numberOfInstallments,
        InstallmentFrequency frequency,
        LocalDate installmentStartDate,
        LocalDate bookingDate,
        BookingStatus status,
        // derived
        BigDecimal totalOutstanding,
        // the generated schedule
        List<InstallmentResponse> installments
) {}