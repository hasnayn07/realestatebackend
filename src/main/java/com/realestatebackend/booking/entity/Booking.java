package com.realestatebackend.booking.entity;

import com.realestatebackend.auth.entity.User;
import com.realestatebackend.inventory.entity.Unit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    // Who bought
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // What they bought (from the inventory module)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    // Which agent made the sale (a system user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private User agent;

    // --- money (negotiated, may differ from unit.listedPrice) ---
    @Column(name = "sale_price", nullable = false, precision = 14, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "down_payment", nullable = false, precision = 14, scale = 2)
    private BigDecimal downPayment;

    // --- plan parameters ---
    @Column(name = "number_of_installments", nullable = false)
    private Integer numberOfInstallments;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private InstallmentFrequency frequency;

    @Column(name = "installment_start_date", nullable = false)
    private LocalDate installmentStartDate;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private BookingStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (bookingDate == null) bookingDate = LocalDate.now();
        if (status == null) status = BookingStatus.ACTIVE;
    }
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id")
    private com.realestatebackend.dealer.entity.Dealer dealer;
}