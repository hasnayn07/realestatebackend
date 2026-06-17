package com.realestatebackend.booking.dto;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String fullName,
        String cnic,
        String phone,
        String address,
        Instant createdAt
) {}