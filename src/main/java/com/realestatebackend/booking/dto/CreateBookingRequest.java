package com.realestatebackend.booking.dto;

import com.realestatebackend.booking.entity.InstallmentFrequency;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Payload to create a booking. The service uses these parameters to
 * generate the installment schedule, so the numbers are validated strictly.
 */
public record CreateBookingRequest(

        @NotNull(message = "customerId is required")
        UUID customerId,

        @NotNull(message = "unitId is required")
        UUID unitId,

        // optional: the in-house agent who made the sale
        UUID agentId,

        @NotNull(message = "salePrice is required")
        @Positive(message = "salePrice must be greater than zero")
        BigDecimal salePrice,

        @NotNull(message = "downPayment is required")
        @PositiveOrZero(message = "downPayment cannot be negative")
        BigDecimal downPayment,

        @NotNull(message = "numberOfInstallments is required")
        @Positive(message = "numberOfInstallments must be at least 1")
        Integer numberOfInstallments,

        @NotNull(message = "frequency is required")
        InstallmentFrequency frequency,

        @NotNull(message = "installmentStartDate is required")
        LocalDate installmentStartDate
) {}