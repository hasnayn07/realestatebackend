package com.realestatebackend.booking.dto;

import com.realestatebackend.booking.entity.InstallmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InstallmentResponse(
        UUID id,
        UUID bookingId,
        Integer installmentNumber,
        LocalDate dueDate,
        BigDecimal amountDue,
        BigDecimal amountPaid,
        BigDecimal remaining,        // amountDue - amountPaid, computed in the mapper
        InstallmentStatus status
) {}