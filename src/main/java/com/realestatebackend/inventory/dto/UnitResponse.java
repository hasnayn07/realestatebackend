package com.realestatebackend.inventory.dto;

import com.realestatebackend.inventory.entity.SizeUnit;
import com.realestatebackend.inventory.entity.UnitStatus;
import com.realestatebackend.inventory.entity.UnitType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Outgoing view of a Unit, with block + project context flattened in
 * so the frontend can show "Saffron City / Block A / Plot 23" without
 * extra lookups.
 */
public record UnitResponse(
        UUID id,
        String unitNumber,
        UnitType unitType,
        Double sizeValue,
        SizeUnit sizeUnit,
        BigDecimal listedPrice,
        Integer bedrooms,
        Integer bathrooms,
        Integer floor,
        UnitStatus status,
        Instant createdAt,
        // flattened context
        UUID blockId,
        String blockName,
        UUID projectId,
        String projectName
) {}