package com.realestatebackend.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Incoming payload to create/update a Project.
 * Validation runs at the controller via @Valid, before any service logic.
 */
public record CreateProjectRequest(

        @NotBlank(message = "Project name is required")
        @Size(max = 150, message = "Project name must be at most 150 characters")
        String name,

        @Size(max = 100, message = "City must be at most 100 characters")
        String city,

        @Size(max = 500, message = "Description must be at most 500 characters")
        String description
) {}