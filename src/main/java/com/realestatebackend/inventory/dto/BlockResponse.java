package com.realestatebackend.inventory.dto;

import java.util.UUID;

/**
 * Outgoing view of a Block, with the parent project flattened in.
 */
public record BlockResponse(
        UUID id,
        String name,
        UUID projectId,
        String projectName
) {}