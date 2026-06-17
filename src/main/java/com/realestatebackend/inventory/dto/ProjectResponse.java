package com.realestatebackend.inventory.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Outgoing view of a Project. Only fields the client should see.
 */
public record ProjectResponse(
        UUID id,
        String name,
        String city,
        String description,
        Instant createdAt
) {}