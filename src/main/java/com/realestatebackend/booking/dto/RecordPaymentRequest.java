package com.realestatebackend.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * The non-file fields of a payment submission. In the controller these arrive
 * as multipart form fields alongside the uploaded proof image (MultipartFile).
 */
public record RecordPaymentRequest(

        @NotNull(message = "installmentId is required")
        UUID installmentId,

        @NotNull(message = "amount is required")
        @Positive(message = "amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "paidDate is required")
        LocalDate paidDate
) {}