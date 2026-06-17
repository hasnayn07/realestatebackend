package com.realestatebackend.inventory.dto;

import com.realestatebackend.inventory.entity.SizeUnit;
import com.realestatebackend.inventory.entity.UnitType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Incoming payload to create a Unit under a given Block.
 * Note: no 'status' field — the service always starts a unit as AVAILABLE,
 * so the client cannot set an arbitrary lifecycle state.
 */
public record CreateUnitRequest(

        @NotNull(message = "blockId is required")
        UUID blockId,

        @NotBlank(message = "Unit number is required")
        @Size(max = 40, message = "Unit number must be at most 40 characters")
        String unitNumber,

        @NotNull(message = "Unit type is required")
        UnitType unitType,

        @PositiveOrZero(message = "Size value cannot be negative")
        Double sizeValue,

        SizeUnit sizeUnit,

        @PositiveOrZero(message = "Listed price cannot be negative")
        BigDecimal listedPrice,

        // built-unit-only; leave null for plots
        @PositiveOrZero(message = "Bedrooms cannot be negative")
        Integer bedrooms,

        @PositiveOrZero(message = "Bathrooms cannot be negative")
        Integer bathrooms,

        @PositiveOrZero(message = "Floor cannot be negative")
        Integer floor
) {}