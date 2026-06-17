package com.realestatebackend.booking.dto;

import com.realestatebackend.booking.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID installmentId,
        BigDecimal amount,
        LocalDate paidDate,
        String proofImageUrl,
        PaymentStatus status,
        // verification audit
        UUID verifiedById,
        String verifiedByName,
        Instant verifiedAt,
        Instant createdAt
) {}