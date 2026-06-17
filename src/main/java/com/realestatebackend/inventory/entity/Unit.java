package com.realestatebackend.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "units")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Unit {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    // Many units belong to one block. Owns the FK column "block_id".
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "block_id", nullable = false)
    private Block block;

    @Column(name = "unit_number", nullable = false, length = 40)
    private String unitNumber;          // "Plot 23", "Apt 4-B"

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_type", nullable = false, length = 20)
    private UnitType unitType;

    // --- size: number + unit, kept as the developer quotes it ---
    @Column(name = "size_value")
    private Double sizeValue;           // 5, 10, 1200

    @Enumerated(EnumType.STRING)
    @Column(name = "size_unit", length = 10)
    private SizeUnit sizeUnit;

    // --- money: BigDecimal, never double ---
    @Column(name = "listed_price", precision = 14, scale = 2)
    private BigDecimal listedPrice;

    // --- built-unit-only fields; null for plots ---
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer floor;

    // --- the lifecycle spine ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UnitStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (status == null) status = UnitStatus.AVAILABLE;   // safety net
    }
}