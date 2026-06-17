package com.realestatebackend.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Incoming payload to create a Block under a given Project.
 */
public record CreateBlockRequest(

        @NotNull(message = "projectId is required")
        UUID projectId,

        @NotBlank(message = "Block name is required")
        @Size(max = 100, message = "Block name must be at most 100 characters")
        String name
) {}